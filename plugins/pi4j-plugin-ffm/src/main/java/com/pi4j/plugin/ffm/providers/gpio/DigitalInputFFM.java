package com.pi4j.plugin.ffm.providers.gpio;

import com.pi4j.context.Context;
import com.pi4j.exception.InitializeException;
import com.pi4j.exception.ShutdownException;
import com.pi4j.io.gpio.digital.*;
import com.pi4j.plugin.ffm.common.file.FileDescriptorNative;
import com.pi4j.plugin.ffm.common.file.FileFlag;
import com.pi4j.plugin.ffm.common.gpio.DetectedEvent;
import com.pi4j.plugin.ffm.common.gpio.PinEvent;
import com.pi4j.plugin.ffm.common.gpio.PinEventProcessing;
import com.pi4j.plugin.ffm.common.gpio.PinFlag;
import com.pi4j.plugin.ffm.common.gpio.enums.LineAttributeId;
import com.pi4j.plugin.ffm.common.gpio.structs.*;
import com.pi4j.plugin.ffm.common.ioctl.Command;
import com.pi4j.plugin.ffm.common.ioctl.IoctlNative;
import com.pi4j.plugin.ffm.common.poll.PollFlag;
import com.pi4j.plugin.ffm.common.poll.PollNative;
import com.pi4j.plugin.ffm.common.poll.structs.PollingData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.foreign.MemorySegment;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermissions;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class DigitalInputFFM extends DigitalInputBase implements DigitalInput {
    private static final Logger logger = LoggerFactory.getLogger(DigitalInputFFM.class);
    private static final IoctlNative ioctl = new IoctlNative();
    private static final FileDescriptorNative file = new FileDescriptorNative();
    private static final PollNative poll = new PollNative();

    private final String chipName;
    private final int pin;
    private final long debounce;
    private final PullResistance pull;
    private int chipFileDescriptor;

    private final ThreadFactory factory;
    // executor services for event watcher
    private final ExecutorService eventTaskProcessor;

    private boolean closed = false;

    public DigitalInputFFM(String chipName, DigitalInputProvider provider, DigitalInputConfig config) {
        super(provider, config);
        this.pin = config.address();
        this.chipName = "/dev/" + chipName;
        this.debounce = config.debounce();
        this.pull = config.pull();
        this.factory =  Thread.ofVirtual().name("ffm-input-event-detection-pin-", pin).factory();
        this.eventTaskProcessor = Executors.newSingleThreadExecutor(factory);
    }

    @Override
    public DigitalInput initialize(Context context) throws InitializeException {
        try {
            if (!deviceExists()) {
                throw new InitializeException("Device '" + chipName + "' does not exist.");
            }
            if (!canAccessDevice()) {
                var posix = Files.readAttributes(Path.of(chipName), PosixFileAttributes.class);
                logger.error("Inaccessible device: '{} {} {} {}'", PosixFilePermissions.toString(posix.permissions()), posix.owner().getName(), posix.group().getName(), chipName);
                logger.error("Please, read the documentation <link> to setup right permissions.");
                throw new InitializeException("Device '" + chipName + "' cannot be accessed with current user.");
            }
            logger.info("{}-{} - setting up DigitalInput Pin...", chipName, pin);
            logger.trace("{}-{} - opening device file.", chipName, pin);
            var fd = file.open(chipName, FileFlag.O_RDONLY | FileFlag.O_CLOEXEC);
            var lineInfo = new LineInfo(new byte[]{}, new byte[]{}, pin, 0, 0, new LineAttribute[]{});
            logger.trace("{}-{} - getting line info.", chipName, pin);
            lineInfo = ioctl.call(fd, Command.getGpioV2GetLineInfoIoctl(), lineInfo);
            if ((lineInfo.flags() & PinFlag.USED.getValue()) > 0) {
                shutdown(context());
                throw new InitializeException("Pin " + pin + " is in use");
            }
            logger.trace("{}-{} - DigitalInput Pin line info: {}", chipName, pin, lineInfo);
            var flags = PinFlag.INPUT.getValue() | PinFlag.EDGE_RISING.getValue() | PinFlag.EDGE_FALLING.getValue();
            var attributes = new ArrayList<LineConfigAttribute>();
            if (debounce > 0) {
                var debounceAttribute = new LineAttribute(LineAttributeId.GPIO_V2_LINE_ATTR_ID_DEBOUNCE.getValue(), 0, 0, (int) debounce);
                attributes.add(new LineConfigAttribute(debounceAttribute, pin));
            }
            flags |= switch (pull) {
                case OFF -> 0;
                case PULL_DOWN -> PinFlag.BIAS_PULL_DOWN.getValue();
                case PULL_UP -> PinFlag.BIAS_PULL_UP.getValue();
            };
            var lineConfig = new LineConfig(flags, attributes.size(), attributes.toArray(new LineConfigAttribute[0]));
            var lineRequest = new LineRequest(new int[]{pin}, ("pi4j." + getClass().getSimpleName()).getBytes(), lineConfig, 1, 0, 0);
            var result = ioctl.call(fd, Command.getGpioV2GetLineIoctl(), lineRequest);
            this.chipFileDescriptor = result.fd();

            var watcher = new EventWatcher(fd, PinEvent.BOTH, events -> {
                for (DetectedEvent detectedEvent : events) {
                    this.dispatch(new DigitalStateChangeEvent<DigitalInput>(this, DigitalState.getState(detectedEvent.pinEvent().getValue())));
                }
            }, 16);
            eventTaskProcessor.submit(watcher);

            file.close(fd);
            logger.info("{}-{} - DigitalInput Pin configured: {}", chipName, pin, result);
        } catch (IOException e) {
            logger.error("{}-{} - DigitalInput Pin Initialization error: {}", chipName, pin, e.getMessage());
            throw new InitializeException(e);
        }
        return this;
    }

    @Override
    public DigitalInput shutdown(Context context) throws ShutdownException {
        super.shutdown(context);
        logger.info("{}-{} - closing GPIO Pin.", chipName, pin);
        try {
            if (chipFileDescriptor > 0) {
                file.close(chipFileDescriptor);
            }
            //eventTaskProcessor.awaitTermination(1, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            this.closed = true;
            throw new ShutdownException(e);
        }
        this.closed = true;
        logger.info("{}-{} - GPIO Pin is closed. Recreate the pin object to reuse.", chipName, pin);
        return this;
    }

    @Override
    public DigitalState state() {
        checkClosed();
        logger.trace("{}-{} - reading GPIO Pin.", chipName, pin);
        var lineValues = new LineValues(0, 1);
        LineValues result;
        try {
            result = ioctl.call(chipFileDescriptor, Command.getGpioV2GetValuesIoctl(), lineValues);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        var state = DigitalState.getState(result.bits());
        logger.trace("{}-{} - GPIO Pin state is {}.", chipName, pin, state);
        return state;
    }

    /**
     * Checks if GPIO Pin is closed.
     */
    private void checkClosed() {
        if (closed) {
            throw new RuntimeException("Pin " + pin + " is closed");
        }
    }

    private boolean canAccessDevice() {
        return new File(chipName).canRead();
    }

    private boolean deviceExists() {
        return new File(chipName).exists();
    }

    /**
     * Internal class for watching the event on GPIO Pin.
     */
    private static class EventWatcher implements Runnable {
        private static final Logger logger = LoggerFactory.getLogger(EventWatcher.class);

        private final int fd;
        private final PinEvent pinEvent;
        private final PinEventProcessing eventProcessor;
        private final int eventBufferSize;
        private final Duration updatePeriod;

        private boolean stopWatching = false;

        /**
         * Constructs the EventWatcher
         *
         * @param pinEvent        event
         * @param eventProcessor  event processor
         * @param eventBufferSize event buffer size
         */
        EventWatcher(int fd, PinEvent pinEvent, PinEventProcessing eventProcessor, int eventBufferSize) {
            this.fd = fd;
            this.pinEvent = pinEvent;
            this.eventProcessor = eventProcessor;
            this.eventBufferSize = eventBufferSize;
            this.updatePeriod = Duration.ZERO;
        }

        /**
         * Constructs the EventWatcher
         *
         * @param pinEvent       event
         * @param eventProcessor event processor
         * @param updatePeriod   update period
         */
        EventWatcher(int fd, PinEvent pinEvent, PinEventProcessing eventProcessor, Duration updatePeriod) {
            this.fd = fd;
            this.pinEvent = pinEvent;
            this.eventProcessor = eventProcessor;
            this.eventBufferSize = 1;
            this.updatePeriod = updatePeriod;
        }

        @Override
        public void run() {
            var pollFd = new PollingData(fd, (short) (PollFlag.POLLIN | PollFlag.POLLERR), (short) 0);
            var eventSize = (int) LineEvent.LAYOUT.byteSize();
            var timestamp = Instant.now();
            List<DetectedEvent> eventList = new ArrayList<>();
            while (!stopWatching) {
                try {
                    // number of file descriptors is set to 1, since we are polling only one pin
                    // timeout is set to 25s for default
                    var retPollFd = poll.poll(pollFd, 1, updatePeriod.equals(Duration.ZERO) ? 25_000 : (int) updatePeriod.toMillis());
                    if (retPollFd == null) {
                        // timeout happened, process all left events, update timestamp
                        eventProcessor.process(eventList);
                        eventList.clear();
                        timestamp = Instant.now();
                        continue;
                    }
                    if ((retPollFd.revents() & (PollFlag.POLLIN)) != 0) {
                        // default minimum buffer size is 16 line events
                        // see https://elixir.bootlin.com/linux/latest/source/include/uapi/linux/gpio.h#L185
                        var buf = file.read(fd, new byte[16 * eventSize], 16 * eventSize);
                        var holder = new byte[eventSize];
                        for (int i = 0; i < 16 * LineEvent.LAYOUT.byteSize(); i += eventSize) {
                            // check if timestamp is 0, then there is no event present, we can skip
                            if (buf[i] == 0) {
                                continue;
                            }
                            // convert byte array of events to java object with heap memory segment
                            System.arraycopy(buf, i, holder, 0, eventSize);
                            var memoryBuffer = MemorySegment.ofArray(holder);
                            var event = LineEvent.createEmpty().from(memoryBuffer);
                            // process only interested events
                            if ((event.id() & this.pinEvent.getValue()) != 0) {
                                eventList.add(new DetectedEvent(event.timestampNs(), PinEvent.getByValue(event.id()), event.lineSeqno()));
                            }
                        }
                        if (eventList.size() >= eventBufferSize && updatePeriod.equals(Duration.ZERO)) {
                            // process by number of events
                            eventProcessor.process(eventList);
                            eventList.clear();
                        } else if (timestamp.plus(updatePeriod).isBefore(Instant.now())) {
                            // process by update period
                            eventProcessor.process(eventList);
                            eventList.clear();
                            timestamp = Instant.now();
                        }
                    }
                    if ((retPollFd.revents() & (PollFlag.POLLERR)) != 0) {
                        // internal error on polling
                        logger.error("Internal error during polling");
                        stopWatching();
                    }
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            }
        }

        /**
         * Stops event watcher, end the task.
         */
        public void stopWatching() {
            this.stopWatching = true;
        }

        /**
         * Checks if the event watcher is running.
         *
         * @return true if event watcher is running
         */
        public boolean isRunning() {
            return !this.stopWatching;
        }

    }
}

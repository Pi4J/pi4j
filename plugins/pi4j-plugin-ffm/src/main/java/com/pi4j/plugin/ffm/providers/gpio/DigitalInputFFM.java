package com.pi4j.plugin.ffm.providers.gpio;

import com.pi4j.context.Context;
import com.pi4j.exception.InitializeException;
import com.pi4j.exception.Pi4JException;
import com.pi4j.exception.ShutdownException;
import com.pi4j.io.gpio.digital.*;
import com.pi4j.plugin.ffm.common.PermissionHelper;
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

import java.io.IOException;
import java.lang.foreign.Arena;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermissions;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DigitalInputFFM extends DigitalInputBase implements DigitalInput {
    private static final Logger logger = LoggerFactory.getLogger(DigitalInputFFM.class);

    // Graceful period for event watcher to shut down
    // NOTE: this value is used for poll operation inside watcher thread and should be always half of the timeout
    private static final int EVENT_WATCHER_SHUTDOWN_TIMEOUT_MS = 200;

    private final IoctlNative ioctl = new IoctlNative();
    private final FileDescriptorNative file = new FileDescriptorNative();
    private final PollNative poll = new PollNative();

    private final String deviceName;
    private final int pin;
    private final long debounce;
    private final PullResistance pull;
    private int chipFileDescriptor;

    // executor services for event watcher
    private ExecutorService eventTaskProcessor;
    private EventWatcher watcher;

    private boolean closed = false;

    public DigitalInputFFM(String chipName, DigitalInputProvider provider, DigitalInputConfig config) {
        super(provider, config);
        this.pin = config.address();
        this.deviceName = "/dev/gpiochip" + config.busNumber();
        this.debounce = config.debounce();
        this.pull = config.pull();
        PermissionHelper.checkDevicePermissions(deviceName, config);
    }

    @Override
    public DigitalInput initialize(Context context) throws InitializeException {
        super.initialize(context);
        try {
            if (!canAccessDevice()) {
                var posix = Files.readAttributes(Path.of(deviceName), PosixFileAttributes.class);
                logger.error("Inaccessible device: '{} {} {} {}'", PosixFilePermissions.toString(posix.permissions()), posix.owner().getName(), posix.group().getName(), deviceName);
                logger.error("Please, read the documentation <link> to setup right permissions.");
                throw new InitializeException("Device '" + deviceName + "' cannot be accessed with current user.");
            }
            logger.info("{}-{} - setting up DigitalInput Pin...", deviceName, pin);
            logger.trace("{}-{} - opening device file.", deviceName, pin);
            var fd = file.open(deviceName, FileFlag.O_RDONLY | FileFlag.O_CLOEXEC);
            var lineInfo = new LineInfo(new byte[]{}, new byte[]{}, pin, 0, 0, new LineAttribute[]{});
            logger.trace("{}-{} - getting line info.", deviceName, pin);
            lineInfo = ioctl.call(fd, Command.getGpioV2GetLineInfoIoctl(), lineInfo);
            if ((lineInfo.flags() & PinFlag.USED.getValue()) > 0) {
                shutdown(context());
                throw new InitializeException("Pin " + pin + " is in use");
            }
            logger.trace("{}-{} - DigitalInput Pin line info: {}", deviceName, pin, lineInfo);
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

            file.close(fd);
            logger.info("{}-{} - DigitalInput Pin configured: {}", deviceName, pin, result);
        } catch (IOException e) {
            logger.error("{}-{} - DigitalInput Pin Initialization error: {}", deviceName, pin, e.getMessage());
            throw new InitializeException(e);
        }
        return this;
    }

    @Override
    public DigitalInput addListener(DigitalStateChangeListener... listener) {
        var factory =  Thread.ofVirtual().name(deviceName + "-event-detection-pin-", pin)
            .uncaughtExceptionHandler(((_, e) -> logger.error(e.getMessage(), e)))
            .factory();
        this.eventTaskProcessor = Executors.newSingleThreadExecutor(factory);
        this.watcher = new EventWatcher(chipFileDescriptor, PinEvent.BOTH, events -> {
            for (DetectedEvent detectedEvent : events) {
                this.dispatch(new DigitalStateChangeEvent<DigitalInput>(this, DigitalState.getState(detectedEvent.pinEvent().getValue())));
            }
        }, 16);
        eventTaskProcessor.submit(watcher);
        return super.addListener(listener);
    }

    @Override
    public DigitalInput shutdown(Context context) throws ShutdownException {
        super.shutdown(context);
        logger.info("{}-{} - closing GPIO Pin.", deviceName, pin);
        try {
            if (chipFileDescriptor > 0) {
                file.close(chipFileDescriptor);
            }
            if (watcher != null) {
                watcher.stopWatching();
                eventTaskProcessor.shutdown();
                if (!eventTaskProcessor.awaitTermination(EVENT_WATCHER_SHUTDOWN_TIMEOUT_MS, TimeUnit.MILLISECONDS)) {
                    eventTaskProcessor.shutdownNow();
                }
            }
        } catch (Exception e) {
            this.closed = true;
            throw new ShutdownException(e);
        }
        this.closed = true;
        logger.info("{}-{} - GPIO Pin is closed. Recreate the pin object to reuse.", deviceName, pin);
        return this;
    }

    @Override
    public DigitalState state() {
        checkClosed();
        logger.trace("{}-{} - reading GPIO Pin.", deviceName, pin);
        var lineValues = new LineValues(0, 1);
        LineValues result;
        try {
            result = ioctl.call(chipFileDescriptor, Command.getGpioV2GetValuesIoctl(), lineValues);
        } catch (Exception e) {
            throw new Pi4JException(e);
        }
        var state = DigitalState.getState(result.bits());
        logger.trace("{}-{} - GPIO Pin state is {}.", deviceName, pin, state);
        return state;
    }

    /**
     * Checks if GPIO Pin is closed.
     */
    private void checkClosed() {
        if (closed) {
            throw new Pi4JException("Pin " + pin + " is closed");
        }
    }

    private boolean canAccessDevice() {
        return file.access(deviceName, FileFlag.R_OK) == 0;
    }

    private boolean deviceExists() {
        return file.access(deviceName, FileFlag.F_OK) == 0;
    }

    /**
     * Internal class for watching the event on GPIO Pin.
     */
    private class EventWatcher implements Runnable {
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
                    var retPollFd = poll.poll(pollFd, 1, updatePeriod.equals(Duration.ZERO) ? EVENT_WATCHER_SHUTDOWN_TIMEOUT_MS / 2 : (int) updatePeriod.toMillis());
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
                            // convert byte array of events to java object with memory segment
                            System.arraycopy(buf, i, holder, 0, eventSize);
                            var memoryBuffer = Arena.ofAuto().allocate(LineEvent.LAYOUT);
                            memoryBuffer.asByteBuffer().put(holder);
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
                    throw new Pi4JException(e);
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

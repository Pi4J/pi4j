package com.pi4j.plugin.ffm.providers.gpio;

import com.pi4j.context.Context;
import com.pi4j.exception.InitializeException;
import com.pi4j.exception.Pi4JException;
import com.pi4j.exception.ShutdownException;
import com.pi4j.io.gpio.digital.*;
import com.pi4j.plugin.ffm.common.FFMPermissionHelper;
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
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class FFMDigitalInput extends DigitalInputBase implements DigitalInput {
    private static final Logger logger = LoggerFactory.getLogger(FFMDigitalInput.class);

    // Graceful period for event watcher to shut down
    // NOTE: this value is used for poll operation inside watcher thread and should be always half of the timeout
    private static final int EVENT_WATCHER_SHUTDOWN_TIMEOUT_MS = 200;

    private final IoctlNative ioctl = new IoctlNative();
    private final FileDescriptorNative file = new FileDescriptorNative();
    private final PollNative poll = new PollNative();

    private final String deviceName;
    private final int bcm;
    private final long debounce;
    private final PullResistance pull;
    private int chipFileDescriptor;

    // executor services for event watcher
    private ExecutorService eventTaskProcessor;
    private final List<EventWatcher> watchers = new ArrayList<>();
    private ThreadFactory threadFactory;

    private boolean closed = false;

    public FFMDigitalInput(String chipName, DigitalInputProvider provider, DigitalInputConfig config) {
        super(provider, config);
        this.bcm = config.bcm();
        this.deviceName = "/dev/gpiochip" + config.bus();
        this.debounce = (config.debounce() != null && config.debounce() >= 0) ? config.debounce() : 0;
        this.pull = config.pull();
        FFMPermissionHelper.checkDevicePermissions(deviceName, config);
    }

    @Override
    public DigitalInput initialize(Context context) throws InitializeException {
        super.initialize(context);
        try {
            if (!canAccessDevice()) {
                var posix = Files.readAttributes(Path.of(deviceName), PosixFileAttributes.class);
                logger.error("Inaccessible device: '{} {} {} {}'",
                    PosixFilePermissions.toString(posix.permissions()), posix.owner().getName(), posix.group().getName(), deviceName);
                logger.error("Please, read the documentation <link> to setup right permissions.");
                throw new InitializeException("Device '" + deviceName + "' cannot be accessed with current user.");
            }
            logger.info("{}-{} - setting up DigitalInput BCM...", deviceName, bcm);
            logger.trace("{}-{} - opening device file.", deviceName, bcm);
            var fd = file.open(deviceName, FileFlag.O_RDONLY | FileFlag.O_CLOEXEC);
            var lineInfo = new LineInfo(new byte[]{}, new byte[]{}, bcm, 0, 0, new LineAttribute[]{});
            logger.trace("{}-{} - getting line info.", deviceName, bcm);
            lineInfo = ioctl.call(fd, Command.getGpioV2GetLineInfoIoctl(), lineInfo);
            if ((lineInfo.flags() & PinFlag.USED.getValue()) > 0) {
                this.shutdownInternal(context());
                throw new InitializeException("BCM " + bcm + " is in use");
            }
            logger.trace("{}-{} - DigitalInput BCM line info: {}", deviceName, bcm, lineInfo);
            var flags = PinFlag.INPUT.getValue() | PinFlag.EDGE_RISING.getValue() | PinFlag.EDGE_FALLING.getValue();
            var attributes = new ArrayList<LineConfigAttribute>();
            if (debounce > 0) {
                // check conversion from ms to ns
                if (debounce * 1000 > Integer.MAX_VALUE) {
                    throw new InitializeException("Debounce value of " + debounce + " is too large");
                }
                var debounceAttribute = new LineAttribute(LineAttributeId.GPIO_V2_LINE_ATTR_ID_DEBOUNCE.getValue(), 0, 0, (int) debounce * 1000);
                attributes.add(new LineConfigAttribute(debounceAttribute, 1L));
            }
            flags |= switch (pull) {
                case OFF -> 0;
                case PULL_DOWN -> PinFlag.BIAS_PULL_DOWN.getValue();
                case PULL_UP -> PinFlag.BIAS_PULL_UP.getValue();
            };
            var lineConfig = new LineConfig(flags, attributes.size(), attributes.toArray(new LineConfigAttribute[0]));
            var lineRequest = new LineRequest(new int[]{bcm}, ("pi4j." + getClass().getSimpleName()).getBytes(), lineConfig, 1, 0, 0);
            var result = ioctl.call(fd, Command.getGpioV2GetLineIoctl(), lineRequest);
            this.chipFileDescriptor = result.fd();

            file.close(fd);
            logger.info("{}-{} - DigitalInput BCM configured: {}", deviceName, bcm, result);
        } catch (IOException e) {
            logger.error("{}-{} - DigitalInput BCM Initialization error: {}", deviceName, bcm, e.getMessage());
            throw new InitializeException(e);
        }
        return this;
    }

    @Override
    public DigitalInput addListener(DigitalStateChangeListener... listener) {
        logger.trace("{}-{} - Adding new listener", deviceName, bcm);
        if (threadFactory == null) {
            this.threadFactory = Thread.ofPlatform().name(deviceName + "-event-detection-pin-", bcm)
                .daemon(true)
                .uncaughtExceptionHandler(((_, e) -> logger.error(e.getMessage(), e)))
                .factory();
            this.eventTaskProcessor = Executors.newCachedThreadPool(threadFactory);
        }
        var watcher = new EventWatcher(chipFileDescriptor, PinEvent.BOTH, events -> {
            for (DetectedEvent detectedEvent : events) {
                var state = switch (detectedEvent.pinEvent()) {
                    case RISING -> DigitalState.HIGH;
                    case FALLING -> DigitalState.LOW;
                    default -> DigitalState.UNKNOWN;
                };
                this.dispatch(new DigitalStateChangeEvent<DigitalInput>(this, state));
            }
        });
        watchers.add(watcher);
        eventTaskProcessor.submit(watcher);
        logger.trace("{}-{} - New listener added", deviceName, bcm);
        return super.addListener(listener);
    }

    @Override
    public DigitalInput shutdownInternal(Context context) throws ShutdownException {
        super.shutdownInternal(context);
        logger.info("{}-{} - closing GPIO BCM.", deviceName, bcm);
        try {
            logger.trace("{}-{} - Stopping event watchers", deviceName, bcm);
            for (EventWatcher watcher : watchers) {
                watcher.stopWatching();
            }
            if (!watchers.isEmpty()) {
                logger.trace("{}-{} - Gracefully shutting down event processor", deviceName, bcm);
                eventTaskProcessor.shutdown();
                if (!eventTaskProcessor.awaitTermination(EVENT_WATCHER_SHUTDOWN_TIMEOUT_MS, TimeUnit.MILLISECONDS)) {
                    logger.trace("{}-{} - Timeout when shutting down event processor, halting it", deviceName, bcm);
                    eventTaskProcessor.shutdownNow();
                }
            }
        } catch (Exception e) {
            this.closed = true;
            throw new ShutdownException(e);
        } finally {
            if (chipFileDescriptor > 0) {
                logger.trace("{}-{} - closing GPIO file descriptor '{}'.", deviceName, bcm, chipFileDescriptor);
                file.close(chipFileDescriptor);
            }
        }
        this.closed = true;
        logger.info("{}-{} - GPIO BCM is closed.", deviceName, bcm);
        return this;
    }

    @Override
    public DigitalState state() {
        checkClosed();
        logger.trace("{}-{} - reading GPIO BCM.", deviceName, bcm);
        var lineValues = new LineValues(0, 1);
        LineValues result;
        try {
            result = ioctl.call(chipFileDescriptor, Command.getGpioV2GetValuesIoctl(), lineValues);
        } catch (Exception e) {
            throw new Pi4JException(e);
        }
        var state = DigitalState.getState(result.bits());
        logger.trace("{}-{} - GPIO BCM state is {}.", deviceName, bcm, state);
        return state;
    }

    /**
     * Checks if GPIO Pin is closed.
     */
    private void checkClosed() {
        if (closed) {
            throw new Pi4JException("BCM " + bcm + " is closed");
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
        private final long debounceNs;

        private boolean stopWatching = false;

        /**
         * Constructs the EventWatcher
         *
         * @param pinEvent       event
         * @param eventProcessor event processor
         */
        EventWatcher(int fd, PinEvent pinEvent, PinEventProcessing eventProcessor) {
            this.fd = fd;
            this.pinEvent = pinEvent;
            this.eventProcessor = eventProcessor;
            // Convert microseconds to nanoseconds for software debounce
            this.debounceNs = debounce * 1000L;
        }

        @Override
        public void run() {
            var eventSize = (int) LineEvent.LAYOUT.byteSize();
            var timestamp = Instant.now();
            List<DetectedEvent> eventList = new ArrayList<>();
            DetectedEvent lastDebouncedEvent = null;
            PinEvent lastDebouncedState = null;
            long lastEventReceivedTimeNs = 0; // Track when we last received an event (using System.nanoTime)
            logger.trace("{} - Start polling GPIO data on BCM {} at {}",
                Thread.currentThread().getName(), bcm, timestamp);
            while (!stopWatching) {
                try {
                    // PollingData structure need to be recreated each time, because poll does not erase revents between calls
                    var pollData = new PollingData(fd, (short) (PollFlag.POLLIN | PollFlag.POLLPRI), (short) 0);
                    // number of file descriptors is set to 1, since we are polling only one pin
                    // timeout is set to EVENT_WATCHER_SHUTDOWN_TIMEOUT_MS / 2 to make sure we can gracefully shut down in a main thread.
                    pollData = poll.poll(pollData, 1, EVENT_WATCHER_SHUTDOWN_TIMEOUT_MS / 2);
                    if (pollData == null) {
                        var duration = timestamp.until(Instant.now()).toMillis();
                        logger.trace("{} - No events detected on BCM {}: polling timeout at {} (took {}ms)",
                            Thread.currentThread().getName(), bcm, timestamp, duration);
                        // Check if there's a pending debounced event that should be dispatched
                        if (lastDebouncedEvent != null && debounceNs > 0 && lastEventReceivedTimeNs > 0) {
                            long currentTimeNs = System.nanoTime();
                            long timeSinceLastEventNs = currentTimeNs - lastEventReceivedTimeNs;
                            if (timeSinceLastEventNs >= debounceNs) {
                                logger.trace("{} - Dispatching pending debounced event on BCM {} after timeout ({}ns >= {}ns)",
                                    Thread.currentThread().getName(), bcm, timeSinceLastEventNs, debounceNs);
                                eventList.add(lastDebouncedEvent);
                                eventProcessor.process(eventList);
                                eventList.clear();
                                lastDebouncedEvent = null;
                                lastEventReceivedTimeNs = 0;
                            }
                        }
                        timestamp = Instant.now();
                        continue;
                    }
                    if ((pollData.revents() & (PollFlag.POLLERR | PollFlag.POLLHUP | PollFlag.POLLNVAL)) != 0) {
                        // internal error on polling
                        logger.error("{} - Internal error during polling on BCM {}. Last polling data: {}",
                            Thread.currentThread().getName(), bcm, pollData);
                        stopWatching();
                        continue;
                    }
                    if ((pollData.revents() & (PollFlag.POLLIN | PollFlag.POLLPRI)) != 0) {
                        // default minimum buffer size is 16 line events
                        // see https://elixir.bootlin.com/linux/latest/source/include/uapi/linux/gpio.h#L185
                        var buf = file.read(fd, new byte[16 * eventSize], 16 * eventSize);
                        var holder = new byte[eventSize];
                        for (int i = 0; i < 16 * LineEvent.LAYOUT.byteSize(); i += eventSize) {
                            // check if timestampInNanos is 0, then there is no event present, we can skip
                            if (buf[i] == 0) {
                                continue;
                            }
                            // convert byte array of events to java object with memory segment
                            System.arraycopy(buf, i, holder, 0, eventSize);
                            var memoryBuffer = Arena.ofAuto().allocate(LineEvent.LAYOUT);
                            memoryBuffer.asByteBuffer().put(holder);
                            var event = LineEvent.createEmpty().from(memoryBuffer);
                            logger.trace("{} - Detected new event on BCM {}: {}",
                                Thread.currentThread().getName(), bcm, event);
                            // process only interested events
                            if ((event.id() & this.pinEvent.getValue()) != 0) {
                                var pinEvent = PinEvent.getByValue(event.id());
                                logger.trace("{} - Processing event on BCM {}: {}",
                                    Thread.currentThread().getName(), bcm, pinEvent);

                                DetectedEvent detectedEvent = new DetectedEvent(event.timestampNs(), pinEvent, event.lineSeqno());

                                // Apply software debounce if configured
                                if (debounceNs > 0) {
                                    long currentTimeNs = System.nanoTime();
                                    if (lastDebouncedEvent == null) {
                                        // First event, start debounce period
                                        lastDebouncedEvent = detectedEvent;
                                        lastDebouncedState = pinEvent;
                                        lastEventReceivedTimeNs = currentTimeNs;
                                        logger.trace("{} - Starting debounce period on BCM {} for {}",
                                            Thread.currentThread().getName(), bcm, pinEvent);
                                    } else {
                                        // Check if enough time has passed since last event (using kernel timestamps)
                                        long timeSinceLastEventNs = detectedEvent.timestampInNanos() - lastDebouncedEvent.timestampInNanos();

                                        if (timeSinceLastEventNs < debounceNs) {
                                            // Event within debounce period - update to latest event and reset timer
                                            logger.trace("{} - Event on BCM {} within debounce period ({}ns < {}ns), updating to latest",
                                                Thread.currentThread().getName(), bcm, timeSinceLastEventNs, debounceNs);
                                            lastDebouncedEvent = detectedEvent;
                                            lastDebouncedState = pinEvent;
                                            lastEventReceivedTimeNs = currentTimeNs;
                                        } else {
                                            // Debounce period passed - dispatch previous event and start new debounce
                                            logger.trace("{} - Debounce period passed on BCM {} ({}ns >= {}ns), dispatching event",
                                                Thread.currentThread().getName(), bcm, timeSinceLastEventNs, debounceNs);
                                            // Only dispatch if state actually changed
                                            if (lastDebouncedState != null) {
                                                eventList.add(lastDebouncedEvent);
                                            }
                                            lastDebouncedEvent = detectedEvent;
                                            lastDebouncedState = pinEvent;
                                            lastEventReceivedTimeNs = currentTimeNs;
                                        }
                                    }
                                } else {
                                    // No debounce configured - add event directly
                                    eventList.add(detectedEvent);
                                }
                            }
                        }
                        logger.trace("{} - Total events on BCM {}: {}",
                            Thread.currentThread().getName(), bcm, eventList.size());
                        // process events that have passed debounce
                        if (!eventList.isEmpty()) {
                            eventProcessor.process(eventList);
                            eventList.clear();
                        }
                        logger.trace("{} - Total processing on BCM {} took {}ms",
                            Thread.currentThread().getName(), bcm, timestamp.until(Instant.now()).toMillis());
                        timestamp = Instant.now();
                    }
                } catch (Throwable e) {
                    logger.error("{} - Error while polling pin on BCM {}",
                        Thread.currentThread().getName(), bcm, e);
                    throw new Pi4JException(e);
                }
            }
        }

        /**
         * Stops event watcher, end the task.
         */
        public synchronized void stopWatching() {
            this.stopWatching = true;
        }

        /**
         * Checks if the event watcher is running.
         *
         * @return true if event watcher is running
         */
        public synchronized boolean isRunning() {
            return !this.stopWatching;
        }

    }
}

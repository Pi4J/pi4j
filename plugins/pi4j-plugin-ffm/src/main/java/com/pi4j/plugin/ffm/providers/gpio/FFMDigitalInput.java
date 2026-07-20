package com.pi4j.plugin.ffm.providers.gpio;

import com.pi4j.context.Context;
import com.pi4j.exception.InitializeException;
import com.pi4j.exception.Pi4JException;
import com.pi4j.exception.ShutdownException;
import com.pi4j.io.gpio.digital.*;
import com.pi4j.plugin.ffm.common.FFMPermissionHelper;
import com.pi4j.plugin.ffm.common.gpio.DetectedEvent;
import com.pi4j.plugin.ffm.common.gpio.PinEvent;
import com.pi4j.plugin.ffm.common.gpio.PinEventProcessing;
import com.pi4j.plugin.ffm.common.gpio.PinFlag;
import com.pi4j.plugin.ffm.common.gpio.enums.LineAttributeId;
import com.pi4j.plugin.ffm.common.gpio.structs.*;
import com.pi4j.plugin.ffm.common.poll.PollFlag;
import com.pi4j.plugin.ffm.common.poll.PollNative;
import com.pi4j.plugin.ffm.common.poll.structs.PollingData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.foreign.Arena;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * Native {@link DigitalInput} implementation for the FFM backend. Requests a single GPIO line from a
 * {@code /dev/gpiochipN} character device as an input via the GPIO v2 character-device ioctl
 * ({@code GPIO_V2_GET_LINE_IOCTL}), reads its level with {@code GPIO_V2_LINE_GET_VALUES_IOCTL}, and
 * watches for edge events by polling the resulting line file descriptor. Configured bias (pull
 * up/down) and optional debounce are applied through GPIO v2 line attributes.
 * <p>
 * The pin can be atomically reconfigured as a digital output at runtime via {@link #reconfigure()}.
 */
public class FFMDigitalInput extends DigitalInputBase implements DigitalInput {
    private static final Logger logger = LoggerFactory.getLogger(FFMDigitalInput.class);

    // Graceful period for event watcher to shut down.
    // NOTE: used for poll() inside watcher thread — always keep at half of the total timeout.
    private static final int EVENT_WATCHER_SHUTDOWN_TIMEOUT_MS = 200;

    // Maximum graceful-await cycles before giving up on a stuck watcher thread.
    private static final int EVENT_WATCHER_SHUTDOWN_MAX_ATTEMPTS = 5;

    private final FFMGpioLine line;
    private final PollNative poll = new PollNative();

    private final long debounce;
    private final PullResistance pull;

    private ExecutorService eventTaskProcessor;
    private final List<EventWatcher> watchers = new ArrayList<>();
    private ThreadFactory threadFactory;

    /**
     * Creates a digital input bound to a GPIO line. Resolves the target device path
     * ({@code /dev/gpiochip} + the configured bus number), captures the BCM line offset, pull
     * resistance and debounce period from the configuration, and verifies that the current user has
     * the required permissions on the device file. The line itself is not requested until
     * {@link #initialize(Context)} is called.
     *
     * @param provider the {@link DigitalInputProvider} that created this instance
     * @param config   the {@link DigitalInputConfig} supplying the BCM line offset, bus number,
     *                 pull resistance and debounce period
     */
    public FFMDigitalInput(DigitalInputProvider provider, DigitalInputConfig config) {
        super(provider, config);
        this.line = new FFMGpioLine(config.bcm(), config.bus());
        this.debounce = (config.debounce() != null && config.debounce() >= 0) ? config.debounce() : 0;
        this.pull = config.pull();
        FFMPermissionHelper.checkDevicePermissions(line.deviceName, config);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Opens the GPIO chip device read-only, reads the line info to ensure the BCM line is not already
     * in use, then requests it as an input via {@code GPIO_V2_GET_LINE_IOCTL} with both rising- and
     * falling-edge detection enabled. The configured pull resistance is mapped to the matching
     * {@code GPIO_V2_LINE_FLAG_BIAS_*} flag, and a non-zero debounce period is applied as a
     * {@code GPIO_V2_LINE_ATTR_ID_DEBOUNCE} line attribute.
     *
     * @throws InitializeException if the device cannot be accessed, the line is already in use, the
     *                             debounce value overflows the kernel's nanosecond field, or a native
     *                             {@code ioctl}/open call fails
     */
    @Override
    public DigitalInput initialize(Context context) throws InitializeException {
        super.initialize(context);
        var flags = PinFlag.INPUT.getValue() | PinFlag.EDGE_RISING.getValue() | PinFlag.EDGE_FALLING.getValue();
        var attributes = getLineConfigAttributes();
        flags |= switch (pull) {
            case OFF -> 0;
            case PULL_DOWN -> PinFlag.BIAS_PULL_DOWN.getValue();
            case PULL_UP -> PinFlag.BIAS_PULL_UP.getValue();
        };
        try {
            line.openAndRequest(flags, attributes, getClass().getSimpleName());
        } catch (InitializeException e) {
            logger.error("{}-{} - DigitalInput BCM Initialization error: {}", line.deviceName, line.bcm, e.getMessage());
            throw e;
        }
        logger.info("{}-{} - DigitalInput BCM configured.", line.deviceName, line.bcm);
        return this;
    }

    private ArrayList<LineConfigAttribute> getLineConfigAttributes() {
        var attributes = new ArrayList<LineConfigAttribute>();
        if (debounce > 0) {
            if (debounce * 1000 > Integer.MAX_VALUE) {
                throw new InitializeException("Debounce value of " + debounce + " is too large");
            }
            var debounceAttribute = new LineAttribute(
                LineAttributeId.GPIO_V2_LINE_ATTR_ID_DEBOUNCE.getValue(), 0, 0, (int) debounce * 1000);
            attributes.add(new LineConfigAttribute(debounceAttribute, 1L << line.bcm));
        }
        return attributes;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Starts a dedicated daemon {@link EventWatcher} thread that blocks in a native {@code poll()} on
     * the requested line file descriptor and dispatches a {@link DigitalStateChangeEvent} for every
     * detected rising (HIGH) or falling (LOW) edge. The first call lazily creates the watcher thread
     * factory and executor; each call adds a new watcher.
     */
    @Override
    public DigitalInput addListener(DigitalStateChangeListener... listener) {
        logger.trace("{}-{} - Adding new listener", line.deviceName, line.bcm);
        if (threadFactory == null) {
            this.threadFactory = Thread.ofPlatform()
                .name(line.deviceName + "-event-detection-pin-", line.bcm)
                .daemon(true)
                .uncaughtExceptionHandler((_, e) -> logger.error(e.getMessage(), e))
                .factory();
            this.eventTaskProcessor = Executors.newCachedThreadPool(threadFactory);
        }
        var watcher = new EventWatcher(line.chipFileDescriptor, PinEvent.BOTH, events -> {
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
        // add listener first to avoid race with event dispatch
        super.addListener(listener);
        eventTaskProcessor.submit(watcher);
        logger.trace("{}-{} - New listener added", line.deviceName, line.bcm);
        return this;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Signals all running {@link EventWatcher} threads to stop and discards them before delegating
     * removal of the listeners to the superclass.
     */
    @Override
    public DigitalInput removeListener(DigitalStateChangeListener... listeners) {
        for (EventWatcher watcher : watchers) {
            watcher.stopWatching();
        }
        watchers.clear();
        return super.removeListener(listeners);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Stops all event watchers, waits for their threads to finish their current native {@code poll()}
     * cycle, then delegates to {@link FFMGpioLine#close()} to release the GPIO line.
     *
     * @throws ShutdownException if waiting for the watcher threads or closing native resources fails
     */
    @Override
    public DigitalInput shutdownInternal(Context context) throws ShutdownException {
        super.shutdownInternal(context);
        logger.info("{}-{} - closing GPIO BCM.", line.deviceName, line.bcm);
        try {
            logger.trace("{}-{} - Stopping event watchers", line.deviceName, line.bcm);
            for (EventWatcher watcher : watchers) {
                watcher.stopWatching();
            }
            watchers.clear();
            // Await the executor whenever it was ever created. removeListener() stops its watcher
            // and clears the list without waiting for the thread to leave its native poll(); that
            // thread still holds the line fd. Closing the fd without waiting would leave the GPIO
            // line flagged USED on the next create attempt.
            if (eventTaskProcessor != null) {
                logger.trace("{}-{} - Gracefully shutting down event processor", line.deviceName, line.bcm);
                eventTaskProcessor.shutdown();
                var terminated = false;
                for (int attempt = 0;
                     attempt < EVENT_WATCHER_SHUTDOWN_MAX_ATTEMPTS && !terminated; attempt++) {
                    terminated = eventTaskProcessor.awaitTermination(
                        EVENT_WATCHER_SHUTDOWN_TIMEOUT_MS, TimeUnit.MILLISECONDS);
                    if (!terminated) {
                        logger.trace("{}-{} - Event processor still running, waiting for watcher threads",
                            line.deviceName, line.bcm);
                    }
                }
                if (!terminated) {
                    logger.error(
                        "{}-{} - Event watcher threads did not terminate within {}ms; forcing shutdown",
                        line.deviceName, line.bcm,
                        (long) EVENT_WATCHER_SHUTDOWN_TIMEOUT_MS * EVENT_WATCHER_SHUTDOWN_MAX_ATTEMPTS);
                    eventTaskProcessor.shutdownNow();
                }
            }
        } catch (Exception e) {
            line.closed = true;
            throw new ShutdownException(e);
        } finally {
            line.close();
        }
        logger.info("{}-{} - GPIO BCM is closed.", line.deviceName, line.bcm);
        return this;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Reads the current level of the requested line via {@code GPIO_V2_LINE_GET_VALUES_IOCTL} and maps
     * the returned bit to a {@link DigitalState}.
     *
     * @throws Pi4JException if the line is closed or the native value-read {@code ioctl} fails
     */
    @Override
    public DigitalState state() {
        return line.readState();
    }

    /**
     * Runnable that blocks in a native {@code poll()} on the requested GPIO line file descriptor,
     * reads {@code struct gpio_v2_line_event} records as they arrive, applies optional software
     * debounce, and hands the resulting {@link DetectedEvent} list to a {@link PinEventProcessing}
     * callback. It runs until {@link #stopWatching()} is called.
     */
    private class EventWatcher implements Runnable {
        private static final Logger logger = LoggerFactory.getLogger(EventWatcher.class);

        private final int fd;
        private final PinEvent pinEvent;
        private final PinEventProcessing eventProcessor;
        private final long debounceNs;

        private boolean stopWatching = false;

        EventWatcher(int fd, PinEvent pinEvent, PinEventProcessing eventProcessor) {
            this.fd = fd;
            this.pinEvent = pinEvent;
            this.eventProcessor = eventProcessor;
            // convert microseconds to nanoseconds for software debounce
            this.debounceNs = debounce * 1000L;
        }

        @Override
        public void run() {
            var eventSize = (int) LineEvent.LAYOUT.byteSize();
            var timestamp = Instant.now();
            List<DetectedEvent> eventList = new ArrayList<>();
            DetectedEvent lastDebouncedEvent = null;
            PinEvent lastDebouncedState = null;
            long lastEventReceivedTimeNs = 0;
            logger.trace("{} - Start polling GPIO data on BCM {} at {}",
                Thread.currentThread().getName(), line.bcm, timestamp);
            while (!stopWatching) {
                try {
                    // PollingData must be recreated each time — poll does not erase revents between calls
                    var pollData = new PollingData(fd, (short) (PollFlag.POLLIN | PollFlag.POLLPRI), (short) 0);
                    pollData = poll.poll(pollData, 1, EVENT_WATCHER_SHUTDOWN_TIMEOUT_MS / 2);
                    if (pollData == null) {
                        var duration = timestamp.until(Instant.now()).toMillis();
                        logger.trace("{} - No events detected on BCM {}: polling timeout at {} (took {}ms)",
                            Thread.currentThread().getName(), line.bcm, timestamp, duration);
                        if (lastDebouncedEvent != null && debounceNs > 0 && lastEventReceivedTimeNs > 0) {
                            long currentTimeNs = System.nanoTime();
                            long timeSinceLastEventNs = currentTimeNs - lastEventReceivedTimeNs;
                            if (timeSinceLastEventNs >= debounceNs) {
                                logger.trace(
                                    "{} - Dispatching pending debounced event on BCM {} after timeout ({}ns >= {}ns)",
                                    Thread.currentThread().getName(), line.bcm, timeSinceLastEventNs, debounceNs);
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
                        logger.error("{} - Internal error during polling on BCM {}. Last polling data: {}",
                            Thread.currentThread().getName(), line.bcm, pollData);
                        stopWatching();
                        continue;
                    }
                    if ((pollData.revents() & (PollFlag.POLLIN | PollFlag.POLLPRI)) != 0) {
                        // default minimum buffer size is 16 line events
                        // see https://elixir.bootlin.com/linux/latest/source/include/uapi/linux/gpio.h#L185
                        var buf = line.file.read(fd, new byte[16 * eventSize], 16 * eventSize);
                        var holder = new byte[eventSize];
                        for (int i = 0; i < 16 * LineEvent.LAYOUT.byteSize(); i += eventSize) {
                            // Check all 8 bytes of timestamp_ns — checking only buf[i] (the LSB) is
                            // insufficient because a valid timestamp divisible by 256 ns has a zero LSB.
                            if ((buf[i] | buf[i + 1] | buf[i + 2] | buf[i + 3]
                                | buf[i + 4] | buf[i + 5] | buf[i + 6] | buf[i + 7]) == 0) {
                                continue;
                            }
                            System.arraycopy(buf, i, holder, 0, eventSize);
                            var memoryBuffer = Arena.ofAuto().allocate(LineEvent.LAYOUT);
                            memoryBuffer.asByteBuffer().put(holder);
                            var event = LineEvent.createEmpty().from(memoryBuffer);
                            logger.trace("{} - Detected new event on BCM {}: {}",
                                Thread.currentThread().getName(), line.bcm, event);
                            if ((event.id() & this.pinEvent.getValue()) != 0) {
                                var pinEvent = PinEvent.getByValue(event.id());
                                logger.trace("{} - Processing event on BCM {}: {}",
                                    Thread.currentThread().getName(), line.bcm, pinEvent);
                                DetectedEvent detectedEvent =
                                    new DetectedEvent(event.timestampNs(), pinEvent, event.lineSeqno());
                                if (debounceNs > 0) {
                                    long currentTimeNs = System.nanoTime();
                                    if (lastDebouncedEvent == null) {
                                        lastDebouncedEvent = detectedEvent;
                                        lastDebouncedState = pinEvent;
                                        lastEventReceivedTimeNs = currentTimeNs;
                                        logger.trace("{} - Starting debounce period on BCM {} for {}",
                                            Thread.currentThread().getName(), line.bcm, pinEvent);
                                    } else {
                                        long timeSinceLastEventNs =
                                            detectedEvent.timestampInNanos() - lastDebouncedEvent.timestampInNanos();
                                        if (timeSinceLastEventNs < debounceNs) {
                                            logger.trace(
                                                "{} - Event on BCM {} within debounce period ({}ns < {}ns), updating to latest",
                                                Thread.currentThread().getName(), line.bcm,
                                                timeSinceLastEventNs, debounceNs);
                                            lastDebouncedEvent = detectedEvent;
                                            lastDebouncedState = pinEvent;
                                            lastEventReceivedTimeNs = currentTimeNs;
                                        } else {
                                            logger.trace(
                                                "{} - Debounce period passed on BCM {} ({}ns >= {}ns), dispatching event",
                                                Thread.currentThread().getName(), line.bcm,
                                                timeSinceLastEventNs, debounceNs);
                                            if (lastDebouncedState != null) {
                                                eventList.add(lastDebouncedEvent);
                                            }
                                            lastDebouncedEvent = detectedEvent;
                                            lastDebouncedState = pinEvent;
                                            lastEventReceivedTimeNs = currentTimeNs;
                                        }
                                    }
                                } else {
                                    eventList.add(detectedEvent);
                                }
                            }
                        }
                        logger.trace("{} - Total events on BCM {}: {}",
                            Thread.currentThread().getName(), line.bcm, eventList.size());
                        if (!eventList.isEmpty()) {
                            eventProcessor.process(eventList);
                            eventList.clear();
                        }
                        logger.trace("{} - Total processing on BCM {} took {}ms",
                            Thread.currentThread().getName(), line.bcm, timestamp.until(Instant.now()).toMillis());
                        timestamp = Instant.now();
                    }
                } catch (Throwable e) {
                    logger.error("{} - Error while polling pin on BCM {}",
                        Thread.currentThread().getName(), line.bcm, e);
                    throw new Pi4JException(e);
                }
            }
        }

        public synchronized void stopWatching() {
            this.stopWatching = true;
        }

        public synchronized boolean isRunning() {
            return !this.stopWatching;
        }
    }
}

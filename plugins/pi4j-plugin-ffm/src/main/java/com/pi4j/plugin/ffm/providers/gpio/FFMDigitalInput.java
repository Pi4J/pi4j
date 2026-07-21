package com.pi4j.plugin.ffm.providers.gpio;

import com.pi4j.context.Context;
import com.pi4j.exception.InitializeException;
import com.pi4j.exception.Pi4JException;
import com.pi4j.exception.ShutdownException;
import com.pi4j.io.gpio.digital.*;
import com.pi4j.plugin.ffm.common.FFMPermissionHelper;
import com.pi4j.plugin.ffm.common.gpio.DetectedEvent;
import com.pi4j.plugin.ffm.common.gpio.PinEvent;
import com.pi4j.plugin.ffm.common.gpio.PinFlag;
import com.pi4j.plugin.ffm.common.gpio.enums.LineAttributeId;
import com.pi4j.plugin.ffm.common.gpio.structs.LineAttribute;
import com.pi4j.plugin.ffm.common.gpio.structs.LineConfigAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 */
public class FFMDigitalInput extends DigitalInputBase implements DigitalInput {
    private static final Logger logger = LoggerFactory.getLogger(FFMDigitalInput.class);



    // Maximum graceful-await cycles before giving up on a stuck watcher thread.
    private static final int EVENT_WATCHER_SHUTDOWN_MAX_ATTEMPTS = 5;

    private final FFMGpioLine line;

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
            logger.error("{}-{} - DigitalInput offset Initialization error: {}", line.deviceName, line.offset, e.getMessage());
            throw e;
        }
        logger.info("{}-{} - DigitalInput offset configured.", line.deviceName, line.offset);
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
            attributes.add(new LineConfigAttribute(debounceAttribute, 1L << line.offset));
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
        logger.trace("{}-{} - Adding new listener", line.deviceName, line.offset);
        if (threadFactory == null) {
            this.threadFactory = Thread.ofPlatform()
                .name(line.deviceName + "-event-detection-pin-", line.offset)
                .daemon(true)
                .uncaughtExceptionHandler((_, e) -> logger.error(e.getMessage(), e))
                .factory();
            this.eventTaskProcessor = Executors.newCachedThreadPool(threadFactory);
        }
        var watcher = new EventWatcher(line.chipFileDescriptor, line.offset, debounce, line.file, PinEvent.BOTH, events -> {
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
        logger.trace("{}-{} - New listener added", line.deviceName, line.offset);
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
        logger.info("{}-{} - closing GPIO offset.", line.deviceName, line.offset);
        try {
            logger.trace("{}-{} - Stopping event watchers", line.deviceName, line.offset);
            for (EventWatcher watcher : watchers) {
                watcher.stopWatching();
            }
            watchers.clear();
            // Await the executor whenever it was ever created. removeListener() stops its watcher
            // and clears the list without waiting for the thread to leave its native poll(); that
            // thread still holds the line fd. Closing the fd without waiting would leave the GPIO
            // line flagged USED on the next create attempt.
            if (eventTaskProcessor != null) {
                logger.trace("{}-{} - Gracefully shutting down event processor", line.deviceName, line.offset);
                eventTaskProcessor.shutdown();
                var terminated = false;
                for (int attempt = 0;
                     attempt < EVENT_WATCHER_SHUTDOWN_MAX_ATTEMPTS && !terminated; attempt++) {
                    terminated = eventTaskProcessor.awaitTermination(
                        EventWatcher.EVENT_WATCHER_SHUTDOWN_TIMEOUT_MS, TimeUnit.MILLISECONDS);
                    if (!terminated) {
                        logger.trace("{}-{} - Event processor still running, waiting for watcher threads",
                            line.deviceName, line.offset);
                    }
                }
                if (!terminated) {
                    logger.error(
                        "{}-{} - Event watcher threads did not terminate within {}ms; forcing shutdown",
                        line.deviceName, line.offset,
                        (long) EventWatcher.EVENT_WATCHER_SHUTDOWN_TIMEOUT_MS * EVENT_WATCHER_SHUTDOWN_MAX_ATTEMPTS);
                    eventTaskProcessor.shutdownNow();
                }
            }
        } catch (Exception e) {
            line.closed = true;
            throw new ShutdownException(e);
        } finally {
            line.close();
        }
        logger.info("{}-{} - GPIO offset is closed.", line.deviceName, line.offset);
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
}

package com.pi4j.plugin.ffm.providers.parallel;

import com.pi4j.exception.InitializeException;
import com.pi4j.exception.Pi4JException;
import com.pi4j.exception.ShutdownException;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.gpio.parallel.ParallelPort;
import com.pi4j.io.gpio.parallel.ParallelPortBase;
import com.pi4j.io.gpio.parallel.ParallelPortConfig;
import com.pi4j.plugin.ffm.common.FFMGpioLine;
import com.pi4j.plugin.ffm.common.gpio.PinEvent;
import com.pi4j.plugin.ffm.common.gpio.PinFlag;
import com.pi4j.plugin.ffm.common.gpio.enums.LineAttributeId;
import com.pi4j.plugin.ffm.common.gpio.structs.LineAttribute;
import com.pi4j.plugin.ffm.common.gpio.structs.LineConfig;
import com.pi4j.plugin.ffm.common.gpio.structs.LineConfigAttribute;
import com.pi4j.plugin.ffm.common.gpio.structs.LineEvent;
import com.pi4j.plugin.ffm.providers.gpio.EventWatcher;
import com.pi4j.util.MaskUtils;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 * FFM implementation of {@link ParallelPort}
 */
public sealed class FFMParallelPort
    extends ParallelPortBase
    implements ParallelPort {

    private final LineConfig inputLineConfig;
    private final LineConfig outputLineConfig;
    private final FFMGpioLine gpioLine;

    private EventWatcher eventWatcher;
    /**
     * Executor service for the event watcher thread.
     * @see <a href="https://github.com/Pi4J/pi4j/issues/804">Pi4J issue 804</a>
     */
    private final ExecutorService watcherService;
    /**
     * Executor service for dispatching events to listeners.
     * @see <a href="https://github.com/Pi4J/pi4j/issues/805">Pi4J issue 805</a>
     */
    private final ExecutorService dispatcherService;

    /**
     * Creates a new GPIO parallel port instance bound to the given context and configuration.
     *
     * @param config  the configuration describing this I/O
     * @param onClose an optional close handler
     */
    public static FFMParallelPort create(ParallelPortConfig config, Consumer<? super ParallelPort> onClose) {
        var mappingLogic = new FFMParallelPort.BitmaskMappingLogic(config.offsets());
        return mappingLogic.isRequired()
            ? new FFMParallelPort.WithBitmaskMappingLogic(config, mappingLogic, onClose)
            : new FFMParallelPort(config, onClose);
    }

    private FFMParallelPort(ParallelPortConfig config, Consumer<? super ParallelPort> onClose) {
        super(config, onClose);
        this.inputLineConfig = createInputLineConfigs(config);
        this.outputLineConfig = createOutputLineConfigs(config);
        this.gpioLine = new FFMGpioLine(MaskUtils.mask(config.offsets()), config.bus());

        // Events are only available if the initial direction is INPUT. No need to create these for OUTPUT ports
        if (config.initialDirection() == Direction.INPUT) {
            this.watcherService = Executors.newSingleThreadExecutor(Thread.ofPlatform()
                .name(config.id() + "-watcher-", 0)
                .daemon(true)
                .uncaughtExceptionHandler((_, e) -> logger.error("Uncaught exception in watcher thread", e))
                .factory()
            );
            this.dispatcherService = Executors.newCachedThreadPool(Thread.ofPlatform()
                .name(config.id() + "-dispatcher-", 0)
                .uncaughtExceptionHandler((_, e) -> logger.error("Uncaught exception in dispatcher thread", e))
                .factory()
            );
        } else {
            this.watcherService = null;
            this.dispatcherService = null;
        }
    }

    @Override
    public void applyInitialState() throws InitializeException {
        if (config.initialDirection() == Direction.INPUT) {
            gpioLine.openAndRequest(inputLineConfig.flags(), List.of(inputLineConfig.attrs()), config.id());
            if (this.eventWatcher == null) {
                this.eventWatcher = new EventWatcher(
                    gpioLine.chipFileDescriptor,
                    gpioLine.mask,
                    gpioLine.file,
                    PinEvent.BOTH,
                    /* TODO: No software debounce for now and leaving this to hardware debounce if configured.
                        Current software debounce is designed for Digital devices and does not differentiate between
                        offsets. As such, it's likely to skip events when multiple offsets are configured in a single port.
                        -
                        Recommendation will probably be to apply debounce as a filter at the port level to allow
                        a strategy to be configured by the user.
                     */
                    this::handleEvents
                );
            }

        } else {
            gpioLine.openAndRequest(outputLineConfig.flags(), List.of(outputLineConfig.attrs()), config.id());
        }
        super.applyInitialState();
    }

    @Override
    protected void handleWrite(int value) {
        gpioLine.writeValue(value);
    }

    @Override
    protected int handleRead() {
        return gpioLine.readValue();
    }

    @Override
    protected Direction handleSetDirection(Direction direction) {
        gpioLine.reconfigure(direction == Direction.INPUT ? inputLineConfig : outputLineConfig);
        return direction;
    }

    /**
     * Map a gpio offset to its pin offset in this port
     * @param gpio the gpio offset
     * @return the pin offset
     */
    protected int mapGpioToPin(int gpio) {
        return MaskUtils.packOffsetByMask(config.mask(), gpio);
    }

    @Override
    public ParallelPort addListener(PinStateChangeListener listener) {
        if (config.initialDirection() == Direction.OUTPUT) {
            throw new IllegalStateException("Cannot add listener to a port not primarily configured as an input");
        }

        if (this.eventWatcher != null) {
            boolean startRequired = !events.hasListeners();
            super.addListener(listener);
            if (startRequired) {
                watcherService.submit(eventWatcher);
            }
        } else {
            throw new IllegalStateException("Event watcher is not initialized");
        }
        return this;
    }

    @Override
    public ParallelPort removeListener(PinStateChangeListener listener) {
        if (config.initialDirection() == Direction.OUTPUT) {
            throw new IllegalStateException("Cannot remove listener from a port not primarily configured as an input");
        }

        if (this.eventWatcher != null) {
            super.removeListener(listener);
            if (!events.hasListeners()) {
                eventWatcher.stopWatching();
            }
        } else {
            throw new IllegalStateException("Event watcher is not initialized");
        }
        return this;
    }

    @Override
    public void close() {
        if (closed) {
            return;
        }
        super.close();
        if (eventWatcher != null && events.hasListeners()) {
            eventWatcher.stopWatching();
        }
        if (watcherService != null) {
            watcherService.shutdown();
            dispatcherService.shutdown();
        }
        if (config.shutdownValue() != null && getDirection() == Direction.OUTPUT) {
            write(config.shutdownValue());
        }
        try {
            gpioLine.close();
        } catch (Pi4JException e) {
            throw new ShutdownException(e);
        }
    }

    /**
     * Handle the events detected by the {@link EventWatcher} and dispatch them to the registered listeners.
     * @param eventList the list of line events detected
     */
    private void handleEvents(List<LineEvent> eventList) {
        var notification = eventsAsNotification(eventList);
        if (!dispatcherService.isShutdown()) {
            dispatcherService.submit(() -> events.dispatch(notification));
        }
    }

    /**
     * Create the {@link LineConfig} for the input mode
     * @param config the parallel port configuration
     * @return the line configuration for the input mode
     */
    private static LineConfig createInputLineConfigs(ParallelPortConfig config) {
        var eventFlags = 0;
        var modeFlags = PinFlag.INPUT.getValue();
        var attributes = new LineConfigAttribute[0];

        // only configure pulls, events and debounce when initial (primary) direction is INPUT
        if (config.initialDirection() == ParallelPort.Direction.INPUT) {
            modeFlags |= switch (config.pull()) {
                case PULL_DOWN -> PinFlag.BIAS_PULL_DOWN.getValue();
                case PULL_UP -> PinFlag.BIAS_PULL_UP.getValue();
                case OFF -> 0;
            };

            /*
             Configuration might want to allow the user not to forced to listen to both edges.
             */
            eventFlags |= PinFlag.EDGE_RISING.getValue() | PinFlag.EDGE_FALLING.getValue();

            if (config.debounce() * 1000 > Integer.MAX_VALUE) {
                throw new InitializeException("Debounce value of " + config.debounce() + " is too large");
            }

            if (config.debounce() > 0) {
                var attribute = new LineAttribute(
                    LineAttributeId.GPIO_V2_LINE_ATTR_ID_DEBOUNCE.getValue(),
                    0,
                    0,
                    (int) (config.debounce() * 1000)
                );

                attributes = new LineConfigAttribute[] {
                    new LineConfigAttribute(attribute, MaskUtils.packed(config.mask()))
                };
            }
        }

        return new LineConfig(eventFlags | modeFlags, attributes.length, attributes);
    }

    /**
     * Create the {@link LineConfig} for the output mode
     * @param config the parallel port configuration
     * @return the line configuration for the output mode
     */
    private static LineConfig createOutputLineConfigs(ParallelPortConfig config) {
        var modeFlags = PinFlag.OUTPUT.getValue();
        var attributes = new LineConfigAttribute[0];

        // only configure the initial value when the initial direction is OUTPUT,
        // and the value differs from the hardware default
        if (config.initialDirection() == ParallelPort.Direction.OUTPUT && config.initialValue() > 0) {
            var attribute = new LineAttribute(
                LineAttributeId.GPIO_V2_LINE_ATTR_ID_OUTPUT_VALUES.getValue(),
                0,
                config.initialValue(),
                0
            );
            attributes = new LineConfigAttribute[] {
                new LineConfigAttribute(attribute, MaskUtils.packed(config.mask()))
            };
        }

        return  new LineConfig(modeFlags, attributes.length, attributes);
    }

    /**
     * Convert a list of {@link LineEvent} to a {@link PinStateChangedNotification}
     * <p>
     * TODO: This does not currently detect missing events.
     * <p>
     * TODO: Additionally, we then need to ask whether it is necessary to track {@link LineEvent#lineSeqno()} across
     *  {@link ParallelPort.PinStateChangedNotification}s to capture dropping of first and last events. This might
     *  have to consider any downside in introducing mutable state to this class.
     *
     * @param eventList the list of detected events
     * @return a {@link PinStateChangedNotification} representing the state changes
     */
    private ParallelPort.PinStateChangedNotification eventsAsNotification(List<LineEvent> eventList) {
        PinStateChangedEvent[] changeEvents = new PinStateChangedEvent[eventList.size()];
        for (int i = 0; i < eventList.size(); i++) {
            changeEvents[i] = eventAsChangeEvent(eventList.get(i));
        }

        return new PinStateChangedNotification(this, false, changeEvents);
    }

    /**
     * Map a {@link LineEvent} to a {@link PinStateChangedEvent}
     * @param event the detected event
     * @return a {@link PinStateChangedEvent} representing the state change
     */
    private ParallelPort.PinStateChangedEvent eventAsChangeEvent(LineEvent event) {
        // convert the hardware offset to the logical user-specified offset
        int offset = mapGpioToPin(event.offset());
        // represent the change type by its resulting digital state value
        var state = event.pinEvent() == PinEvent.RISING
            ? DigitalState.HIGH
            : DigitalState.LOW;

        return new PinStateChangedEvent(offset, state);
    }

    /**
     * {@link FFMParallelPort} which maps to and from user-specified and hardware-compatible bitmask.
     * <p>
     * This allows users to avoid the O(n) overhead of remapping bitmasks for each {@link #read()} and
     * {@link #write(int)} operation when their offsets are provided in a hardware-native order
     * @see BitmaskMappingLogic
     */
    static final class WithBitmaskMappingLogic extends FFMParallelPort {

        private final BitmaskMappingLogic mappingLogic;

        /**
         * Creates a new GPIO parallel port instance bound to the given provider and configuration.
         *
         * @param config       the configuration describing this I/O
         * @param mappingLogic logic required to map user-defined pins to hardware-friendly values
         * @param onClose      an optional close handler
         */
        WithBitmaskMappingLogic(
            ParallelPortConfig config, BitmaskMappingLogic mappingLogic, Consumer<? super ParallelPort> onClose
        ) {
            super(config, onClose);
            this.mappingLogic = mappingLogic;
        }

        @Override
        protected void handleWrite(int value) {
            super.handleWrite(mappingLogic.map(value));
        }

        @Override
        protected int handleRead() {
            return mappingLogic.unmap(super.handleRead());
        }

        @Override
        protected int mapGpioToPin(int offset) {
            return mappingLogic.getUserOffset(super.mapGpioToPin(offset));
        }
    }

    /**
     * Operations to invoke when mapping to and from a user-provided and the bitmask expected or provided
     * by hardware.
     * <p>
     * If the user supplies bcm values in ascending order, remapping is not required
     * (as indicated by {@link #isRequired()} evaluating to false) and {@link UnaryOperator#identity()} is sufficient.
     * <p>
     * If the user supplies bcm values in anything other than ascending order, we're assuming that
     * this implies a bit-order. In such cases, we need to adjust the user-provided values to match
     * what {@code ioctl} expects (for {@link #map(int)}) or provides ({@link #unmap}).
     */
    static final class BitmaskMappingLogic {
        /**
         * The indices of each bit of the user-specified mask required to map to the value to a hardware-compatible
         * bit mask
         */
        private final int[] offsets;

        /**
         * Whether remap logic is necessary for this user mask
         */
        private final boolean required;

        /**
         * Construct remapping logic based on the user-specified offsets
         * @param offsets the ordered offsets as specified by the user
         */
        public BitmaskMappingLogic(List<Integer> offsets) {
            this.offsets = offsets.stream().sorted()
                .mapToInt(offsets::indexOf)
                .toArray();

            var req = false;
            for (int i = 1; i < this.offsets.length; i++) {
                if (this.offsets[i] < this.offsets[i - 1]) {
                    req = true;
                    break;
                }
            }
            this.required = req;
        }

        /**
         * Whether re-mapping logic is required
         * @return true if the user offsets are out of sequential order
         */
        public boolean isRequired() {
            return this.required;
        }

        /**
         * Map the user-specified value to that required by {@code ioctl}
         * @param value the user value
         * @return a value which can sent to {@code ioctl}
         */
        public int map(int value) {
            int result = 0;
            for (int i = 0; i < this.offsets.length; i++) {
                result |= ((value >> i) & 1) << offsets[i];
            }
            return result;
        }

        /**
         * Map the {@code ioctl}-provided value back to user space
         * @param value the value returned by {@code ioctl}
         * @return a value which makes sense to the user
         */
        public int unmap(int value) {
            int result = 0;
            for (int i = 0; i < this.offsets.length; i++) {
                result |= ((value >> offsets[i]) & 1) << i;
            }
            return result;
        }

        /**
         * Find the user-specified offset for an in-sequence offset
         * @param inSequenceOffset the in-sequence offset as maybe packed from a {@link LineEvent#offset()}
         * @return the logical offset according to the user-specified sequence
         */
        public int getUserOffset(int inSequenceOffset) {
            if (inSequenceOffset < offsets.length) {
                return offsets[inSequenceOffset];
            } else {
                return -1;
            }
        }
    }
}

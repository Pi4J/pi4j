package com.pi4j.plugin.ffm.providers.parallel;

import com.pi4j.context.Context;
import com.pi4j.exception.InitializeException;
import com.pi4j.exception.Pi4JException;
import com.pi4j.exception.ShutdownException;
import com.pi4j.io.gpio.MaskUtils;
import com.pi4j.io.gpio.parallel.ParallelPort;
import com.pi4j.io.gpio.parallel.ParallelPortBase;
import com.pi4j.io.gpio.parallel.ParallelPortConfig;
import com.pi4j.io.gpio.parallel.ParallelPortProvider;
import com.pi4j.plugin.ffm.common.FFMGpioLine;
import com.pi4j.plugin.ffm.common.gpio.PinFlag;
import com.pi4j.plugin.ffm.common.gpio.enums.LineAttributeId;
import com.pi4j.plugin.ffm.common.gpio.structs.LineAttribute;
import com.pi4j.plugin.ffm.common.gpio.structs.LineConfig;
import com.pi4j.plugin.ffm.common.gpio.structs.LineConfigAttribute;

import java.util.List;
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

    /**
     * Creates a new GPIO parallel port instance bound to the given provider and configuration.
     *
     * @param context the Pi4J context
     * @param provider the provider that creates and backs this I/O instance
     * @param config   the configuration describing this I/O
     */
    public FFMParallelPort(Context context, ParallelPortProvider provider, ParallelPortConfig config) {
        super(context, provider, config);
        this.inputLineConfig = createInputLineConfigs(config);
        this.outputLineConfig = createOutputLineConfigs(config);
        this.gpioLine = new FFMGpioLine(MaskUtils.mask(config.offsets()), config.bus());
    }

    @Override
    public ParallelPort initialize(Context context) throws InitializeException {
        if (config.initialDirection() == Direction.INPUT) {
            gpioLine.openAndRequest(inputLineConfig.flags(), List.of(inputLineConfig.attrs()), config.id());
        } else {
            gpioLine.openAndRequest(outputLineConfig.flags(), List.of(outputLineConfig.attrs()), config.id());
        }
        return super.initialize(context);
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

    @Override
    public ParallelPort addListener(Listener listener) {
        throw new UnsupportedOperationException("TBD");
    }

    @Override
    public ParallelPort removeListener(Listener listener) {
        throw new UnsupportedOperationException("TBD");
    }

    @Override
    public void close() {
        if (closed) {
            return;
        }
        super.close();
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
     * Create the {@link LineConfig} for the input mode
     * @param config the parallel port configuration
     * @return the line configuration for the input mode
     */
    private LineConfig createInputLineConfigs(ParallelPortConfig config) {
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
    private LineConfig createOutputLineConfigs(ParallelPortConfig config) {
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
         * @param context the Pi4J context
         * @param provider the provider that creates and backs this I/O instance
         * @param config   the configuration describing this I/O
         * @param mappingLogic logic required to map user-defined pins to hardware-friendly values
         */
        WithBitmaskMappingLogic(
            Context context,
            ParallelPortProvider provider,
            ParallelPortConfig config,
            BitmaskMappingLogic mappingLogic
        ) {
            super(context, provider, config);
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
    }
}

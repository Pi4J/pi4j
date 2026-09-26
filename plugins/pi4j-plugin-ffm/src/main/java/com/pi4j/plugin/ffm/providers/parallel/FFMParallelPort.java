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

/**
 * FFM implementation of {@link ParallelPort}
 */
public class FFMParallelPort extends ParallelPortBase implements ParallelPort {

    private final LineConfig inputLineConfig;
    private final LineConfig outputLineConfig;
    private final FFMGpioLine gpioLine;

    /**
     * Creates a new GPIO parallel port instance bound to the given provider and configuration.
     *
     * @param provider the provider that creates and backs this I/O instance
     * @param config   the configuration describing this I/O
     */
    public FFMParallelPort(ParallelPortProvider provider, ParallelPortConfig config) {
        super(provider, config);
        this.inputLineConfig = createInputLineConfigs(config);
        this.outputLineConfig = createOutputLineConfigs(config);
        this.gpioLine = new FFMGpioLine(config.mask(), config.bus());
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
    public ParallelPort shutdownInternal(Context context) throws ShutdownException {
        if (config.shutdownValue() != null && getDirection() == Direction.OUTPUT) {
            write(config.shutdownValue());
        }
        try {
            gpioLine.close();
        } catch (Pi4JException e) {
            throw new ShutdownException(e);
        }
        return super.shutdownInternal(context);
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
}

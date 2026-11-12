package com.pi4j.plugin.mock;

import com.pi4j.context.ContextBuilder;
import com.pi4j.context.ContextConfig;
import com.pi4j.context.impl.DefaultContext;
import com.pi4j.io.IO;
import com.pi4j.io.IOConfig;
import com.pi4j.io.IOType;
import com.pi4j.io.gpio.digital.DigitalOutputConfig;
import com.pi4j.io.gpio.digital.DigitalInputConfig;
import com.pi4j.io.gpio.parallel.ParallelDigitalInput;
import com.pi4j.io.gpio.parallel.ParallelDigitalOutput;
import com.pi4j.io.gpio.parallel.ParallelPortConfig;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.pwm.PwmConfig;
import com.pi4j.io.spi.SpiConfig;
import com.pi4j.plugin.mock.provider.gpio.parallel.MockParallelPort;
import com.pi4j.plugin.mock.provider.i2c.MockI2C;
import com.pi4j.plugin.mock.provider.pwm.MockPwm;
import com.pi4j.plugin.mock.provider.spi.MockSpi;

import java.util.function.Consumer;

public class MockContext extends DefaultContext {

    private final Consumer<IO<?>> onClose;

    public MockContext(Consumer<IO<?>> onClose) {
        super(ContextBuilder.newInstance().toConfig());
        this.onClose = onClose;
    }

    public MockContext(ContextConfig config) {
        super(config);
        this.onClose = this::shutdown;
    }

    public MockContext() {
        this(ContextBuilder.newInstance().toConfig());
    }

    @Override
    protected IO<?> createImpl(IOConfig config, IOType type) {
        return switch (type) {
            case PWM -> new MockPwm((PwmConfig) config, this.onClose);
            case I2C -> new MockI2C((I2CConfig) config, this.onClose);
            case SPI -> new MockSpi((SpiConfig) config, this.onClose);
            case DIGITAL_INPUT -> {
                var parallelConfig = ParallelPortConfig.of((DigitalInputConfig) config);
                var parallelPort = new MockParallelPort(parallelConfig, _ -> {});
                yield new ParallelDigitalInput((DigitalInputConfig) config, parallelPort, this.onClose);
            }
            case DIGITAL_OUTPUT -> {
                var parallelConfig = ParallelPortConfig.of((DigitalOutputConfig) config);
                var parallelPort = new MockParallelPort(parallelConfig, _ -> {});
                yield new ParallelDigitalOutput((DigitalOutputConfig) config, parallelPort, this.onClose);
            }
            case PARALLEL -> new MockParallelPort((ParallelPortConfig) config, this.onClose);
        };
    }
}

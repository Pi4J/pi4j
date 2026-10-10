package com.pi4j.plugin.mock;

import com.pi4j.context.ContextBuilder;
import com.pi4j.context.ContextConfig;
import com.pi4j.context.impl.DefaultContext;
import com.pi4j.io.IO;
import com.pi4j.io.IOConfig;
import com.pi4j.io.IOType;
import com.pi4j.io.gpio.digital.DigitalInputConfig;
import com.pi4j.io.gpio.digital.DigitalOutputConfig;
import com.pi4j.io.gpio.parallel.ParallelPortConfig;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.pwm.PwmConfig;
import com.pi4j.io.spi.SpiConfig;
import com.pi4j.plugin.mock.provider.gpio.digital.MockDigitalInput;
import com.pi4j.plugin.mock.provider.gpio.digital.MockDigitalOutput;
import com.pi4j.plugin.mock.provider.gpio.parallel.MockParallelPort;
import com.pi4j.plugin.mock.provider.i2c.MockI2C;
import com.pi4j.plugin.mock.provider.pwm.MockPwm;
import com.pi4j.plugin.mock.provider.spi.MockSpi;

public class MockContext extends DefaultContext {

    public MockContext(ContextConfig config) {
        super(config);
    }

    public MockContext() {
        this(ContextBuilder.newInstance().toConfig());
    }

    @Override
    protected IO<?, ?> createImpl(IOConfig config, IOType type) {
        return switch (type) {
            case DIGITAL_INPUT -> new MockDigitalInput(this, (DigitalInputConfig) config);
            case DIGITAL_OUTPUT -> new MockDigitalOutput(this, (DigitalOutputConfig) config);
            case PWM -> new MockPwm(this, (PwmConfig) config);
            case I2C -> new MockI2C(this, (I2CConfig) config);
            case SPI -> new MockSpi(this, (SpiConfig) config);
            case PARALLEL -> new MockParallelPort(this, (ParallelPortConfig) config);
        };
    }
}

package com.pi4j.plugin.ffm;

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
import com.pi4j.plugin.ffm.common.FFMPermissionHelper;
import com.pi4j.plugin.ffm.providers.gpio.FFMDigitalInput;
import com.pi4j.plugin.ffm.providers.gpio.FFMDigitalOutput;
import com.pi4j.plugin.ffm.providers.i2c.FFMI2C;
import com.pi4j.plugin.ffm.providers.parallel.FFMParallelPort;
import com.pi4j.plugin.ffm.providers.pwm.FFMPwm;
import com.pi4j.plugin.ffm.providers.spi.FFMSpi;

public class FFMContext extends DefaultContext {

    public FFMContext() {
        this(ContextBuilder.newInstance().toConfig());
    }

    public FFMContext(ContextConfig config) {
        super(config);
    }

    @Override
    public IO<?, ?> createImpl(IOConfig config, IOType type) {
        FFMPermissionHelper.checkUserPermissions(type);
        return switch (type) {
            case DIGITAL_INPUT -> new FFMDigitalInput(this, (DigitalInputConfig) config);
            case DIGITAL_OUTPUT -> new FFMDigitalOutput( this, (DigitalOutputConfig) config);
            case PWM -> FFMPwm.create(this, (PwmConfig) config);
            case I2C -> FFMI2C.create(this, (I2CConfig) config);
            case SPI -> new FFMSpi(this, (SpiConfig) config);
            case PARALLEL -> FFMParallelPort.create(this, (ParallelPortConfig) config);
        };
    }

}

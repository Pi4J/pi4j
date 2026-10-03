package com.pi4j.plugin.ffm;

import com.pi4j.context.ContextBuilder;
import com.pi4j.context.ContextConfig;
import com.pi4j.context.impl.DefaultContext;
import com.pi4j.io.IO;
import com.pi4j.io.IOConfig;
import com.pi4j.io.IOType;
import com.pi4j.io.exception.IOException;
import com.pi4j.io.gpio.digital.DigitalInputConfig;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalOutputConfig;
import com.pi4j.io.gpio.parallel.ParallelPortConfig;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CBase;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.i2c.I2CImplementation;
import com.pi4j.io.pwm.Pwm;
import com.pi4j.io.pwm.PwmConfig;
import com.pi4j.io.pwm.PwmType;
import com.pi4j.io.spi.SpiConfig;
import com.pi4j.plugin.ffm.common.FFMPermissionHelper;
import com.pi4j.plugin.ffm.providers.gpio.FFMDigitalInput;
import com.pi4j.plugin.ffm.providers.gpio.FFMDigitalOutput;
import com.pi4j.plugin.ffm.providers.i2c.FFMI2CBus;
import com.pi4j.plugin.ffm.providers.i2c.impl.I2CDirect;
import com.pi4j.plugin.ffm.providers.i2c.impl.I2CSMBus;
import com.pi4j.plugin.ffm.providers.parallel.FFMParallelPort;
import com.pi4j.plugin.ffm.providers.pwm.FFMPwmHardware;
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
            case PWM -> createPwm((PwmConfig) config);
            case I2C -> createI2C((I2CConfig) config);
            case SPI -> new FFMSpi(this, (SpiConfig) config);
            case PARALLEL -> new FFMParallelPort(this, (ParallelPortConfig) config);
        };
    }

    private Pwm createPwm(PwmConfig config) {
        if (config.pwmType() != PwmType.HARDWARE) {
            throw new IOException("The FFM PWM provider only supports HARDWARE PWM");
        }

        // validate the config
        if (config.chip() == null || config.channel() == null) {
            throw new IllegalArgumentException("PWM Chip and Channel are needed for hardware PWM with the FFM I/O provider");
        }

        // Warn for unneeded config
        if (config.pwmType() == PwmType.HARDWARE && config.bcm() != null) {
            logger.warn("You specified a BCM value for the PWM, but this is not needed for hardware PWM. Please specify chip and channel instead.");
        }

        // create new I/O instance based on I/O config
        return new FFMPwmHardware(this, config);
    }

    private I2C createI2C(I2CConfig config) {
        var bus = new FFMI2CBus(config);

        if (logger.isDebugEnabled()) {
            var functions = bus.getFunctionalityMap();
            logger.debug("{} - bus I2C functions support:", bus.getBusName());
            for (var entry : functions.entrySet()) {
                logger.debug("\t{} - {}", entry.getKey(), entry.getValue());
            }
        }

        var impl = config.i2cImplementation();
        if (impl == null) {
            impl = I2CImplementation.DIRECT;
            logger.debug("{} - no I2C implementation was chosen, using {} as default", bus.getBusName(), impl);
        }
        I2CBase<?> i2c;
        if (impl.equals(I2CImplementation.SMBUS) && bus.supportsSMBus()) {
            logger.debug("{} - creating SMBus adapter based on default implementation and functions", bus.getBusName());
            i2c = new I2CSMBus( this, config, bus);
        } else {
            logger.debug("{} - creating Direct ioctl adapter based on default implementation and functions", bus.getBusName());
            i2c = new I2CDirect(this, config, bus);
        }

        return i2c;
    }}

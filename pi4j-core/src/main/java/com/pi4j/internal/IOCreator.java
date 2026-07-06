package com.pi4j.internal;

import com.pi4j.io.IO;
import com.pi4j.io.IOConfig;
import com.pi4j.io.IOConfigBuilder;
import com.pi4j.io.IOType;
import com.pi4j.io.gpio.digital.*;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.i2c.I2CConfigBuilder;
import com.pi4j.io.pwm.Pwm;
import com.pi4j.io.pwm.PwmConfig;
import com.pi4j.io.pwm.PwmConfigBuilder;
import com.pi4j.io.spi.Spi;
import com.pi4j.io.spi.SpiConfig;
import com.pi4j.io.spi.SpiConfigBuilder;

/**
 * Note: This interface previously had string-based create methods.  These have been removed in favor of the
 * {@link IOConfigBuilder} and {@link IOType} based create methods.
 * <p>
 * We considered keeping these methods around for backwards compatibility, but this would move a compile time
 * issue to a runtime crash or lookup failure, which we considered worse.
 */
public interface IOCreator {

    <I extends IO> I create(IOConfig config, IOType type);

    default <I extends IO> I create(IOConfig config, Class<I> ioClass) {
        return (ioClass.cast(create(config, IOType.getByIOClass(ioClass))));
    }

    default <I extends IO> I create(IOConfigBuilder builder, IOType ioType) {
        return create((IOConfig) builder.build(), ioType);
    }

    default <I extends IO> I create(IOConfigBuilder builder, Class<I> ioClass) {
        return create((IOConfig) builder.build(), ioClass);
    }

    default DigitalOutput create(DigitalOutputConfig config) {
        return create(config, DigitalOutput.class);
    }

    default DigitalInput create(DigitalInputConfig config) {
        return create(config, DigitalInput.class);
    }

    default Pwm create(PwmConfig config) {
        return create(config, Pwm.class);
    }

    default I2C create(I2CConfig config) {
        return create(config, I2C.class);
    }

    default Spi create(SpiConfig config) {
        return create(config, Spi.class);
    }

    default DigitalOutput create(DigitalOutputConfigBuilder config) {
        return create(config.build());
    }

    default DigitalInput create(DigitalInputConfigBuilder config) {
        return create(config.build());
    }

    default Pwm create(PwmConfigBuilder config) {
        return create(config.build());
    }

    default I2C create(I2CConfigBuilder config) {
        return create(config.build());
    }

    default Spi create(SpiConfigBuilder config) {
        return create(config.build());
    }
}

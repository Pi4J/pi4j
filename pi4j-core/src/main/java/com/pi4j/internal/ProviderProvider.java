package com.pi4j.internal;

import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.*;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.pwm.Pwm;
import com.pi4j.io.pwm.PwmConfig;
import com.pi4j.io.spi.Spi;
import com.pi4j.io.spi.SpiConfig;
import com.pi4j.provider.Provider;

/**
 * Minimal stub preserved to keep code structured after examples advertised on the website working while
 * generally migrating out of providers.
 *
 * @deprecated Please use Context.create() methods directly. They are type safe wrt. the requested IO configuration.
 */
@Deprecated(since="5.0")
public interface ProviderProvider {

    default Provider<DigitalInput, DigitalInputConfig> din() {
        return digitalInput();
    }

    default Provider<DigitalOutput, DigitalOutputConfig> dout()  {
        return digitalOutput();
    }

    default Provider<DigitalInput, DigitalInputConfig> digitalInput() {
        return config -> ((Context) this).create(config);
    }

    default Provider<DigitalOutput, DigitalOutputConfig> digitalOutput() {
        return config -> ((Context) this).create(config);
    }

    default Provider<Pwm, PwmConfig> pwm() {
        return config -> ((Context) this).create(config);
    }

    default Provider<Spi, SpiConfig> spi() {
        return config -> ((Context) this).create(config);
    }

    default Provider<I2C, I2CConfig> i2c() {
        return config -> ((Context) this).create(config);
    }

    default Provider<DigitalInput, DigitalInputConfig> getDigitalInputProvider() {
        return this.digitalInput();
    }

    default Provider<DigitalOutput, DigitalOutputConfig> getDigitalOutputProvider() {
        return this.digitalOutput();
    }

    default Provider<Pwm, PwmConfig> getPwmProvider() {
        return this.pwm();
    }

    default Provider<Spi, SpiConfig> getSpiProvider() {
        return this.spi();
    }

    default Provider<I2C, I2CConfig> getI2CProvider() {
        return this.i2c();
    }
}

package com.pi4j.plugin.ffm.providers.gpio;

import com.pi4j.context.Context;
import com.pi4j.exception.InitializeException;
import com.pi4j.exception.ShutdownException;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalOutputConfig;
import com.pi4j.io.gpio.digital.DigitalOutputProvider;
import com.pi4j.io.gpio.digital.DigitalOutputProviderBase;

public class FFMDigitalOutputProviderImpl  extends DigitalOutputProviderBase implements DigitalOutputProvider {
    /**
     * <p>Constructor for PiGpioDigitalOutputProviderImpl.</p>
     */
    public FFMDigitalOutputProviderImpl() {
        this.id = "";
        this.name = "";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DigitalOutput create(DigitalOutputConfig config) {
        // create new I/O instance based on I/O config
        var digitalOutput = new FFMDigitalOutput(this, config);
        this.context.registry().add(digitalOutput);
        return digitalOutput;
    }

    @Override
    public int getPriority() {
        // the gpioD driver should be higher priority always
        return 150;
    }

    @Override
    public DigitalOutputProvider initialize(Context context) throws InitializeException {
        DigitalOutputProvider provider = super.initialize(context);
        return provider;
    }

    @Override
    public DigitalOutputProvider shutdown(Context context) throws ShutdownException {
        return super.shutdown(context);
    }
}

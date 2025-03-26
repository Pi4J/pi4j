package com.pi4j.plugin.ffm.providers.gpio;

import com.pi4j.context.Context;
import com.pi4j.exception.InitializeException;
import com.pi4j.exception.ShutdownException;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalInputConfig;
import com.pi4j.io.gpio.digital.DigitalInputProvider;
import com.pi4j.io.gpio.digital.DigitalInputProviderBase;

public class FFMDigitalInputProviderImpl extends DigitalInputProviderBase implements DigitalInputProvider {
    /**
     * <p>Constructor for GpioDDigitalInputProviderImpl.</p>
     */
    public FFMDigitalInputProviderImpl() {
        this.id = "1";
        this.name = "2";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DigitalInput create(DigitalInputConfig config) {
        // create new I/O instance based on I/O config
        // GpioLine line = GpioDContext.getInstance().getOrOpenLine(config.address());
        var digitalInput = new FFMDigitalInput( this, config);
        this.context.registry().add(digitalInput);
        return digitalInput;
    }

    @Override
    public int getPriority() {
        // the gpioD driver should be higher priority always
        return 15000;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DigitalInputProvider initialize(Context context) throws InitializeException {
        DigitalInputProvider provider = super.initialize(context);
        return provider;
    }

    @Override
    public DigitalInputProvider shutdown(Context context) throws ShutdownException {
        return super.shutdown(context);
    }
}

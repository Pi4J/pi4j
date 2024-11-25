package com.pi4j.plugin.gpiod.provider.gpio.digital;


import com.pi4j.context.Context;
import com.pi4j.exception.InitializeException;
import com.pi4j.exception.ShutdownException;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalInputConfig;
import com.pi4j.io.gpio.digital.DigitalInputProvider;
import com.pi4j.io.gpio.digital.DigitalInputProviderBase;
import com.pi4j.library.gpiod.internal.GpioDContext;
import com.pi4j.library.gpiod.internal.GpioLine;

public class GpioDDigitalInputProviderImpl extends DigitalInputProviderBase implements GpioDDigitalInputProvider {

    /**
     * <p>Constructor for GpioDDigitalInputProviderImpl.</p>
     */
    public GpioDDigitalInputProviderImpl() {
        this.id = ID;
        this.name = NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DigitalInput create(DigitalInputConfig config) {
        // create new I/O instance based on I/O config
        GpioLine line = GpioDContext.getInstance().getOrOpenLine(config.address());
        GpioDDigitalInput digitalInput = new GpioDDigitalInput(line, this, config);
        this.context.registry().add(digitalInput);
        return digitalInput;
    }

    @Override
    public int getPriority() {
        // the gpioD driver should be higher priority always
        return 150;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DigitalInputProvider initialize(Context context) throws InitializeException {
        DigitalInputProvider provider = super.initialize(context);
        GpioDContext.getInstance().initialize();
        return provider;
    }

    @Override
    public DigitalInputProvider shutdown(Context context) throws ShutdownException {
        GpioDContext.getInstance().close();
        return super.shutdown(context);
    }
}

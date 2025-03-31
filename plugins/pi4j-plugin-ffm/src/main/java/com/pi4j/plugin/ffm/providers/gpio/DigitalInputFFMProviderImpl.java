package com.pi4j.plugin.ffm.providers.gpio;

import com.pi4j.context.Context;
import com.pi4j.exception.InitializeException;
import com.pi4j.exception.ShutdownException;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalInputConfig;
import com.pi4j.io.gpio.digital.DigitalInputProvider;
import com.pi4j.io.gpio.digital.DigitalInputProviderBase;

public class DigitalInputFFMProviderImpl extends DigitalInputProviderBase implements DigitalInputProvider {

    public DigitalInputFFMProviderImpl() {
        this.id = getClass().getSimpleName();
        this.name = "Digital Pin Input (FFM API)";
    }

    @Override
    public DigitalInput create(DigitalInputConfig config) {
        var chipName =  context.config().properties().get("gpio.chip.name");
        var digitalInput = new DigitalInputFFM(chipName, this, config);
        this.context.registry().add(digitalInput);
        return digitalInput;
    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public DigitalInputProvider initialize(Context context) throws InitializeException {
        return super.initialize(context);
    }

    @Override
    public DigitalInputProvider shutdown(Context context) throws ShutdownException {
        return super.shutdown(context);
    }
}

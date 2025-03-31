package com.pi4j.plugin.ffm.providers.gpio;

import com.pi4j.context.Context;
import com.pi4j.exception.InitializeException;
import com.pi4j.exception.ShutdownException;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalOutputConfig;
import com.pi4j.io.gpio.digital.DigitalOutputProvider;
import com.pi4j.io.gpio.digital.DigitalOutputProviderBase;

public class DigitalOutputFFMProviderImpl extends DigitalOutputProviderBase implements DigitalOutputProvider {

    public DigitalOutputFFMProviderImpl() {
        this.id = getClass().getSimpleName();
        this.name = "Digital Pin Output (FFM API)";
    }


    @Override
    public DigitalOutput create(DigitalOutputConfig config) {
        var chipName =  context.config().properties().get("gpio.chip.name");
        var digitalOutput = new DigitalOutputFFM(chipName, this, config);
        this.context.registry().add(digitalOutput);
        return digitalOutput;
    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public DigitalOutputProvider initialize(Context context) throws InitializeException {
        return super.initialize(context);
    }

    @Override
    public DigitalOutputProvider shutdown(Context context) throws ShutdownException {
        return super.shutdown(context);
    }
}

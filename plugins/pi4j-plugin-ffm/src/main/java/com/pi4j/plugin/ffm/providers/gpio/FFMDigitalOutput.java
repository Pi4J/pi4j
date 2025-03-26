package com.pi4j.plugin.ffm.providers.gpio;

import com.pi4j.context.Context;
import com.pi4j.exception.InitializeException;
import com.pi4j.exception.ShutdownException;
import com.pi4j.io.exception.IOException;
import com.pi4j.io.gpio.digital.*;

public class FFMDigitalOutput  extends DigitalOutputBase implements DigitalOutput {

    public FFMDigitalOutput(DigitalOutputProvider provider, DigitalOutputConfig config) {
        super(provider, config);
    }

    @Override
    public DigitalOutput initialize(Context context) throws InitializeException {
        super.initialize(context);
        return this;
    }

    @Override
    public DigitalOutput shutdown(Context context) throws ShutdownException {
        super.shutdown(context);
        return this;
    }

    @Override
    public DigitalOutput state(DigitalState state) throws IOException {
        return super.state(state);
    }
}

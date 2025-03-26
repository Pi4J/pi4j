package com.pi4j.plugin.ffm.providers.gpio;

import com.pi4j.context.Context;
import com.pi4j.exception.InitializeException;
import com.pi4j.exception.ShutdownException;
import com.pi4j.io.gpio.digital.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FFMDigitalInput extends DigitalInputBase implements DigitalInput {
    private static final Logger logger = LoggerFactory.getLogger(FFMDigitalInput.class);

    public FFMDigitalInput(DigitalInputProvider provider, DigitalInputConfig config) {
        super(provider, config);
    }

    @Override
    public DigitalInput initialize(Context context) throws InitializeException {
        return this;
    }

    @Override
    public DigitalInput shutdown(Context context) throws ShutdownException {
        super.shutdown(context);
        return this;
    }

    @Override
    public DigitalState state() {
        return DigitalState.getState(true);
    }
}

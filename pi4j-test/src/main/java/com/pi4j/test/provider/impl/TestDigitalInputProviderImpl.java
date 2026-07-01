package com.pi4j.test.provider.impl;

import com.pi4j.io.exception.IOAlreadyExistsException;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalInputConfig;
import com.pi4j.io.gpio.digital.DigitalInputProviderBase;
import com.pi4j.test.provider.TestDigitalInput;
import com.pi4j.test.provider.TestDigitalInputProvider;

public class TestDigitalInputProviderImpl extends DigitalInputProviderBase implements TestDigitalInputProvider {

    public TestDigitalInputProviderImpl() {
        super();
    }

    public TestDigitalInputProviderImpl(String id) {
        super(id);
    }

    public TestDigitalInputProviderImpl(String id, String name) {
        super(id, name);
    }

    @Override
    public DigitalInput create(DigitalInputConfig config) {
        TestDigitalInput input = new TestDigitalInput(this, config);
        if (this.context.registry().exists(input.id()))
            throw new IOAlreadyExistsException(config.id());
        input.initialize(this.context);
        this.context.register(input);
        return input;
    }
}

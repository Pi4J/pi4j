package com.pi4j.test.provider;

import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalInputProvider;
import com.pi4j.test.provider.impl.TestDigitalInputProviderImpl;

/**
 * <p>TestDigitalInputProvider interface.</p>
 */
public interface TestDigitalInputProvider extends DigitalInputProvider {
    static TestDigitalInputProvider newInstance(){
        return new TestDigitalInputProviderImpl();
    }
    static TestDigitalInputProvider newInstance(String id){
        return new TestDigitalInputProviderImpl(id);
    }
    static TestDigitalInputProvider newInstance(String id, String name){
        return new TestDigitalInputProviderImpl(id, name);
    }

    default <T extends DigitalInput> T create() {
        return create(0);
    }
}

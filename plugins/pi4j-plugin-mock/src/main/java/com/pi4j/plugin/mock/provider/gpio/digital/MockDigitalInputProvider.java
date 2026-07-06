package com.pi4j.plugin.mock.provider.gpio.digital;

import com.pi4j.io.gpio.digital.DigitalInputProvider;
import com.pi4j.plugin.mock.Mock;

/**
 * Mock implementation of the pi4j-core {@link DigitalInputProvider} contract. This provider
 * creates {@link MockDigitalInput} instances backed by in-memory state instead of real GPIO
 * hardware, allowing digital-input code to be exercised in unit tests.
 *
 * @see MockDigitalInput
 */
public interface MockDigitalInputProvider extends DigitalInputProvider {
    /** Unique provider name, as defined by {@link Mock#DIGITAL_INPUT_PROVIDER_NAME}. */
    String NAME = Mock.DIGITAL_INPUT_PROVIDER_NAME;
    /** Unique provider identifier, as defined by {@link Mock#DIGITAL_INPUT_PROVIDER_ID}. */
    String ID = Mock.DIGITAL_INPUT_PROVIDER_ID;

    /**
     * Creates a new mock digital input provider instance.
     *
     * @return a new {@link MockDigitalInputProvider} backed by {@link MockDigitalInputProviderImpl}
     */
    static MockDigitalInputProvider newInstance() {
        return new MockDigitalInputProviderImpl();
    }
}

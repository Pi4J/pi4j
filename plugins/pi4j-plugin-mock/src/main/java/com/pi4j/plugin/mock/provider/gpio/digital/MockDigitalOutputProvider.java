package com.pi4j.plugin.mock.provider.gpio.digital;

import com.pi4j.io.gpio.digital.DigitalOutputProvider;
import com.pi4j.plugin.mock.Mock;

/**
 * Mock implementation of the pi4j-core {@link DigitalOutputProvider} contract. This provider
 * creates {@link MockDigitalOutput} instances backed by in-memory state instead of real GPIO
 * hardware, allowing digital-output code to be exercised in unit tests.
 *
 * @see MockDigitalOutput
 */
public interface MockDigitalOutputProvider extends DigitalOutputProvider {
    /** Unique provider name, as defined by {@link Mock#DIGITAL_OUTPUT_PROVIDER_NAME}. */
    String NAME = Mock.DIGITAL_OUTPUT_PROVIDER_NAME;
    /** Unique provider identifier, as defined by {@link Mock#DIGITAL_OUTPUT_PROVIDER_ID}. */
    String ID = Mock.DIGITAL_OUTPUT_PROVIDER_ID;

    /**
     * Creates a new mock digital output provider instance.
     *
     * @return a new {@link MockDigitalOutputProvider} backed by {@link MockDigitalOutputProviderImpl}
     */
    static MockDigitalOutputProvider newInstance() {
        return new MockDigitalOutputProviderImpl();
    }
}

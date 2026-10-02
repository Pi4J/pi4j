package com.pi4j.plugin.mock.provider.gpio.digital;

import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalOutputConfig;
import com.pi4j.io.gpio.digital.DigitalOutputProviderBase;
import com.pi4j.plugin.mock.Mock;

/**
 * Default implementation of {@link MockDigitalOutputProvider}. Extends the pi4j-core
 * {@link DigitalOutputProviderBase} and produces {@link MockDigitalOutput} instances that
 * simulate GPIO outputs entirely in memory for use in unit tests.
 *
 * @see MockDigitalOutput
 */
public class MockDigitalOutputProviderImpl extends DigitalOutputProviderBase implements MockDigitalOutputProvider {

    /**
     * Creates the provider and assigns its mock {@link #ID} and {@link #NAME}.
     */
    public MockDigitalOutputProviderImpl() {
        this.id = ID;
        this.name = NAME;
    }

    /**
     * Returns Mock.MOCK_PROVIDER_PRIORITY.
     */
    @Override
    public int getPriority() {
        return Mock.MOCK_PROVIDER_PRIORITY;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Creates a {@link MockDigitalOutput} that simulates the pin in memory.
     */
    @Override
    public DigitalOutput create(DigitalOutputConfig config) {
        return new MockDigitalOutput(context, this, config);
    }
}

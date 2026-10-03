package com.pi4j.plugin.mock.provider.gpio.digital;

import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalOutputConfig;
import com.pi4j.io.gpio.digital.DigitalOutputProvider;
import com.pi4j.plugin.mock.Mock;
import com.pi4j.provider.ProviderBase;

/**
 * Default implementation of {@link MockDigitalOutputProvider}. Extends the pi4j-core
 * {@link ProviderBase} and produces {@link MockDigitalOutput} instances that
 * simulate GPIO outputs entirely in memory for use in unit tests.
 *
 * @see MockDigitalOutput
 */
public class MockDigitalOutputProviderImpl
    extends ProviderBase<DigitalOutputProvider, DigitalOutput, DigitalOutputConfig>
    implements MockDigitalOutputProvider {

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

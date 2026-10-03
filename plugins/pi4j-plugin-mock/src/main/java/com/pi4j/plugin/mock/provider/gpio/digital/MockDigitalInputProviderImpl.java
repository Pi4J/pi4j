package com.pi4j.plugin.mock.provider.gpio.digital;

import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalInputConfig;
import com.pi4j.io.gpio.digital.DigitalInputProvider;
import com.pi4j.plugin.mock.Mock;
import com.pi4j.provider.ProviderBase;

/**
 * Default implementation of {@link MockDigitalInputProvider}. Extends the pi4j-core
 * {@link ProviderBase} and produces {@link MockDigitalInput} instances that
 * simulate GPIO inputs entirely in memory for use in unit tests.
 *
 * @see MockDigitalInput
 */
public class MockDigitalInputProviderImpl
    extends ProviderBase<DigitalInputProvider, DigitalInput, DigitalInputConfig>
    implements MockDigitalInputProvider {

    /**
     * Creates the provider and assigns its mock {@link #ID} and {@link #NAME}.
     */
    public MockDigitalInputProviderImpl() {
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
     * Creates a {@link MockDigitalInput} that simulates the pin in memory.
     */
    @Override
    public DigitalInput create(DigitalInputConfig config) {
        return new MockDigitalInput(context, this, config);
    }
}

package com.pi4j.plugin.mock.provider.gpio.digital;

import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalInputConfig;
import com.pi4j.io.gpio.digital.DigitalInputProviderBase;

/**
 * Default implementation of {@link MockDigitalInputProvider}. Extends the pi4j-core
 * {@link DigitalInputProviderBase} and produces {@link MockDigitalInput} instances that
 * simulate GPIO inputs entirely in memory for use in unit tests.
 *
 * @see MockDigitalInput
 */
public class MockDigitalInputProviderImpl extends DigitalInputProviderBase implements MockDigitalInputProvider {

    /**
     * Creates the provider and assigns its mock {@link #ID} and {@link #NAME}.
     */
    public MockDigitalInputProviderImpl() {
        this.id = ID;
        this.name = NAME;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Returns a deliberately high priority ({@code 1000}) so that, when the mock plugin is on
     * the classpath, it is preferred over real hardware providers during testing.
     */
    @Override
    public int getPriority() {
        // if the mock is loaded, then we most probably want to use it for testing
        return 1000;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Creates a {@link MockDigitalInput} that simulates the pin in memory and registers it
     * with the Pi4J context.
     */
    @Override
    public DigitalInput create(DigitalInputConfig config) {
        MockDigitalInput input = new MockDigitalInput(this, config);
        this.context.register(input);
        return input;
    }
}

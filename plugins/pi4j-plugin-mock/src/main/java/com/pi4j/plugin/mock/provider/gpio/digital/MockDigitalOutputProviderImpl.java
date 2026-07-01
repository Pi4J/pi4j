package com.pi4j.plugin.mock.provider.gpio.digital;

import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalOutputConfig;
import com.pi4j.io.gpio.digital.DigitalOutputProviderBase;

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
     * Creates a {@link MockDigitalOutput} that simulates the pin in memory and registers it
     * with the Pi4J context.
     */
    @Override
    public DigitalOutput create(DigitalOutputConfig config) {
        MockDigitalOutput output = new MockDigitalOutput(this, config);
        this.context.register(output);
        return output;
    }
}

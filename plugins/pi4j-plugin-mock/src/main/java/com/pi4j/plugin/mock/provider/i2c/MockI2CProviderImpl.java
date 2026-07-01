package com.pi4j.plugin.mock.provider.i2c;

import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.i2c.I2CProviderBase;

/**
 * Default in-memory implementation of {@link MockI2CProvider}, extending {@link I2CProviderBase}.
 * It produces {@link MockI2C} instances that simulate I2C device registers in memory rather than
 * communicating over a real I2C bus.
 */
public class MockI2CProviderImpl extends I2CProviderBase implements MockI2CProvider {

    /**
     * Creates the mock I2C provider, assigning its mock {@link #ID} and {@link #NAME}.
     */
    public MockI2CProviderImpl() {
        this.id = ID;
        this.name = NAME;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Returns a deliberately high priority so that, when the mock plugin is present on the
     * classpath, it is preferred over hardware providers during testing.
     */
    @Override
    public int getPriority() {
        // if the mock is loaded, then we most probably want to use it for testing
        return 1000;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Creates a new {@link MockI2C} instance that simulates the device in memory and registers
     * it with the Pi4J context.
     */
    @Override
    public I2C create(I2CConfig config) {
        MockI2C i2C = new MockI2C(this, config);
        this.context.register(i2C);
        return i2C;
    }
}

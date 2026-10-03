package com.pi4j.plugin.mock.provider.i2c;

import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.i2c.I2CProvider;
import com.pi4j.plugin.mock.Mock;
import com.pi4j.provider.ProviderBase;

/**
 * Default in-memory implementation of {@link MockI2CProvider}, extending {@link ProviderBase}.
 * It produces {@link MockI2C} instances that simulate I2C device registers in memory rather than
 * communicating over a real I2C bus.
 */
public class MockI2CProviderImpl
    extends ProviderBase<I2CProvider, I2C, I2CConfig>
    implements MockI2CProvider {

    /**
     * Creates the mock I2C provider, assigning its mock {@link #ID} and {@link #NAME}.
     */
    public MockI2CProviderImpl() {
        this.id = ID;
        this.name = NAME;
    }

    /**
     * Returns Mock.MOCK_PRIORITY.
     */
    @Override
    public int getPriority() {
        return Mock.MOCK_PROVIDER_PRIORITY;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Creates a new {@link MockI2C} instance that simulates the device in memory.
     */
    @Override
    public I2C create(I2CConfig config) {
        return new MockI2C(context, this, config);
    }
}

package com.pi4j.plugin.mock.provider.i2c;

import com.pi4j.io.i2c.I2CProvider;
import com.pi4j.plugin.mock.Mock;

/**
 * Mock, in-memory {@link I2CProvider} used for testing and for running Pi4J on machines
 * without real I2C hardware. It creates {@link com.pi4j.plugin.mock.provider.i2c.MockI2C}
 * instances that simulate device registers in memory instead of accessing the I2C bus.
 */
public interface MockI2CProvider extends I2CProvider {
    /** The human-readable provider name, {@link Mock#I2C_PROVIDER_NAME}. */
    String NAME = Mock.I2C_PROVIDER_NAME;
    /** The unique provider identifier, {@link Mock#I2C_PROVIDER_ID}. */
    String ID = Mock.I2C_PROVIDER_ID;

    /**
     * Creates a new mock I2C provider instance.
     *
     * @return a new {@link MockI2CProvider} backed by an in-memory implementation
     */
    static MockI2CProvider newInstance() {
        return new MockI2CProviderImpl();
    }
}

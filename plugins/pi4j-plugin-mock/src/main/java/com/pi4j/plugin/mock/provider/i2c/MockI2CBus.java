package com.pi4j.plugin.mock.provider.i2c;

import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CBusBase;
import com.pi4j.io.i2c.I2CConfig;

import java.util.concurrent.Callable;

/**
 * Mock, in-memory implementation of a Pi4J I2C bus, extending the pi4j-core
 * {@link I2CBusBase}. It backs {@link MockI2C} devices and serializes access to the simulated
 * bus without touching any real hardware.
 *
 * @see MockI2C
 */
public class MockI2CBus extends I2CBusBase {

    /**
     * Creates a mock I2C bus for the given configuration.
     *
     * @param config the {@link I2CConfig} describing the bus (and device) being simulated
     */
    public MockI2CBus(I2CConfig config) {
        super(config);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Runs the action under the bus lock inherited from {@link I2CBusBase}; no real hardware
     * transaction is performed.
     */
    @Override
    public <R> R execute(I2C i2c, Callable<R> action) {
        return _execute(i2c, action);
    }
}

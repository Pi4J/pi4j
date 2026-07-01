package com.pi4j.io.i2c;

import com.pi4j.config.BusConfig;
import com.pi4j.config.DeviceConfig;
import com.pi4j.context.Context;
import com.pi4j.io.IOConfig;

/**
 * Immutable configuration for an {@link I2C} device, combining the bus number, device (slave) address and the
 * desired {@link I2CImplementation}. Instances are produced by an {@link I2CConfigBuilder} and consumed by an
 * {@link I2CProvider} when creating a device.
 */
public interface I2CConfig extends IOConfig<I2CConfig>, BusConfig<I2CConfig>, DeviceConfig<I2CConfig> {

    /** Configuration property key identifying the selected {@link I2CImplementation}. */
    String I2C_IMPLEMENTATION = "i2c_implementation";

    /**
     * Returns the low-level access strategy selected for this device.
     *
     * @return the configured {@link I2CImplementation}, or {@code null} if none was specified
     */
    I2CImplementation i2cImplementation();

    /**
     * Returns the low-level access strategy selected for this device.
     *
     * @return the configured {@link I2CImplementation}, or {@code null} if none was specified
     */
    default I2CImplementation getI2CImplementation() {
        return i2cImplementation();
    }

    /**
     * Creates a new configuration builder.
     *
     * @param context the Pi4J runtime context (unused by the current implementation)
     * @return a new {@link I2CConfigBuilder} instance
     * @deprecated As of version 5, please use {@link #newBuilder()} instead.
     */
    static I2CConfigBuilder newBuilder(Context context) {
        return I2CConfigBuilder.newInstance(context);
    }

    /**
     * Creates a new configuration builder.
     *
     * @return a new {@link I2CConfigBuilder} instance
     */
    static I2CConfigBuilder newBuilder() {
        return I2CConfigBuilder.newInstance();
    }

    /**
     * I2C Device Identifier
     * To be able to identify unique I2C devices, an identifier is available which is based on the bus and device value.
     *
     * @return Unique I2C device identifier.
     */
    @Override
    default int getUniqueIdentifier() {
        return (bus() << 8) + device();
    }
}

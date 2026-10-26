package com.pi4j.io.i2c;

import com.pi4j.context.Context;
import com.pi4j.io.IOConfig;

/**
 * Immutable configuration for an {@link I2C} device, combining the bus number, device (slave) address and the
 * desired {@link I2CImplementation}. Instances are produced by an {@link I2CConfigBuilder} when creating a device.
 *
 * @param id the configuration identifier
 * @param name the configuration name
 * @param description the configuration description
 * @param bus the I2C bus number
 * @param device the 7-bit device (slave) address on the I2C bus
 * @param i2cImplementation the low-level access strategy selected for this device
 */
public record I2CConfig(
    @Override String id,
    @Override String name,
    @Override String description,
    Integer bus,
    Integer device,
    I2CImplementation i2cImplementation
) implements IOConfig {

    /** Property key under which the bus number is stored in the configuration properties map. */
    public static final String BUS_KEY = "bus";

    /** Property key under which the device address is stored in the configuration properties map. */
    public static final String DEVICE_KEY = "device";

    /** Configuration property key identifying the selected {@link I2CImplementation}. */
    public static final String I2C_IMPLEMENTATION = "i2c_implementation";

    /**
     * Returns the hardware bus number this I/O is assigned to.
     *
     * @return the bus number, or {@code null} if not configured
     */
    public Integer getBus() {
        return bus;
    }

    /**
     * Returns the target device address this I/O is bound to.
     *
     * @return the device address, or {@code null} if not configured
     */
    public Integer getDevice() {
        return device;
    }

    /**
     * Returns the low-level access strategy selected for this device.
     *
     * @return the configured {@link I2CImplementation}, or {@code null} if none was specified
     */
    public I2CImplementation getI2CImplementation() {
        return i2cImplementation;
    }

    /**
     * Creates a new configuration builder.
     *
     * @param context the Pi4J runtime context (unused by the current implementation)
     * @return a new {@link I2CConfigBuilder} instance
     * @deprecated Please use I2C.newConfigBuilder() instead.
     */
    @Deprecated(since = "5.0")
    public static I2CConfigBuilder newBuilder(Context context) {
        return I2CConfigBuilder.newInstance();
    }

    /**
     * I2C Device Identifier
     * To be able to identify unique I2C devices, an identifier is available which is based on the bus and device value.
     *
     * @return Unique I2C device identifier.
     */
    @Override
    public int getUniqueIdentifier() {
        return (bus == null ? 0 : (bus << 8)) + (device == null ? 0 : device);
    }

    @Override
    public void validate() {
        if (id == null) {
            throw new IllegalStateException("ID cannot be null");
        }
    }
}

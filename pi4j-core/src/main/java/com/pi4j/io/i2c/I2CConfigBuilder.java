package com.pi4j.io.i2c;

import com.pi4j.config.Config;
import com.pi4j.context.Context;
import com.pi4j.io.impl.IOConfigBuilderBase;
import com.pi4j.util.StringUtil;

/**
 * Fluent builder for assembling an {@link I2CConfig}. Callers set the bus number, device address and optional
 * {@link I2CImplementation}, then call {@code build()} to produce the immutable configuration.
 */
public class I2CConfigBuilder extends IOConfigBuilderBase<I2CConfigBuilder, I2CConfig> {

    private Integer bus = null;
    private Integer device = null;
    private I2CImplementation i2cImplementation = I2CImplementation.DIRECT;

    /**
     * Default constructor for creating a new I2C configuration builder.
     */
    public I2CConfigBuilder() {
    }

    /**
     * Creates a new builder instance.
     *
     * @param context the Pi4J runtime context (unused by the current implementation)
     * @return a new {@link I2CConfigBuilder}
     * @deprecated As of version 5, please use {@link #newInstance()} instead.
     */
    @Deprecated(since = "5.0")
    public static I2CConfigBuilder newInstance(Context context) {
        return new I2CConfigBuilder();
    }

    /**
     * Internal method creating a new builder instance; please use I2C.newConfigBuilder() instead.
     *
     * @return a new {@link I2CConfigBuilder}
     */
    public static I2CConfigBuilder newInstance() {
        return new I2CConfigBuilder();
    }

    /**
     * Sets the I2C bus number the device is attached to.
     *
     * @param bus the bus number (e.g. {@code 1} for {@code /dev/i2c-1})
     * @return this builder instance for method chaining
     */
    public I2CConfigBuilder bus(Integer bus) {
        this.bus = bus;
        return this;
    }

    /**
     * Sets the device (slave) address on the I2C bus.
     *
     * @param device the 7-bit device address
     * @return this builder instance for method chaining
     */
    public I2CConfigBuilder device(Integer device) {
        this.device = device;
        return this;
    }

    /**
     * Sets the low-level access strategy to use for this device.
     *
     * @param i2cImplementation the {@link I2CImplementation} to use
     * @return this builder instance for method chaining
     */
    public I2CConfigBuilder i2cImplementation(I2CImplementation i2cImplementation) {
        this.i2cImplementation = i2cImplementation;
        return this;
    }

    @Override
    public I2CConfig build() {
        String defaultId = (bus != null && device != null) ? "I2C-" + bus + "." + device : "I2C";
        String id = StringUtil.setIfNullOrEmpty(this.id(), defaultId, true);
        String name = StringUtil.setIfNullOrEmpty(this.properties.get(Config.NAME_KEY), id, true);
        String description = StringUtil.setIfNullOrEmpty(this.properties.get(Config.DESCRIPTION_KEY), id, true);

        return new I2CConfig(
            id,
            name,
            description,
            this.bus,
            this.device,
            this.i2cImplementation
        );
    }
}

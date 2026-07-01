package com.pi4j.config;

/**
 * Builder contract for configurations that target a numbered device on a bus, such as a chip-select line
 * on an SPI bus or a device address on an I2C bus. Extends {@link ConfigBuilder} with a {@link #device(Integer)}
 * setter and is specialized by transport-specific builders to produce a {@link DeviceConfig}-derived configuration.
 *
 * @param <BUILDER_TYPE> the concrete builder type returned by fluent setters, enabling method chaining on subtypes
 * @param <CONFIG_TYPE> the configuration type produced by this builder
 */
public interface DeviceConfigBuilder<BUILDER_TYPE, CONFIG_TYPE> extends ConfigBuilder<BUILDER_TYPE, CONFIG_TYPE> {
    /**
     * Sets the device number that identifies the target on its bus (for example, the SPI chip-select index or the
     * I2C device address).
     *
     * @param device the device number to target; interpretation depends on the underlying bus/transport
     * @return this builder instance for method chaining
     */
    BUILDER_TYPE device(Integer device);
}

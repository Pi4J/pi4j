package com.pi4j.io.gpio;

import com.pi4j.io.IOBcmConfigBuilder;

/**
 * Fluent builder contract for assembling a {@link GpioConfig}. It extends {@link IOBcmConfigBuilder}
 * to inherit the BCM pin and common I/O configuration setters, adding GPIO-specific bus addressing so
 * concrete GPIO config builders share a consistent fluent API.
 *
 * @param <BUILDER_TYPE> the concrete builder sub-type, returned by fluent setters to enable type-safe chaining
 * @param <CONFIG_TYPE>  the {@link GpioConfig} type produced by this builder
 */
public interface GpioConfigBuilder<BUILDER_TYPE extends GpioConfigBuilder, CONFIG_TYPE extends GpioConfig>
    extends IOBcmConfigBuilder<BUILDER_TYPE, CONFIG_TYPE> {

    /**
     * Sets the bus number on which the GPIO device is addressed.
     *
     * @param bus the bus number to associate with the configured GPIO device
     * @return this builder instance for method chaining
     */
    GpioConfigBuilder<BUILDER_TYPE, CONFIG_TYPE> bus(int bus);
}

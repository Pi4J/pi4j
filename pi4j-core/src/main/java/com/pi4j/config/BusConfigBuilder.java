package com.pi4j.config;

/**
 * Builder contract for assembling a {@link BusConfig}, i.e. the configuration of an I/O that
 * communicates over a numbered hardware bus (I2C, SPI or PWM).
 *
 * @param <BUILDER_TYPE> the concrete builder sub-type, returned by setters to enable type-safe chaining
 * @param <CONFIG_TYPE> the configuration type produced by {@link #build()}
 */
public interface BusConfigBuilder<BUILDER_TYPE, CONFIG_TYPE> extends ConfigBuilder<BUILDER_TYPE, CONFIG_TYPE> {
    /**
     * Sets the hardware bus number this I/O is assigned to.
     *
     * @param bus the bus number
     * @return this builder instance for method chaining
     */
    BUILDER_TYPE bus(Integer bus);
}

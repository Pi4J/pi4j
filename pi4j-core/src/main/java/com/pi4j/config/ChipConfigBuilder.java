package com.pi4j.config;

/**
 * Builder contract for assembling a {@link ChipConfig}, i.e. the configuration of an I/O that is
 * addressed by a controller chip number.
 *
 * @param <BUILDER_TYPE> the concrete builder sub-type, returned by setters to enable type-safe chaining
 * @param <CONFIG_TYPE> the configuration type produced by {@link #build()}
 */
public interface ChipConfigBuilder<BUILDER_TYPE, CONFIG_TYPE> extends ConfigBuilder<BUILDER_TYPE, CONFIG_TYPE> {
    /**
     * Sets the chip number this I/O is assigned to.
     *
     * @param chip the chip number
     * @return this builder instance for method chaining
     */
    BUILDER_TYPE chip(Integer chip);
}

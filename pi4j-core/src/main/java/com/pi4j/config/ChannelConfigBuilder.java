package com.pi4j.config;

/**
 * Builder contract for assembling a {@link ChannelConfig}, i.e. the configuration of an I/O that
 * is addressed by a channel number within a bus (such as SPI or PWM).
 *
 * @param <BUILDER_TYPE> the concrete builder sub-type, returned by setters to enable type-safe chaining
 * @param <CONFIG_TYPE> the configuration type produced by {@link #build()}
 */
public interface ChannelConfigBuilder<BUILDER_TYPE, CONFIG_TYPE> extends ConfigBuilder<BUILDER_TYPE, CONFIG_TYPE> {
    /**
     * Sets the channel number this I/O is assigned to within its bus.
     *
     * @param channel the channel number
     * @return this builder instance for method chaining
     */
    BUILDER_TYPE channel(Integer channel);
}

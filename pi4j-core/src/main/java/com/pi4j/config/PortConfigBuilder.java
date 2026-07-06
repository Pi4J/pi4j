package com.pi4j.config;

/**
 * Builder contract for configurations addressed by a named port. Extends {@link ConfigBuilder} with a
 * {@link #port(String)} setter and is specialized by transport-specific builders to produce a
 * {@link PortConfig}-derived configuration.
 *
 * @param <BUILDER_TYPE> the concrete builder type returned by fluent setters, enabling method chaining on subtypes
 * @param <CONFIG_TYPE>  the configuration type produced by this builder
 */
public interface PortConfigBuilder<BUILDER_TYPE, CONFIG_TYPE> extends ConfigBuilder<BUILDER_TYPE, CONFIG_TYPE> {
    /**
     * Sets the port to target.
     *
     * @param port the port identifier to use for the I/O connection
     * @return this builder instance for method chaining
     */
    BUILDER_TYPE port(String port);
}

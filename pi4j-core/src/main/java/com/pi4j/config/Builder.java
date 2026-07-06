package com.pi4j.config;

/**
 * Generic builder contract for the Pi4J configuration API. Implementations accumulate state through
 * fluent setters and produce a finished instance via {@link #build()}; it is the common super-interface
 * of {@link ConfigBuilder} and the I/O-specific configuration builders.
 *
 * @param <BUILT_TYPE> the type of object produced by {@link #build()}
 */
public interface Builder<BUILT_TYPE> {
    /**
     * Constructs and returns the configured instance from the state collected on this builder.
     *
     * @return the newly built instance
     */
    BUILT_TYPE build();
}

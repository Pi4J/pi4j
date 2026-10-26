package com.pi4j.io;

import com.pi4j.config.ConfigBuilder;

/**
 * Builder marker interface shared by all Pi4J I/O configuration builders.
 * <p>
 * It extends the generic {@link ConfigBuilder}, and is the common parent of the more specific
 * device- and pin-addressed builders.
 *
 * @param <BUILDER_TYPE> the concrete builder type, returned for fluent method chaining
 * @param <CONFIG_TYPE>  the configuration type produced by this builder
 */
public interface IOConfigBuilder<BUILDER_TYPE, CONFIG_TYPE> extends ConfigBuilder<BUILDER_TYPE, CONFIG_TYPE> {
}

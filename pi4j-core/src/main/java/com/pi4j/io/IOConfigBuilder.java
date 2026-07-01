package com.pi4j.io;

import com.pi4j.config.ConfigBuilder;
import com.pi4j.provider.Provider;

/**
 * Builder contract shared by all Pi4J I/O configuration builders.
 * <p>
 * It extends the generic {@link ConfigBuilder} with the ability to select the {@link Provider} that
 * will service the resulting {@link IOConfig}, and is the common parent of the more specific
 * device- and pin-addressed builders such as {@link IODeviceConfigBuilder} and {@link IOBcmConfigBuilder}.
 *
 * @param <BUILDER_TYPE> the concrete builder type, returned for fluent method chaining
 * @param <CONFIG_TYPE>  the configuration type produced by this builder
 */
public interface IOConfigBuilder<BUILDER_TYPE, CONFIG_TYPE> extends ConfigBuilder<BUILDER_TYPE, CONFIG_TYPE> {
    /**
     * Selects the I/O provider to use, identified by its registered provider id.
     *
     * @param provider the provider id (e.g. a plugin's registered provider name)
     * @return this builder for method chaining
     */
    BUILDER_TYPE provider(String provider);

    /**
     * Selects the I/O provider to use by its implementing class.
     *
     * @param providerClass the {@link Provider} implementation class to resolve
     * @return this builder for method chaining
     */
    BUILDER_TYPE provider(Class<? extends Provider> providerClass);
}

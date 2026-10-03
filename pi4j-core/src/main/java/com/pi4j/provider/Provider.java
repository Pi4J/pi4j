package com.pi4j.provider;

import com.pi4j.config.Config;
import com.pi4j.config.ConfigBuilder;
import com.pi4j.io.IO;

/**
 * Legacy interface stub for IO construction; still supported for deprecated ProviderProvider implemented by
 * Context as the corresponding construction pattern was frequently advertised on the website.
 *
 * @deprecated
 */
@Deprecated(since="5.0")
public interface Provider<IO_TYPE extends IO, CONFIG_TYPE extends Config>  {

    /**
     * Creates and returns a new I/O instance configured by the supplied configuration.
     *
     * @param config the configuration describing the I/O instance to create (address, id, options, etc.)
     * @return the newly created I/O instance
     */
    IO_TYPE create(CONFIG_TYPE config);

    default IO_TYPE create(ConfigBuilder<?, CONFIG_TYPE> configBuilder) {
        return create(configBuilder.build());
    }
}

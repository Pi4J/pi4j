package com.pi4j.provider;

import com.pi4j.io.IO;
import com.pi4j.io.IOConfig;
import com.pi4j.io.IOConfigBuilder;

/**
 * Legacy interface stub for IO construction; still supported for deprecated ProviderProvider implemented by
 * Context as the corresponding construction pattern was frequently advertised on the website.
 *
 * @param <IO_TYPE>       the type of {@link IO} instance this provider creates
 * @param <CONFIG_TYPE>   the {@link IOConfig} type consumed when creating an I/O instance
 * @deprecated
 */
@Deprecated(since="5.0")
public interface Provider<IO_TYPE extends IO, CONFIG_TYPE extends IOConfig>  {

    /**
     * Creates and returns a new I/O instance configured by the supplied configuration.
     *
     * @param config the configuration describing the I/O instance to create (address, id, options, etc.)
     * @return the newly created I/O instance
     */
    IO_TYPE create(CONFIG_TYPE config);

    default IO_TYPE create(IOConfigBuilder<?, CONFIG_TYPE> configBuilder) {
        return create(configBuilder.build());
    }
}

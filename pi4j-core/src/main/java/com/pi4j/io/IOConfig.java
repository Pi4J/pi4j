package com.pi4j.io;

import com.pi4j.config.Config;

/**
 * Common configuration contract shared by all Pi4J I/O types.
 * <p>
 * In addition to the generic properties from {@link Config}, it identifies the {@link com.pi4j.provider.Provider}
 * that should service the I/O instance described by this configuration.
 */
public interface IOConfig extends Config {
    /** Configuration property key identifying the I/O provider. */
    String PROVIDER_KEY = "provider";

    /**
     * Returns the id of the provider that should create the I/O instance for this configuration.
     *
     * @return the configured provider id, or {@code null} if none was specified
     */
    @Deprecated
    String provider();

    /**
     * Alias for {@link #provider()} following the JavaBeans getter naming convention.
     *
     * @return the configured provider id, or {@code null} if none was specified
     */
    @Deprecated
    default String getProvider() {
        return provider();
    }
}

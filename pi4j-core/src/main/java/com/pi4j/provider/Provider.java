package com.pi4j.provider;

import com.pi4j.common.Descriptor;
import com.pi4j.config.Config;
import com.pi4j.config.ConfigBuilder;
import com.pi4j.context.Context;
import com.pi4j.extension.Extension;
import com.pi4j.io.IO;
import com.pi4j.io.IOType;
import com.pi4j.io.exception.IOException;

/**
 * A platform-specific factory for a single category of I/O. A provider is an {@link Extension}
 * that knows how to create concrete {@link IO} instances (for example digital, PWM, I2C or SPI)
 * from a {@link Config} for the underlying hardware or operating-system interface it represents.
 * Providers are registered with and resolved through {@link Providers}, and grouped by their
 * {@link IOType} via {@link ProviderGroup}.
 *
 * @param <PROVIDER_TYPE> the concrete provider self-type, returned by lifecycle methods for fluent chaining
 * @param <IO_TYPE>       the type of {@link IO} instance this provider creates
 * @param <CONFIG_TYPE>   the {@link Config} type consumed when creating an I/O instance
 */
public interface Provider<PROVIDER_TYPE extends Provider, IO_TYPE extends IO, CONFIG_TYPE extends Config> extends Extension<PROVIDER_TYPE> {

    /**
     * Returns the Pi4J {@link Context} this provider was initialized with, or {@code null} if it
     * has not yet been initialized.
     *
     * @return the runtime context bound to this provider
     */
    Context context();

    /**
     * Creates and returns a new I/O instance configured by the supplied configuration.
     *
     * @param config the configuration describing the I/O instance to create (address, id, options, etc.)
     * @return the newly created I/O instance
     */
    IO_TYPE create(CONFIG_TYPE config);

    /**
     * Returns the {@link IOType} this provider supplies, derived from its provider class.
     *
     * @return the I/O type (digital input/output, PWM, I2C, SPI, etc.) handled by this provider
     */
    default IOType type() { return IOType.getByProviderClass(this.getClass()); }

    /**
     * Returns the {@link IOType} this provider supplies.
     *
     * @return the I/O type handled by this provider
     * @see #type()
     */
    default IOType getType() { return type(); }

    /**
     * Returns the selection priority for this provider. When multiple providers are available for
     * the same {@link IOType}, a higher priority is preferred when choosing a default. Defaults to 0.
     *
     * @return the provider priority; higher values take precedence
     */
    default int getPriority() {
        return 0;
    }

    /**
     * Indicates whether this provider supplies the given I/O type.
     *
     * @param type the I/O type to test against this provider's own type
     * @return {@code true} if this provider's {@link #type()} matches the given type
     */
    default boolean isType(IOType type) { return this.type().isType(type); }

    /**
     * {@inheritDoc}
     * <p>
     * The returned descriptor is categorized as {@code "PROVIDER"} rather than the generic
     * extension category.
     */
    @Override
    default Descriptor describe() {
        Descriptor descriptor = Extension.super.describe();
        //descriptor.category(this.type().name());
        descriptor.category("PROVIDER");
        return descriptor;
    }

    /**
     * Creates a new I/O instance with the given id, using a default configuration built from this
     * provider's {@link IOType} and the bound {@link Context}.
     *
     * @param id the unique identifier to assign to the new I/O instance
     * @return the newly created I/O instance
     * @throws IOException if this provider has not been initialized with a Pi4J context
     */
    default IO_TYPE create(String id) {
        // validate context
        if(context() == null) throw new IOException("Unable to create IO instance; this provider has not been 'initialized()' with a Pi4J context.");

        // create IO instance
        ConfigBuilder builder = type().newConfigBuilder(context());
        builder.id(id);
        return (IO_TYPE)create((CONFIG_TYPE) builder.build());
    }
}

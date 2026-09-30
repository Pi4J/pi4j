package com.pi4j.io;

import com.pi4j.common.Descriptor;
import com.pi4j.common.IdentityBase;
import com.pi4j.context.Context;
import com.pi4j.exception.InitializeException;
import com.pi4j.provider.Provider;

import java.io.Closeable;

/**
 * Abstract base implementation of {@link IO} that the concrete I/O types build upon.
 * <p>
 * It stores the originating {@link Provider} and {@link IOConfig}, initializes its {@link Identity}
 * (id, name, description) from that configuration, and integrates with the Pi4J {@link Context}
 * lifecycle so that {@link #close()} reliably unregisters and shuts the instance down exactly once.
 *
 * @param <IO_TYPE>       the concrete I/O type, returned by the fluent identity setters for chaining
 * @param <CONFIG_TYPE>   the {@link IOConfig} type describing this instance
 * @param <PROVIDER_TYPE> the {@link Provider} type backing this instance
 */
public abstract class IOBase<IO_TYPE extends IO, CONFIG_TYPE extends IOConfig, PROVIDER_TYPE extends Provider>
        extends IdentityBase implements IO<IO_TYPE,CONFIG_TYPE, PROVIDER_TYPE>, Closeable {

    protected final CONFIG_TYPE config;
    protected final PROVIDER_TYPE provider;
    protected final Context context;
    // close() requires idempotency.
    protected boolean closed = false;

    @Override
    public PROVIDER_TYPE provider(){
        return this.provider;
    }

    /**
     * Creates a new I/O instance, copying the id, name and description from the supplied
     * configuration into this instance's identity.
     *
     * @param provider the provider that created and backs this instance
     * @param config   the configuration defining this instance's identity and properties
     */
    public IOBase(Context context, PROVIDER_TYPE provider, CONFIG_TYPE config){
        super();
        this.id = config.id();
        this.name = config.name();
        this.description = config.description();
        this.provider = provider;
        this.config = config;
        this.context = context;
    }

    @Override
    public IO_TYPE name(String name){
        this.name = name;
        return (IO_TYPE)this;
    }

    @Override
    public IO_TYPE description(String description){
        this.description = description;
        return (IO_TYPE)this;
    }

    /**
     * Closes the driver and calls this.context().shutdown(this), which removes this IO instance from the context
     * registry.
     * <p>
     * Note for subclass implementations: As context.shoutdown(IO) might be called directly for historical reasons,
     * it needs to call this method again. To prevent an infinite cycles, it's important that the idempotency aspect
     * of the close contract is strictly observed.
     * <p>
     */
    @Override
    public void close() {
        // The closed check ensures idempotency, as required by the close method contract.
        if (closed) {
            return;
        }
        closed = true;
        // The null check accounts for contextless tests or somehow just closing without initializing,
        // although we probably should make context a required ctor parameter, see #719
        if (this.context != null) {
            this.context.shutdown(this);
        }
    }

    @Override
    public final boolean isOpen() {
        return !closed;
    }

    @Override
    public CONFIG_TYPE config(){
        return this.config;
    }


    @Override
    public IO_TYPE initialize(Context context) throws InitializeException {
        if (this.context != null && context != this.context &&
            this.context != null) {
            throw new IllegalArgumentException("Context mismatch");
        }
        return (IO_TYPE) this;
    }

    @Override
    public Descriptor describe() {
        return super.describe().category("IO");
    }
}

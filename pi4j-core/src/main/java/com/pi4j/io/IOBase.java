package com.pi4j.io;

import com.pi4j.common.Descriptor;
import com.pi4j.common.IdentityBase;
import com.pi4j.context.Context;
import com.pi4j.exception.InitializeException;
import com.pi4j.exception.ShutdownException;
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

    protected CONFIG_TYPE config;
    protected PROVIDER_TYPE provider;
    private Context context;
    // close() requires idempotency.
    private boolean closed = false;

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
    public IOBase(PROVIDER_TYPE provider, CONFIG_TYPE config){
        super();
        this.id = config.id();
        this.name = config.name();
        this.description = config.description();
        this.provider = provider;
        this.config = config;
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
     * Closes the driver by calling this.context().shutdown(this.getId()), which in turn calls
     * the local shutdown() method here via DefaultRuntimeRegistry.remove().
     * <p>
     * Basically, for Pi4J, this constitutes an idempotent user convenience method for
     * this.context().shutdown(this.getId())
     * <p>
     * Subclasses should typically override the local shutdown method with implementation-specific shutdown
     * behaviour. Behaviour added here will not be triggered by context.shutdown()
     */
    @Override
    public void close() {
        // The null check accounts for contextless tests or somehow just closing without initializing
        // The closed check ensures idempotency, as required by the close method contract.
        if (this.context != null && !closed) {
            this.closed = true;
            this.context.shutdown(getId());
        }
    }

    @Override
    public CONFIG_TYPE config(){
        return this.config;
    }

    /**
     * Returns the Pi4J {@link Context} this instance was initialized with, for use by subclasses.
     *
     * @return the owning context, or {@code null} if the instance has not been initialized
     */
    protected Context context() {
        return this.context;
    }

    @Override
    public IO_TYPE initialize(Context context) throws InitializeException {
        this.context = context;
        return (IO_TYPE) this;
    }

    @Override
    public IO_TYPE shutdownInternal(Context context) throws ShutdownException {
        // Close is supposed to be idempotent. We interpret this here to include effective shutdowns by
        // other means, i.e. the infrastructure calling this method.
        this.closed = true;
        if (context != this.context) {
            throw new IllegalArgumentException("The context parameter and the local context don't match.");
        }
        return (IO_TYPE) this;
    }

    @Override
    public Descriptor describe() {
        return super.describe().category("IO");
    }
}

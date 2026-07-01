package com.pi4j.provider;

import com.pi4j.config.Config;
import com.pi4j.context.Context;
import com.pi4j.exception.InitializeException;
import com.pi4j.exception.LifecycleException;
import com.pi4j.exception.ShutdownException;
import com.pi4j.extension.ExtensionBase;
import com.pi4j.io.IO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Base class for {@link Provider} implementations. It captures the Pi4J {@link Context} on
 * {@link #initialize(Context)}, exposes it via {@link #context()}, and on shutdown automatically
 * shuts down every {@link IO} instance created by this provider that is tracked in the registry.
 * Concrete providers extend this class and supply the {@code create(...)} logic for their
 * specific {@link IO} type.
 *
 * @param <PROVIDER_TYPE> the concrete provider self-type, returned by lifecycle methods for fluent chaining
 * @param <IO_TYPE>       the type of {@link IO} instance this provider creates
 * @param <CONFIG_TYPE>   the {@link Config} type consumed when creating an I/O instance
 */
public abstract class ProviderBase<PROVIDER_TYPE extends Provider, IO_TYPE extends IO, CONFIG_TYPE extends Config>
        extends ExtensionBase<PROVIDER_TYPE>
        implements Provider<PROVIDER_TYPE, IO_TYPE, CONFIG_TYPE> {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    protected Context context;

    /**
     * Creates a provider without an assigned id or name; an id may be derived later by the base
     * extension behaviour.
     */
    public ProviderBase(){
        super();
    }

    /**
     * Creates a provider with the given unique identifier.
     *
     * @param id the unique identifier for this provider
     */
    public ProviderBase(String id){
        super(id);
    }

    /**
     * Creates a provider with the given unique identifier and human-readable name.
     *
     * @param id   the unique identifier for this provider
     * @param name the human-readable display name for this provider
     */
    public ProviderBase(String id, String name){
        super(id, name);
    }

    @Override
    public PROVIDER_TYPE initialize(Context context) throws InitializeException {
        this.context = context;
        return (PROVIDER_TYPE)this;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Shuts down every {@link IO} instance created by this provider that is still tracked in the
     * context registry. Failures shutting down an individual instance are logged and do not abort
     * the remaining shutdowns.
     */
    @Override
    public PROVIDER_TYPE shutdownInternal(Context context) throws ShutdownException {

        // TODO :: ABSTRACT PROVIDER IO INSTANCE SHUTDOWN VIA PROXY IMPL

        // perform a shutdown on each digital I/O instance that is tracked in the internal cache
        Map<String, IO> instances = context.registry().allByProvider(this.id(), IO.class);
        instances.forEach((address, instance)->{
            try {
                instance.shutdownInternal(context);
            } catch (LifecycleException e) {
                logger.error(e.getMessage(), e);
            }
        });
        return (PROVIDER_TYPE)this;
    }

    @Override
    public Context context(){
        return this.context;
    }
}

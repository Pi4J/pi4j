package com.pi4j.io.gpio;

import com.pi4j.io.IO;
import com.pi4j.provider.ProviderBase;

/**
 * Abstract base implementation for {@link GpioProvider}s. It extends {@link ProviderBase} to supply the
 * shared id/name identity and provider lifecycle plumbing, leaving concrete GPIO providers to implement
 * the actual creation of GPIO I/O instances for a given platform or device.
 *
 * @param <PROVIDER_TYPE> the concrete GPIO provider sub-type
 * @param <IO_TYPE>       the {@link IO} type this provider creates
 * @param <CONFIG_TYPE>   the {@link GpioConfig} type accepted when creating I/O instances
 */
public abstract class GpioProviderBase<
            PROVIDER_TYPE extends GpioProvider,
            IO_TYPE extends IO,
            CONFIG_TYPE extends GpioConfig>
        extends ProviderBase<PROVIDER_TYPE, IO_TYPE, CONFIG_TYPE>
        implements GpioProvider<PROVIDER_TYPE, IO_TYPE, CONFIG_TYPE> {

    /**
     * Creates a new GPIO provider without an explicit id or name; subclasses are expected to supply
     * their identity by other means.
     */
    public GpioProviderBase(){
        super();
    }

    /**
     * Creates a new GPIO provider with the given unique identifier.
     *
     * @param id the unique identifier used to register and look up this provider
     */
    public GpioProviderBase(String id){
        super(id);
    }

    /**
     * Creates a new GPIO provider with the given unique identifier and human-readable name.
     *
     * @param id   the unique identifier used to register and look up this provider
     * @param name the human-readable display name of this provider
     */
    public GpioProviderBase(String id, String name){
        super(id, name);
    }
}

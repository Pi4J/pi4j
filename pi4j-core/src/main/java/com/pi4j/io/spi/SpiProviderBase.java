package com.pi4j.io.spi;

import com.pi4j.provider.ProviderBase;

/**
 * Base class for {@link SpiProvider} implementations, supplying identity handling on top of
 * {@link ProviderBase}. Concrete plugins extend this class and implement creation of {@link Spi}
 * devices for their target platform.
 */
public abstract class SpiProviderBase
        extends ProviderBase<SpiProvider, Spi, SpiConfig>
        implements SpiProvider {

    /**
     * Creates a provider with no preset identifier; the id is assigned later by the runtime.
     */
    public SpiProviderBase(){
        super();
    }

    /**
     * Creates a provider with the given unique identifier.
     *
     * @param id the unique provider id used to register and look up this provider
     */
    public SpiProviderBase(String id){
        super(id);
    }

    /**
     * Creates a provider with the given identifier and human-readable name.
     *
     * @param id   the unique provider id used to register and look up this provider
     * @param name a human-readable display name for this provider
     */
    public SpiProviderBase(String id, String name){
        super(id, name);
    }
}

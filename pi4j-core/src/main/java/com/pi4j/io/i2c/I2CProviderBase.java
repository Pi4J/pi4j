package com.pi4j.io.i2c;

import com.pi4j.provider.ProviderBase;

/**
 * Base class for {@link I2CProvider} implementations, supplying the common {@link com.pi4j.provider.ProviderBase}
 * plumbing so concrete providers only need to implement device creation. Platform plugins extend this to expose
 * their I2C support to Pi4J.
 */
public abstract class I2CProviderBase
        extends ProviderBase<I2CProvider, I2C, I2CConfig>
        implements I2CProvider {

    /**
     * Creates a provider with auto-generated identifier and name.
     */
    public I2CProviderBase(){
        super();
    }

    /**
     * Creates a provider with the given identifier.
     *
     * @param id the unique identifier used to register and look up this provider
     */
    public I2CProviderBase(String id){
        super(id);
    }

    /**
     * Creates a provider with the given identifier and display name.
     *
     * @param id   the unique identifier used to register and look up this provider
     * @param name the human-readable display name of this provider
     */
    public I2CProviderBase(String id, String name){
        super(id, name);
    }
}

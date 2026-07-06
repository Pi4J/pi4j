package com.pi4j.io.gpio.digital;

/**
 * Abstract base class for {@link DigitalInputProvider} implementations, supplying the common provider
 * identity handling from {@link DigitalProviderBase}. Concrete platform or expander providers extend this
 * to implement the actual digital input creation.
 */
public abstract class DigitalInputProviderBase
        extends DigitalProviderBase<DigitalInputProvider, DigitalInput, DigitalInputConfig>
        implements DigitalInputProvider {

    /**
     * Creates a provider with no preset identity; the id and name are expected to be supplied later.
     */
    public DigitalInputProviderBase(){
        super();
    }

    /**
     * Creates a provider with the given unique identifier.
     *
     * @param id the provider's unique identifier
     */
    public DigitalInputProviderBase(String id){
        super(id);
    }

    /**
     * Creates a provider with the given unique identifier and display name.
     *
     * @param id the provider's unique identifier
     * @param name the provider's human-readable name
     */
    public DigitalInputProviderBase(String id, String name){
        super(id, name);
    }
}

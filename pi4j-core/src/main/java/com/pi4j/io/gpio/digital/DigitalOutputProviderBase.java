package com.pi4j.io.gpio.digital;

/**
 * Base class for {@link DigitalOutputProvider} implementations, supplying the common provider identity
 * handling so concrete platform providers only need to implement output creation.
 */
public abstract class DigitalOutputProviderBase
        extends DigitalProviderBase<DigitalOutputProvider, DigitalOutput, DigitalOutputConfig>
        implements DigitalOutputProvider {

    /**
     * Creates a provider with no preset identifier; the identifier is expected to be supplied later.
     */
    public DigitalOutputProviderBase(){
        super();
    }

    /**
     * Creates a provider with the given identifier.
     *
     * @param id the unique provider identifier
     */
    public DigitalOutputProviderBase(String id){
        super(id);
    }

    /**
     * Creates a provider with the given identifier and name.
     *
     * @param id   the unique provider identifier
     * @param name the human-readable provider name
     */
    public DigitalOutputProviderBase(String id, String name){
        super(id, name);
    }
}

package com.pi4j.io.pwm;

import com.pi4j.provider.ProviderBase;

/**
 * Abstract base class for {@link PwmProvider} implementations, supplying the common
 * provider plumbing inherited from {@link ProviderBase}. Concrete PWM providers
 * extend this class and implement the creation of platform-specific {@link Pwm}
 * instances.
 */
public abstract class PwmProviderBase
        extends ProviderBase<PwmProvider, Pwm, PwmConfig>
        implements PwmProvider {

    /** Creates a provider with no predefined identifier or name. */
    public PwmProviderBase(){
        super();
    }

    /**
     * Creates a provider with the given identifier.
     *
     * @param id the unique provider identifier
     */
    public PwmProviderBase(String id){
        super(id);
    }

    /**
     * Creates a provider with the given identifier and name.
     *
     * @param id   the unique provider identifier
     * @param name the human-readable provider name
     */
    public PwmProviderBase(String id, String name){
        super(id, name);
    }
}

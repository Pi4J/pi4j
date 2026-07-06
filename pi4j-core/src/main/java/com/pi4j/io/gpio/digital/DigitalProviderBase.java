package com.pi4j.io.gpio.digital;

import com.pi4j.io.gpio.GpioProviderBase;

/**
 * Base class for {@link DigitalProvider} implementations, inheriting the shared GPIO provider plumbing from
 * {@link GpioProviderBase} and serving as the common ancestor of the digital input and output provider base
 * classes such as {@link DigitalOutputProviderBase}.
 *
 * @param <PROVIDER_TYPE> the concrete provider type
 * @param <DIGITAL_TYPE>  the concrete {@link Digital} I/O type this provider creates
 * @param <CONFIG_TYPE>   the {@link DigitalConfig} type used to configure created instances
 */
public abstract class DigitalProviderBase<
            PROVIDER_TYPE extends DigitalProvider,
            DIGITAL_TYPE extends Digital,
            CONFIG_TYPE extends DigitalConfig>
        extends GpioProviderBase<PROVIDER_TYPE, DIGITAL_TYPE, CONFIG_TYPE>
        implements DigitalProvider<PROVIDER_TYPE, DIGITAL_TYPE, CONFIG_TYPE> {

    /**
     * Creates a provider with no preset identifier; the identifier is expected to be supplied later.
     */
    public DigitalProviderBase(){
        super();
    }

    /**
     * Creates a provider with the given identifier.
     *
     * @param id the unique provider identifier
     */
    public DigitalProviderBase(String id){
        super(id);
    }

    /**
     * Creates a provider with the given identifier and name.
     *
     * @param id   the unique provider identifier
     * @param name the human-readable provider name
     */
    public DigitalProviderBase(String id, String name){
        super(id, name);
    }
}

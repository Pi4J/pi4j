package com.pi4j.io.gpio.digital;

/**
 * Abstract base class for {@link DigitalInput} implementations, specializing {@link DigitalBase}
 * with the digital-input type parameters. Provider-specific subclasses extend this to supply the
 * actual hardware or expander read behaviour.
 */
public abstract class DigitalInputBase extends DigitalBase<DigitalInput, DigitalInputConfig, DigitalInputProvider> implements DigitalInput {
    /**
     * Creates a digital input bound to the given provider and configuration.
     *
     * @param provider the {@link DigitalInputProvider} responsible for this input's underlying I/O
     * @param config the configuration describing this input (pin, pull resistance, debounce, etc.)
     */
    public DigitalInputBase(DigitalInputProvider provider, DigitalInputConfig config){
        super(provider, config);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Returns a provider-agnostic {@link DefaultPinReconfigurer} that releases this input's GPIO line
     * and re-requests it as an output or input through the context's registered providers.
     */
    @Override
    public PinReconfigurer reconfigure() {
        return new DefaultPinReconfigurer(this, context());
    }
}

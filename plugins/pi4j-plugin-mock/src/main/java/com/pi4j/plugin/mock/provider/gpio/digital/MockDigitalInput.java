package com.pi4j.plugin.mock.provider.gpio.digital;


import com.pi4j.io.gpio.digital.*;

/**
 * Mock, in-memory implementation of the pi4j-core {@link DigitalInput} contract.
 * <p>
 * Instead of reading the state of a real GPIO pin, this implementation holds the pin
 * state in memory (defaulting to {@link DigitalState#LOW}). Tests drive the simulated
 * input level through {@link #mockState(DigitalState)}, which also dispatches a
 * {@link DigitalStateChangeEvent} so that registered listeners behave as if a real
 * hardware transition occurred.
 *
 * @see MockDigitalInputProvider
 */
public class MockDigitalInput extends DigitalInputBase implements DigitalInput {

    private DigitalState state = DigitalState.LOW;

    /**
     * Creates a mock digital input bound to the given provider and configuration.
     *
     * @param provider the {@link DigitalInputProvider} that created this instance
     * @param config the {@link DigitalInputConfig} describing the pin, pull resistance and other settings
     */
    public MockDigitalInput(DigitalInputProvider provider, DigitalInputConfig config){
        super(provider, config);
    }

    @Override
    public DigitalState state() {
        return this.state;
    }

    /**
     * Test helper that simulates a change of the input pin level. When the supplied state
     * differs from the current one, the in-memory state is updated and a
     * {@link DigitalStateChangeEvent} is dispatched to all registered listeners, mimicking
     * the signal edge a real GPIO input would produce. Identical states are ignored.
     *
     * @param state the simulated input level to apply, e.g. {@link DigitalState#HIGH} or {@link DigitalState#LOW}
     * @return this instance for method chaining
     */
    public MockDigitalInput mockState(DigitalState state){
        if(!this.state.equals(state)) {
            this.state = state;
            this.dispatch(new DigitalStateChangeEvent(this, this.state));
        }
        return this;
    }
}

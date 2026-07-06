package com.pi4j.plugin.mock.provider.gpio.digital;


import com.pi4j.io.exception.IOException;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalOutputBase;
import com.pi4j.io.gpio.digital.DigitalOutputConfig;
import com.pi4j.io.gpio.digital.DigitalOutputProvider;
import com.pi4j.io.gpio.digital.DigitalState;


/**
 * Mock, in-memory implementation of the pi4j-core {@link DigitalOutput} contract.
 * <p>
 * Rather than driving a real GPIO pin, this implementation stores the output state in memory
 * via the inherited {@link DigitalOutputBase} behaviour, so output code can be exercised in
 * unit tests without hardware.
 *
 * @see MockDigitalOutputProvider
 */
public class MockDigitalOutput extends DigitalOutputBase implements DigitalOutput {
    /**
     * Creates a mock digital output bound to the given provider and configuration.
     *
     * @param provider the {@link DigitalOutputProvider} that created this instance
     * @param config the {@link DigitalOutputConfig} describing the pin and its initial/shutdown states
     */
    public MockDigitalOutput(DigitalOutputProvider provider, DigitalOutputConfig config){
        super(provider, config);
    }

    /**
     * Test helper that simulates setting the output pin level. Delegates to
     * {@link #state(DigitalState)} so listeners and the in-memory state are updated exactly
     * as a normal write would, then returns this instance for chaining.
     *
     * @param state the simulated output level to apply, e.g. {@link DigitalState#HIGH} or {@link DigitalState#LOW}
     * @return this instance for method chaining
     * @throws IOException if applying the state fails
     */
    public MockDigitalOutput mockState(DigitalState state) throws IOException {
        this.state(state);
        return this;
    }
}

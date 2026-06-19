package com.pi4j.plugin.mock.provider.gpio.digital;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: PLUGIN   :: Mock Platform & Providers
 * FILENAME      :  MockDigitalInput.java
 *
 * This file is part of the Pi4J project. More information about
 * this project can be found here:  https://pi4j.com/
 * **********************************************************************
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */


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

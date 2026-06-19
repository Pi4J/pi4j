package com.pi4j.plugin.mock.provider.gpio.digital;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: PLUGIN   :: Mock Platform & Providers
 * FILENAME      :  MockDigitalOutput.java
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

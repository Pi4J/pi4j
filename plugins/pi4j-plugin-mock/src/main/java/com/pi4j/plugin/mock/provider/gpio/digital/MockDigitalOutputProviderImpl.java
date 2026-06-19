package com.pi4j.plugin.mock.provider.gpio.digital;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: PLUGIN   :: Mock Platform & Providers
 * FILENAME      :  MockDigitalOutputProviderImpl.java
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

import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalOutputConfig;
import com.pi4j.io.gpio.digital.DigitalOutputProviderBase;

/**
 * Default implementation of {@link MockDigitalOutputProvider}. Extends the pi4j-core
 * {@link DigitalOutputProviderBase} and produces {@link MockDigitalOutput} instances that
 * simulate GPIO outputs entirely in memory for use in unit tests.
 *
 * @see MockDigitalOutput
 */
public class MockDigitalOutputProviderImpl extends DigitalOutputProviderBase implements MockDigitalOutputProvider {

    /**
     * Creates the provider and assigns its mock {@link #ID} and {@link #NAME}.
     */
    public MockDigitalOutputProviderImpl() {
        this.id = ID;
        this.name = NAME;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Returns a deliberately high priority ({@code 1000}) so that, when the mock plugin is on
     * the classpath, it is preferred over real hardware providers during testing.
     */
    @Override
    public int getPriority() {
        // if the mock is loaded, then we most probably want to use it for testing
        return 1000;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Creates a {@link MockDigitalOutput} that simulates the pin in memory and registers it
     * with the Pi4J context.
     */
    @Override
    public DigitalOutput create(DigitalOutputConfig config) {
        MockDigitalOutput output = new MockDigitalOutput(this, config);
        this.context.register(output);
        return output;
    }
}

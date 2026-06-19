package com.pi4j.plugin.mock.provider.gpio.digital;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: PLUGIN   :: Mock Platform & Providers
 * FILENAME      :  MockDigitalOutputProvider.java
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

import com.pi4j.io.gpio.digital.DigitalOutputProvider;
import com.pi4j.plugin.mock.Mock;

/**
 * Mock implementation of the pi4j-core {@link DigitalOutputProvider} contract. This provider
 * creates {@link MockDigitalOutput} instances backed by in-memory state instead of real GPIO
 * hardware, allowing digital-output code to be exercised in unit tests.
 *
 * @see MockDigitalOutput
 */
public interface MockDigitalOutputProvider extends DigitalOutputProvider {
    /** Unique provider name, as defined by {@link Mock#DIGITAL_OUTPUT_PROVIDER_NAME}. */
    String NAME = Mock.DIGITAL_OUTPUT_PROVIDER_NAME;
    /** Unique provider identifier, as defined by {@link Mock#DIGITAL_OUTPUT_PROVIDER_ID}. */
    String ID = Mock.DIGITAL_OUTPUT_PROVIDER_ID;

    /**
     * Creates a new mock digital output provider instance.
     *
     * @return a new {@link MockDigitalOutputProvider} backed by {@link MockDigitalOutputProviderImpl}
     */
    static MockDigitalOutputProvider newInstance() {
        return new MockDigitalOutputProviderImpl();
    }
}

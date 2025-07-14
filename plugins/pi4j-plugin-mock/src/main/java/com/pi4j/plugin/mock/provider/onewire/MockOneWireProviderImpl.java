package com.pi4j.plugin.mock.provider.onewire;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: PLUGIN   :: Mock Platform & Providers
 * FILENAME      :  MockOneWireProviderImpl.java
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

import com.pi4j.io.onewire.OneWire;
import com.pi4j.io.onewire.OneWireConfig;
import com.pi4j.io.onewire.OneWireProviderBase;

public class MockOneWireProviderImpl extends OneWireProviderBase implements MockOneWireProvider {

    /**
     * <p>Constructor for MockSerialProviderImpl.</p>
     */
    public MockOneWireProviderImpl() {
        this.id = ID;
        this.name = NAME;
    }

    @Override
    public OneWire create(OneWireConfig config) {
        MockOneWire oneWire = new MockOneWire(this, config);
        this.context.registry().add(oneWire);
        return oneWire;
    }

    @Override
    public int getPriority() {
        // if the mock is loaded, then we most probably want to use it for testing
        return 1000;
    }
}

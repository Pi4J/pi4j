package com.pi4j.plugin.mock.provider.i2c;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: PLUGIN   :: Mock Platform & Providers
 * FILENAME      :  MockI2CProvider.java
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

import com.pi4j.io.i2c.I2CProvider;
import com.pi4j.plugin.mock.Mock;

/**
 * <p>MockI2CProvider interface.</p>
 */
public interface MockI2CProvider extends I2CProvider {
    /** Constant <code>NAME="Mock.I2C_PROVIDER_NAME"</code> */
    String NAME = Mock.I2C_PROVIDER_NAME;
    /** Constant <code>ID="Mock.I2C_PROVIDER_ID"</code> */
    String ID = Mock.I2C_PROVIDER_ID;
    static MockI2CProvider newInstance() {
        return new MockI2CProviderImpl();
    }
}

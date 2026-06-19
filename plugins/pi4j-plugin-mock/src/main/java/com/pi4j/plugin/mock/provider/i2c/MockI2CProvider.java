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
 * Mock, in-memory {@link I2CProvider} used for testing and for running Pi4J on machines
 * without real I2C hardware. It creates {@link com.pi4j.plugin.mock.provider.i2c.MockI2C}
 * instances that simulate device registers in memory instead of accessing the I2C bus.
 */
public interface MockI2CProvider extends I2CProvider {
    /** The human-readable provider name, {@link Mock#I2C_PROVIDER_NAME}. */
    String NAME = Mock.I2C_PROVIDER_NAME;
    /** The unique provider identifier, {@link Mock#I2C_PROVIDER_ID}. */
    String ID = Mock.I2C_PROVIDER_ID;

    /**
     * Creates a new mock I2C provider instance.
     *
     * @return a new {@link MockI2CProvider} backed by an in-memory implementation
     */
    static MockI2CProvider newInstance() {
        return new MockI2CProviderImpl();
    }
}

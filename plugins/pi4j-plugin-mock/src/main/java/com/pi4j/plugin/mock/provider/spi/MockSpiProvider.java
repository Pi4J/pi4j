package com.pi4j.plugin.mock.provider.spi;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: PLUGIN   :: Mock Platform & Providers
 * FILENAME      :  MockSpiProvider.java
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

import com.pi4j.io.spi.SpiProvider;
import com.pi4j.plugin.mock.Mock;

/**
 * Mock, in-memory {@link SpiProvider} used for testing and for running Pi4J on machines
 * without real SPI hardware. It creates {@link MockSpi} instances that exchange bytes through
 * an in-memory buffer instead of communicating over a real SPI bus.
 */
public interface MockSpiProvider extends SpiProvider{
    /** The human-readable provider name, {@link Mock#SPI_PROVIDER_NAME}. */
    String NAME = Mock.SPI_PROVIDER_NAME;
    /** The unique provider identifier, {@link Mock#SPI_PROVIDER_ID}. */
    String ID = Mock.SPI_PROVIDER_ID;

    /**
     * Creates a new mock SPI provider instance.
     *
     * @return a new {@link MockSpiProvider} backed by an in-memory implementation
     */
    static MockSpiProvider newInstance() {
        return new MockSpiProviderImpl();
    }
}

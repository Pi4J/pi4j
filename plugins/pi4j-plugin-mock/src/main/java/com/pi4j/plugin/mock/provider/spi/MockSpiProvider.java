package com.pi4j.plugin.mock.provider.spi;

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

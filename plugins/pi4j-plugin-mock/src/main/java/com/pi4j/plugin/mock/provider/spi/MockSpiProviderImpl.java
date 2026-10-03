package com.pi4j.plugin.mock.provider.spi;

import com.pi4j.io.spi.Spi;
import com.pi4j.io.spi.SpiConfig;
import com.pi4j.io.spi.SpiProvider;
import com.pi4j.plugin.mock.Mock;
import com.pi4j.provider.ProviderBase;

/**
 * Default in-memory implementation of {@link MockSpiProvider}, extending {@link ProviderBase}.
 * It produces {@link MockSpi} instances that exchange bytes through an in-memory buffer rather than
 * communicating over a real SPI bus.
 */
public class MockSpiProviderImpl
    extends ProviderBase<SpiProvider, Spi, SpiConfig>
    implements MockSpiProvider {

    /**
     * Creates the mock SPI provider, assigning its mock {@link #ID} and {@link #NAME}.
     */
    public MockSpiProviderImpl() {
        this.id = ID;
        this.name = NAME;
    }

    /**
     * Returns Mock.MOCK_PROVIDER_PRIORITY.
     */
    @Override
    public int getPriority() {
        return Mock.MOCK_PROVIDER_PRIORITY;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Creates a new {@link MockSpi} instance that simulates the SPI device in memory.
     */
    @Override
    public Spi create(SpiConfig config) {
        return new MockSpi(context, this, config);
    }
}

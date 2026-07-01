package com.pi4j.plugin.mock.provider.spi;

import com.pi4j.io.spi.Spi;
import com.pi4j.io.spi.SpiConfig;
import com.pi4j.io.spi.SpiProviderBase;

/**
 * Default in-memory implementation of {@link MockSpiProvider}, extending {@link SpiProviderBase}.
 * It produces {@link MockSpi} instances that exchange bytes through an in-memory buffer rather than
 * communicating over a real SPI bus.
 */
public class MockSpiProviderImpl extends SpiProviderBase implements MockSpiProvider {

    /**
     * Creates the mock SPI provider, assigning its mock {@link #ID} and {@link #NAME}.
     */
    public MockSpiProviderImpl() {
        this.id = ID;
        this.name = NAME;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Returns a deliberately high priority so that, when the mock plugin is present on the
     * classpath, it is preferred over hardware providers during testing.
     */
    @Override
    public int getPriority() {
        // if the mock is loaded, then we most probably want to use it for testing
        return 1000;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Creates a new {@link MockSpi} instance that simulates the SPI device in memory and registers
     * it with the Pi4J context.
     */
    @Override
    public Spi create(SpiConfig config) {
        MockSpi spi = new MockSpi(this, config);
        this.context.register(spi);
        return spi;
    }
}

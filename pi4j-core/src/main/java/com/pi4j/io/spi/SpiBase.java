package com.pi4j.io.spi;

import com.pi4j.io.IOBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for {@link Spi} implementations, providing common open/closed state tracking on top of
 * {@link IOBase}. Concrete providers extend this class and implement the actual byte-transfer logic
 * defined by {@link Spi}.
 */
public abstract class SpiBase extends IOBase<Spi, SpiConfig, SpiProvider> implements Spi {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    /** Tracks whether this SPI device is currently open; managed by {@link #open()} and {@link #close()}. */
    protected boolean isOpen = false;

    /**
     * Creates a new SPI device instance bound to the given provider and configuration.
     *
     * @param provider the {@link SpiProvider} that created and backs this SPI device
     * @param config   the {@link SpiConfig} describing the bus, channel, mode, and clock settings to use
     */
    public SpiBase(SpiProvider provider, SpiConfig config) {
        super(provider, config);
    }

    @Override
    public boolean isOpen() {
        return this.isOpen;
    }

    @Override
    public void open() {
        logger.trace("invoked 'open()'");
    }

    @Override
    public void close() {
        logger.trace("invoked 'closed()'");
        super.close();
        this.isOpen = false;
    }
}

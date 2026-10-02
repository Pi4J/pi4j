package com.pi4j.io.spi;

import com.pi4j.context.Context;
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

    /**
     * Creates a new SPI device instance bound to the given provider and configuration.
     *
     * @param context  the {@link Context} this SPI device belongs to
     * @param provider the {@link SpiProvider} that created and backs this SPI device
     * @param config   the {@link SpiConfig} describing the bus, channel, mode, and clock settings to use
     */
    protected SpiBase(Context context, SpiProvider provider, SpiConfig config) {
        super(context, provider, config);
    }

    @Override
    public void open() {
        logger.trace("invoked 'open()'");
    }
}

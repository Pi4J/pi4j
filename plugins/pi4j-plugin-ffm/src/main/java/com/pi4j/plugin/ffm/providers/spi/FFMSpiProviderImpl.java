package com.pi4j.plugin.ffm.providers.spi;

import com.pi4j.io.spi.Spi;
import com.pi4j.io.spi.SpiConfig;
import com.pi4j.io.spi.SpiProvider;
import com.pi4j.io.spi.SpiProviderBase;
import com.pi4j.plugin.ffm.common.FFMPermissionHelper;

/**
 * {@link SpiProvider} for the FFM backend that creates SPI instances communicating with the kernel
 * spidev driver ({@code /dev/spidevB.C}) via {@code SPI_IOC_MESSAGE} ioctls. The produced I/O instances
 * are {@link FFMSpi}.
 *
 * @see com.pi4j.io.spi.SpiProvider
 * @see FFMSpi
 */
public class FFMSpiProviderImpl extends SpiProviderBase implements SpiProvider {

    /**
     * Creates the provider, assigning its id and name and verifying that the current user holds the
     * permissions required to access the spidev device nodes.
     */
    public FFMSpiProviderImpl() {
        this.id = "ffm-spi";
        this.name = "FFM API Provider SPI";
        FFMPermissionHelper.checkUserPermissions(this);
    }

    @Override
    public int getPriority() {
        return 200;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Creates and registers a new {@link FFMSpi} bound to the bus and channel described by the
     * configuration.
     *
     * @param config the SPI configuration carrying the bus, channel, mode and baud rate
     * @return the newly created and registered SPI instance
     */
    @Override
    public Spi create(SpiConfig config) {
        // create new I/O instance based on I/O config
        var spi = new FFMSpi(this, config);
        this.context.register(spi);
        return spi;
    }
}

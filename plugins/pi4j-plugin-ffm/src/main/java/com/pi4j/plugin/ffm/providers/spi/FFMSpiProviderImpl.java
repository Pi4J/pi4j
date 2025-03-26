package com.pi4j.plugin.ffm.providers.spi;

import com.pi4j.io.spi.Spi;
import com.pi4j.io.spi.SpiConfig;
import com.pi4j.io.spi.SpiProvider;
import com.pi4j.io.spi.SpiProviderBase;

public class FFMSpiProviderImpl extends SpiProviderBase implements SpiProvider {

    public FFMSpiProviderImpl() {
        this.id = "FFMSpiProviderImpl";
        this.name = "FFMSpiProviderImpl";
    }

    @Override
    public int getPriority() {
        return 15000;
    }

    @Override
    public Spi create(SpiConfig config) {
        // create new I/O instance based on I/O config
        var spi = new FFMSpi(this, config);
        this.context.registry().add(spi);
        return spi;
    }
}

package com.pi4j.plugin.ffm.providers.spi;

import com.pi4j.io.spi.Spi;
import com.pi4j.io.spi.SpiConfig;
import com.pi4j.io.spi.SpiProvider;
import com.pi4j.io.spi.SpiProviderBase;

public class SpiFFMProviderImpl extends SpiProviderBase implements SpiProvider {

    public SpiFFMProviderImpl() {
        this.id = "SpiFFMProviderImpl";
        this.name = "SpiFFMProviderImpl";
    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public Spi create(SpiConfig config) {
        // create new I/O instance based on I/O config
        var spi = new SpiFFM(this, config);
        this.context.registry().add(spi);
        return spi;
    }
}

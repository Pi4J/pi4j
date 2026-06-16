package com.pi4j.plugin.ffm.providers.spi;

import com.pi4j.io.spi.Spi;
import com.pi4j.io.spi.SpiConfig;
import com.pi4j.io.spi.SpiProvider;
import com.pi4j.io.spi.SpiProviderBase;
import com.pi4j.plugin.ffm.common.FFMPermissionHelper;

public class FFMSpiProviderImpl extends SpiProviderBase implements SpiProvider {

    public FFMSpiProviderImpl() {
        this.id = "ffm-spi";
        this.name = "FFM API Provider SPI";
        FFMPermissionHelper.checkUserPermissions(this);
    }

    @Override
    public int getPriority() {
        return 200;
    }

    @Override
    public Spi create(SpiConfig config) {
        // create new I/O instance based on I/O config
        var spi = new FFMSpi(this, config);
        this.context.register(spi);
        return spi;
    }
}

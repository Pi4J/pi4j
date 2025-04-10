package com.pi4j.plugin.ffm.providers.i2c;

import com.pi4j.io.i2c.*;

public class I2CFFMProviderImpl extends I2CProviderBase implements I2CProvider {

    public I2CFFMProviderImpl() {
        this.id = "FFMI2CProviderImpl";
        this.name = "FFMI2CProviderImpl";
    }

    @Override
    public int getPriority() {
        return 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public I2C create(I2CConfig config) {
        var bus = new I2CBusFFM(config);

        I2CBase<?> i2c;
        if (bus.supportsSMBus()) {
            i2c = new I2CSMBus(this, config, bus);
        } else if (bus.supportsDirect()) {
            i2c = new I2CDirect(this, config, bus);
        } else {
            throw new IllegalArgumentException("Unsupported I2C bus type: none of SMBus or Direct write/read is supported.");
        }

        this.context.registry().add(i2c);
        return i2c;
    }
}

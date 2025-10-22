package com.pi4j.plugin.ffm.providers.i2c;

import com.pi4j.io.i2c.*;
import com.pi4j.plugin.ffm.common.PermissionHelper;
import com.pi4j.plugin.ffm.providers.i2c.impl.I2CDirect;
import com.pi4j.plugin.ffm.providers.i2c.impl.I2CFile;
import com.pi4j.plugin.ffm.providers.i2c.impl.I2CSMBus;

public class I2CFFMProviderImpl extends I2CProviderBase implements I2CProvider {

    public I2CFFMProviderImpl() {
        this.id = "ffm-i2c";
        this.name = "FFM API Provider I2C";
        PermissionHelper.checkUserPermissions(this);
    }

    @Override
    public int getPriority() {
        return 200;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public I2C create(I2CConfig config) {
        var bus = new I2CBusFFM(config);

        var impl = config.i2cImplementation();
        if (impl == null) {
            impl = I2CImplementation.SMBUS;
        }
        I2CBase<?> i2c;
        if (impl.equals(I2CImplementation.SMBUS) && bus.supportsSMBus()) {
            i2c = new I2CSMBus(this, config, bus);
        } else if (impl.equals(I2CImplementation.DIRECT) && bus.supportsDirect()) {
            i2c = new I2CDirect(this, config, bus);
        } else {
            i2c = new I2CFile(this, config, bus);
        }

        this.context.registry().add(i2c);
        return i2c;
    }
}

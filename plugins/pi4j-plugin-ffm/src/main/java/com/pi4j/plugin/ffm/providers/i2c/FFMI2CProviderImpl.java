package com.pi4j.plugin.ffm.providers.i2c;

import com.pi4j.boardinfo.util.BoardInfoHelper;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.i2c.I2CProvider;
import com.pi4j.io.i2c.I2CProviderBase;

import java.util.HashMap;
import java.util.Map;

public class FFMI2CProviderImpl extends I2CProviderBase implements I2CProvider {

    public FFMI2CProviderImpl() {
        this.id = "FFMI2CProviderImpl";
        this.name = "FFMI2CProviderImpl";
    }

    @Override
    public int getPriority() {
        return 15000;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public I2C create(I2CConfig config) {
            //PiGpioI2CBus i2CBus = this.i2CBusMap.computeIfAbsent(config.getBus(), busNr -> new PiGpioI2CBus(config));

            // create new I/O instance based on I/O config
            var i2C = new FFMI2C(new FFMI2CBus(config), this, config);
            this.context.registry().add(i2C);
            return i2C;
    }
}

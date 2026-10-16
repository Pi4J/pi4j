package com.pi4j.plugin.ffm.providers.i2c;

import com.pi4j.io.IO;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CBase;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.i2c.I2CImplementation;
import com.pi4j.plugin.ffm.providers.i2c.impl.I2CDirect;
import com.pi4j.plugin.ffm.providers.i2c.impl.I2CSMBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

/** Static factory for FFM I2C ports */
public class FFMI2C {
    private static final Logger logger = LoggerFactory.getLogger(FFMI2C.class);

    public static I2C create(Consumer<IO<?, ?>> onClose, I2CConfig config) {
        var bus = new FFMI2CBus(config);

        if (logger.isDebugEnabled()) {
            var functions = bus.getFunctionalityMap();
            logger.debug("{} - bus I2C functions support:", bus.getBusName());
            for (var entry : functions.entrySet()) {
                logger.debug("\t{} - {}", entry.getKey(), entry.getValue());
            }
        }

        var impl = config.i2cImplementation();
        if (impl == null) {
            impl = I2CImplementation.DIRECT;
            logger.debug("{} - no I2C implementation was chosen, using {} as default", bus.getBusName(), impl);
        }
        I2CBase<?> i2c;
        if (impl.equals(I2CImplementation.SMBUS) && bus.supportsSMBus()) {
            logger.debug("{} - creating SMBus adapter based on default implementation and functions", bus.getBusName());
            i2c = new I2CSMBus(onClose, config, bus);
        } else {
            logger.debug("{} - creating Direct ioctl adapter based on default implementation and functions", bus.getBusName());
            i2c = new I2CDirect(onClose, config, bus);
        }

        return i2c;
    }
}

package com.pi4j.plugin.ffm.providers.i2c;

import com.pi4j.io.i2c.*;
import com.pi4j.plugin.ffm.common.FFMPermissionHelper;
import com.pi4j.plugin.ffm.providers.i2c.impl.I2CDirect;
import com.pi4j.plugin.ffm.providers.i2c.impl.I2CFile;
import com.pi4j.plugin.ffm.providers.i2c.impl.I2CSMBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FFM backend {@link I2CProvider}. Opens an {@link FFMI2CBus} for each requested device and selects
 * the concrete I2C access implementation - {@link I2CDirect} ({@code I2C_RDWR} ioctls),
 * {@link com.pi4j.plugin.ffm.providers.i2c.impl.I2CSMBus} (SMBus ioctls) or
 * {@link com.pi4j.plugin.ffm.providers.i2c.impl.I2CFile} (plain file read/write) - based on the
 * configured preference and the capabilities the adapter actually reports.
 */
public class FFMI2CProviderImpl extends I2CProviderBase implements I2CProvider {
    private static final Logger logger = LoggerFactory.getLogger(FFMI2CProviderImpl.class);

    /**
     * Creates the provider, assigning its id and name and checking that the current user is permitted
     * to access the I2C character devices used by this backend.
     */
    public FFMI2CProviderImpl() {
        this.id = "ffm-i2c";
        this.name = "FFM API Provider I2C";
        FFMPermissionHelper.checkUserPermissions(this);
    }

    @Override
    public int getPriority() {
        return 200;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Opens the {@link FFMI2CBus} for the configured bus, then chooses the access implementation: the
     * configured {@link I2CImplementation} is honoured when the adapter actually supports it
     * (SMBus or direct), otherwise it falls back to the plain file-based implementation. The created
     * device is registered with the context.
     */
    @Override
    public I2C create(I2CConfig config) {
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
            i2c = new I2CSMBus(this, config, bus);
        } else if (impl.equals(I2CImplementation.DIRECT) && bus.supportsDirect()) {
            logger.debug("{} - creating Direct ioctl adapter based on default implementation and functions", bus.getBusName());
            i2c = new I2CDirect(this, config, bus);
        } else {
            logger.debug("{} - creating File adapter based on default implementation and functions", bus.getBusName());
            i2c = new I2CFile(this, config, bus);
        }

        this.context.register(i2c);
        return i2c;
    }
}

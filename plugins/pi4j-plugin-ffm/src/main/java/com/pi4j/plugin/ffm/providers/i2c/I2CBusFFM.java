package com.pi4j.plugin.ffm.providers.i2c;

import com.pi4j.common.CheckedFunction;
import com.pi4j.exception.InitializeException;
import com.pi4j.exception.Pi4JException;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CBusBase;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.plugin.ffm.common.file.FileDescriptorNative;
import com.pi4j.plugin.ffm.common.file.FileFlag;
import com.pi4j.plugin.ffm.common.ioctl.Command;
import com.pi4j.plugin.ffm.common.ioctl.IoctlNative;
import com.pi4j.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class I2CBusFFM extends I2CBusBase {
    private static final Logger logger = LoggerFactory.getLogger(I2CBusFFM.class);
    private final IoctlNative IOCTL = new IoctlNative();
    private final FileDescriptorNative FILE = new FileDescriptorNative();
    private static final String I2C_BUS = "/dev/i2c-";

    private final String busName;
    private final int i2cFileDescriptor;
    private final Map<I2CFunctionality, Boolean> functionalityMap = new HashMap<>();

    // selected device
    private int selectedDevice;

    public I2CBusFFM(I2CConfig config) {
        super(config);
        this.busName = I2C_BUS + bus;
        try {
            logger.debug("{} - setting up I2CBus...", busName);
            logger.debug("{} - opening device file.", busName);
            this.i2cFileDescriptor = FILE.open(busName, FileFlag.O_RDONLY);
            logger.debug("{} - loading supported functionalities.", busName);
            var i2cFunctions = IOCTL.call(i2cFileDescriptor, Command.getI2CFuncs(), 0);
            for (I2CFunctionality i2CFunctionality : I2CFunctionality.values()) {
                var supported = (i2cFunctions & i2CFunctionality.getValue()) != 0;
                functionalityMap.put(i2CFunctionality, supported);
                logger.trace("{} - functionality {}({}) is {}.", busName, i2CFunctionality.name(), StringUtil.toHexString(i2CFunctionality.getValue()), supported ? "supported" : "not supported");
            }
            if (functionalityMap.get(I2CFunctionality.I2C_FUNC_I2C)) {
                logger.debug("{} - I2CBus supports direct file mode for read/write operations.", busName);
            } else if (functionalityMap.get(I2CFunctionality.I2C_FUNC_SMBUS_BYTE_DATA) ||
                functionalityMap.get(I2CFunctionality.I2C_FUNC_SMBUS_WORD_DATA) ||
                functionalityMap.get(I2CFunctionality.I2C_FUNC_SMBUS_I2C_BLOCK)) {
                logger.debug("{} - I2CBus will be using ioctl with SMBus mode for read/write operations.", busName);
            } else {
                logger.error("{} - Cannot configure I2CBus!", busName);
                for (Map.Entry<I2CFunctionality, Boolean> functionality : functionalityMap.entrySet()) {
                    logger.error("{} - functionality {}({}) is {}.", busName, functionality.getKey().name(),
                        StringUtil.toHexString(functionality.getKey().getValue()),
                        functionality.getValue() ? "supported" : "not supported");
                }
                throw new RuntimeException(busName + " does not support any of read/write operations!");
            }
        } catch (RuntimeException e) {
            logger.error(e.getMessage());
            throw new InitializeException(e);
        }
    }

    public boolean supportsDirect() {
        return hasFunctionality(I2CFunctionality.I2C_FUNC_I2C);
    }

    public boolean supportsSMBus() {
        return hasFunctionality(I2CFunctionality.I2C_FUNC_SMBUS_QUICK) || hasFunctionality(I2CFunctionality.I2C_FUNC_SMBUS_BYTE)
            || hasFunctionality(I2CFunctionality.I2C_FUNC_SMBUS_BYTE_DATA) || hasFunctionality(I2CFunctionality.I2C_FUNC_SMBUS_WORD_DATA)
            || hasFunctionality(I2CFunctionality.I2C_FUNC_SMBUS_I2C_BLOCK);
    }

    public boolean hasFunctionality(I2CFunctionality functionality) {
        return functionalityMap.get(functionality) != null && functionalityMap.get(functionality);
    }

    public Map<I2CFunctionality, Boolean> getFunctionalityMap() {
        return functionalityMap;
    }

    /**
     * Selects the device address for communication.
     *
     * @param device device address on the bus
     */
    public void selectDevice(int device) {
        logger.debug("{} - selecting device '{}'.", busName, StringUtil.toHexString(device));
        selectAddressInternal(device);
    }

    /**
     * Selects 10BIT device address for communication.
     *
     * @param device        device address on the bus
     * @param tenBitsAddress true if the address is in 10 bits address map
     */
    private void selectDevice(int device, boolean tenBitsAddress) {
        if (tenBitsAddress && functionalityMap.get(I2CFunctionality.I2C_FUNC_10BIT_ADDR)) {
            IOCTL.callByValue(i2cFileDescriptor, Command.getI2CTenBit(), 1);
        } else {
            throw new RuntimeException("Cannot set 10bit address, because device '" + busName + "' does not support 10bit addressing extension.");
        }
        selectDevice(device);
    }

    /**
     * Internal method of selecting address.
     *
     * @param device address to be selected
     */
    private void selectAddressInternal(int device) {
        if (device == selectedDevice) {
            return;
        }
        IOCTL.callByValue(i2cFileDescriptor, Command.getI2CSlave(), device);
        this.selectedDevice = device;
    }

    public <R> R execute(I2C i2c, CheckedFunction<Integer, R> action) {
        return _execute(i2c, () -> {
            try {
                return action.apply(this.i2cFileDescriptor);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new Pi4JException("Failed to execute action for device " + i2c.device() + " on bus " + this.busName,
                    e);
            }
        });
    }

    @Override
    public <R> R execute(I2C i2c, Callable<R> action) {
        return _execute(i2c, action);
    }

    public void close() {
        try {
            FILE.close(i2cFileDescriptor);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }
}
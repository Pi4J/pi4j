package com.pi4j.plugin.ffm.providers.i2c;

import com.pi4j.common.CheckedFunction;
import com.pi4j.exception.InitializeException;
import com.pi4j.exception.Pi4JException;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CBusBase;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.plugin.ffm.common.FFMPermissionHelper;
import com.pi4j.plugin.ffm.common.file.FileDescriptorNative;
import com.pi4j.plugin.ffm.common.file.FileFlag;
import com.pi4j.plugin.ffm.common.ioctl.Command;
import com.pi4j.plugin.ffm.common.ioctl.IoctlNative;
import com.pi4j.plugin.ffm.detect.model.HWInterfaces;
import com.pi4j.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Native I2C bus for the FFM backend. Opens the {@code /dev/i2c-N} character device, queries the
 * adapter's capabilities via the {@code I2C_FUNCS} ioctl, and selects slave addresses with the
 * {@code I2C_SLAVE} (and {@code I2C_TENBIT}) ioctls. It serves as the shared, synchronized access
 * point to the underlying file descriptor for the I2C device implementations
 * ({@code I2CDirect}, {@code I2CSMBus}, {@code I2CFile}).
 */
public class FFMI2CBus extends I2CBusBase {
    private static final Logger logger = LoggerFactory.getLogger(FFMI2CBus.class);
    private final IoctlNative ioctl = new IoctlNative();
    private final FileDescriptorNative file = new FileDescriptorNative();
    private static final String I2C_BUS = "/dev/i2c-";

    private final String busName;
    private final int i2cFileDescriptor;
    private final Map<I2CFunctionality, Boolean> functionalityMap = new HashMap<>();

    // selected device
    private int selectedDevice;

    /**
     * Opens and configures the I2C bus device for the configured bus number. Verifies access
     * permissions, opens {@code /dev/i2c-N} read-write, queries the adapter functionality bitmask via
     * the {@code I2C_FUNCS} ioctl, and records which {@link I2CFunctionality} features are supported.
     * Fails if the adapter supports neither plain I2C nor any usable SMBus read/write functionality.
     *
     * @param config the {@link I2CConfig} supplying the bus number and device address
     * @throws InitializeException if the device cannot be accessed, opened, or queried, or if the
     *                             adapter supports none of the required read/write operations
     */
    public FFMI2CBus(I2CConfig config) {
        super(config);
        this.busName = I2C_BUS + bus;
        FFMPermissionHelper.checkDevicePermissions(busName, HWInterfaces.I2C, true);
        try {
            logger.debug("{} - setting up I2CBus...", busName);
            if (!canAccessDevice()) {
                var posix = Files.readAttributes(Path.of(busName), PosixFileAttributes.class);
                logger.error("Inaccessible device: '{} {} {} {}'", PosixFilePermissions.toString(posix.permissions()), posix.owner().getName(), posix.group().getName(), busName);
                logger.error("Please, read the documentation <link> to setup right permissions.");
                throw new InitializeException("Device '" + busName + "' cannot be accessed with current user.");
            }
            logger.debug("{} - opening device file.", busName);
            this.i2cFileDescriptor = file.open(busName, FileFlag.O_RDWR);
            logger.debug("{} - loading supported functionalities.", busName);
            var i2cFunctions = ioctl.call(i2cFileDescriptor, Command.getI2CFuncs(), 0);
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
                throw new Pi4JException(busName + " does not support any of read/write operations!");
            }
        } catch (Pi4JException | IOException e) {
            logger.error(e.getMessage());
            throw new InitializeException(e);
        }
    }

    /**
     * Indicates whether the adapter supports plain I2C-level transfers, i.e. the
     * {@code I2C_RDWR}-based direct access mode used by {@code I2CDirect}.
     *
     * @return {@code true} if the {@code I2C_FUNC_I2C} functionality is available
     */
    public boolean supportsDirect() {
        return hasFunctionality(I2CFunctionality.I2C_FUNC_I2C);
    }

    /**
     * Indicates whether the adapter supports any of the common SMBus transactions (quick, byte,
     * byte-data, word-data or block-data), i.e. the SMBus access mode used by {@code I2CSMBus}.
     *
     * @return {@code true} if at least one of the supported SMBus functionalities is available
     */
    public boolean supportsSMBus() {
        return hasFunctionality(I2CFunctionality.I2C_FUNC_SMBUS_QUICK) || hasFunctionality(I2CFunctionality.I2C_FUNC_SMBUS_BYTE)
            || hasFunctionality(I2CFunctionality.I2C_FUNC_SMBUS_BYTE_DATA) || hasFunctionality(I2CFunctionality.I2C_FUNC_SMBUS_WORD_DATA)
            || hasFunctionality(I2CFunctionality.I2C_FUNC_SMBUS_BLOCK_DATA);
    }

    /**
     * Tests whether a specific adapter functionality was reported as available by the
     * {@code I2C_FUNCS} ioctl during initialization.
     *
     * @param functionality the {@link I2CFunctionality} feature flag to test
     * @return {@code true} if the feature is supported by this adapter
     */
    public boolean hasFunctionality(I2CFunctionality functionality) {
        return functionalityMap.get(functionality) != null && functionalityMap.get(functionality);
    }

    /**
     * Returns the full map of adapter functionalities to their supported state, as detected during
     * initialization.
     *
     * @return a map from each {@link I2CFunctionality} to whether the adapter supports it
     */
    public Map<I2CFunctionality, Boolean> getFunctionalityMap() {
        return functionalityMap;
    }

    /**
     * Returns the device file path of this bus, e.g. {@code /dev/i2c-1}.
     *
     * @return the absolute path of the I2C bus character device
     */
    public String getBusName() {
        return busName;
    }

    /**
     * Selects the active slave device on this bus via the {@code I2C_SLAVE} ioctl. The selection is
     * cached, so re-selecting the currently active address is a no-op.
     *
     * @param device the 7-bit slave address to make active for subsequent transfers
     */
    public void selectDevice(int device) {
        logger.debug("{} - selecting device '{}'.", busName, StringUtil.toHexString(device));
        selectAddressInternal(device);
    }

    /**
     * Selects 10BIT device address for communication.
     *
     * @param device         device address on the bus
     * @param tenBitsAddress true if the address is in 10 bits address map
     */
    private void selectDevice(int device, boolean tenBitsAddress) {
        if (tenBitsAddress && functionalityMap.get(I2CFunctionality.I2C_FUNC_10BIT_ADDR)) {
            ioctl.callByValue(i2cFileDescriptor, Command.getI2CTenBit(), 1);
        } else {
            throw new Pi4JException("Cannot set 10bit address, because device '" + busName + "' does not support 10bit addressing extension.");
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
        ioctl.callByValue(i2cFileDescriptor, Command.getI2CSlave(), device);
        this.selectedDevice = device;
    }

    /**
     * Runs an action under the bus lock, passing it the open I2C device file descriptor so the action
     * can issue native transfers against it. This is the primary entry point used by the device
     * implementations to perform {@code I2C_RDWR}/SMBus ioctls while holding exclusive access to the
     * bus.
     *
     * @param i2c    the {@link I2C} device on whose behalf the action runs, used for error reporting
     * @param action a function receiving the I2C file descriptor and returning a result
     * @param <R>    the result type produced by the action
     * @return the value returned by the action
     * @throws Pi4JException if the action throws while executing
     */
    public <R> R execute(I2C i2c, CheckedFunction<Integer, R> action) {
        return _execute(i2c, () -> {
            try {
                return action.apply(this.i2cFileDescriptor);
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

    /**
     * Closes the underlying I2C bus device file descriptor.
     *
     * @throws Pi4JException if the native close call fails
     */
    public void close() {
        try {
            file.close(i2cFileDescriptor);
        } catch (Exception e) {
            throw new Pi4JException(e);
        }
    }

    private boolean canAccessDevice() {
        return file.access(busName, FileFlag.R_OK) == 0;
    }
}
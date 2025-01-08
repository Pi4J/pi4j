package com.pi4j.plugin.linuxfs.provider.i2c;

import com.pi4j.common.CheckedFunction;
import com.pi4j.exception.Pi4JException;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CBusBase;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.library.linuxfs.LinuxFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.concurrent.Callable;

/**
 * Implementation of an I2C bus using Linux file system access.
 */
public class LinuxFsI2CBus extends I2CBusBase {

    /**
     * Base path for sysfs I2C device directories.
     * <p>
     * Sysfs is used to provide information about I2C devices on the system.
     * The full path is constructed by appending the bus number, e.g., "/sys/bus/i2c/devices/i2c-1".
     */
    private static final String SYSFS_BASE_PATH = "/sys/bus/i2c/devices/i2c-";

    /**
     * Base path for devfs I2C device files.
     * <p>
     * Devfs is used to access I2C devices for read/write operations.
     * The full path is constructed by appending the bus number, e.g., "/dev/i2c-1".
     */
    private static final String DEVFS_BASE_PATH = "/dev/i2c-";

    /** Logger for the class */
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /** File handle for this I2C bus */
    protected LinuxFile file;

    /** Stores the last accessed slave address on this I2C bus */
    private int lastAddress;

    /**
     * Constructs a new {@link LinuxFsI2CBus}.
     *
     * @param config the I2C configuration
     * @throws Pi4JException if the bus cannot be initialized due to missing sysfs or devfs paths
     */
    public LinuxFsI2CBus(I2CConfig config) {
        super(config);

        validateSysFs();
        final File devFs = validateAndGetDevFs();

        try {
            String fileName = devFs.getCanonicalPath();
            this.file = new LinuxFile(fileName, "rw");
        } catch (IOException e) {
            throw new Pi4JException("Failed to initialize I2C bus " + this.bus, e);
        }
    }

    /**
     * Validates the existence and type of the sysfs directory for the I2C bus.
     *
     * @throws Pi4JException if the sysfs directory does not exist or is not a directory
     */
    private void validateSysFs() {
        final String sysfsPath = SYSFS_BASE_PATH + this.bus;
        final File sysfs = new File(sysfsPath);
        if (!sysfs.exists()) {
            throw new Pi4JException("Sysfs validation failed for I2C bus " + this.bus + ": path '" + sysfsPath + "' does not exist.");
        }
        if (!sysfs.isDirectory()) {
            throw new Pi4JException("Sysfs validation failed for I2C bus " + this.bus + ": path '" + sysfsPath + "' is not a directory.");
        }
    }

    /**
     * Validates the devfs file for the I2C bus and checks read/write access.
     *
     * @return the devfs file
     * @throws Pi4JException if the devfs file does not exist or lacks read/write permissions
     */
    private File validateAndGetDevFs() {
        final String devfsPath = DEVFS_BASE_PATH + this.bus;
        final File devfs = new File(devfsPath);
        if (!devfs.exists()) {
            throw new Pi4JException("Devfs validation failed for I2C bus " + this.bus + ": path '" + devfsPath + "' does not exist.");
        }
        if (!devfs.canRead()) {
            throw new Pi4JException("Devfs validation failed for I2C bus " + this.bus + ": path '" + devfsPath + "' is not readable.");
        }
        if (!devfs.canWrite()) {
            throw new Pi4JException("Devfs validation failed for I2C bus " + this.bus + ": path '" + devfsPath + "' is not writable.");
        }
        return devfs;
    }

    /**
     * Executes a callable action with the specified I2C device.
     *
     * @param i2c the I2C device
     * @param action the action to perform
     * @param <R> the result type of the action
     * @return the result of the action
     * @throws Pi4JException if the action fails
     */
    @Override
    public <R> R execute(I2C i2c, Callable<R> action) {
        return _execute(i2c, () -> {
            try {
                selectBusSlave(i2c);
                return action.call();
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new Pi4JException("Failed to execute action for device " + i2c.device() + " on bus " + this.bus,
                    e);
            }
        });
    }

    /**
     * Executes an action that interacts with the underlying file handle.
     *
     * @param i2c the I2C device
     * @param action the action to perform
     * @param <R> the result type of the action
     * @return the result of the action
     * @throws Pi4JException if the action fails
     */
    public <R> R execute(final I2C i2c, final CheckedFunction<LinuxFile, R> action) {
        return _execute(i2c, () -> {
            try {
                selectBusSlave(i2c);
                return action.apply(this.file);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new Pi4JException("Failed to execute action for device " + i2c.device() + " on bus " + this.bus,
                    e);
            }
        });
    }

    /**
     * Executes an ioctl command on the I2C device.
     *
     * @param i2c the I2C device
     * @param command the ioctl command
     * @param data the data buffer for the command
     * @param offsets the offsets buffer
     * @throws Pi4JException if the ioctl command fails
     */
    public void executeIOCTL(final I2C i2c, long command, ByteBuffer data, IntBuffer offsets) {
        _execute(i2c, () -> {
            try {
                selectBusSlave(i2c);
                this.file.ioctl(command, data, offsets);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new Pi4JException("Failed to execute ioctl for device " + i2c.device() + " on bus " + this.bus, e);
            }
            return null;
        });
    }

    /**
     * Selects the slave device on the I2C bus, if not already selected.
     *
     * @param i2c the I2C device to select
     * @throws IOException if selecting the device fails
     */
    protected void selectBusSlave(I2C i2c) throws IOException {
        if (this.lastAddress == i2c.device())
            return;

        this.lastAddress = i2c.device();
        this.file.ioctl(I2CConstants.I2C_SLAVE, i2c.device() & 0xFF);
    }

    /**
     * Closes the file handle for the I2C bus.
     */
    public void close() {
        if (this.file != null) {
            try {
                this.file.close();
            } catch (IOException e) {
                logger.error("Failed to close file {} for {}-{}", this.file, getClass().getSimpleName(), this.bus, e);
            }
        }
    }
}

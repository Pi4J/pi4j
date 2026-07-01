package com.pi4j.io.i2c;

import com.pi4j.context.Context;
import com.pi4j.io.IO;
import com.pi4j.io.IODataReader;
import com.pi4j.io.IODataWriter;
import com.pi4j.io.SerialCircuitIO;

import java.util.Arrays;
import java.util.concurrent.Callable;

/**
 * Represents a single I2C device on an I2C bus and is the primary handle through which application code reads and
 * writes data, both as raw byte streams and via device registers. Instances are created by an {@link I2CProvider}
 * from an {@link I2CConfig}; register-level access is obtained through {@link #getRegister(int)} and atomic
 * bus operations through {@link #execute(Callable)}.
 */
public interface I2C
    extends IO<I2C, I2CConfig, I2CProvider>, IODataWriter, IODataReader, I2CRegisterDataReaderWriter, SerialCircuitIO, AutoCloseable {

    // Override to remove checked exception declaration
    @Override
    void close();

    /**
     * Creates a new configuration builder for an I2C device.
     *
     * @param context the Pi4J runtime context (unused by the current implementation)
     * @return a new {@link I2CConfigBuilder} instance
     */
    static I2CConfigBuilder newConfigBuilder(Context context) {
        return I2CConfigBuilder.newInstance();
    }

    /**
     * Returns the device (slave) address this instance communicates with.
     *
     * @return the I2C device address taken from this instance's configuration
     */
    default int device() {
        return config().device();
    }

    /**
     * Returns the bus number this device is attached to.
     *
     * @return the I2C bus number taken from this instance's configuration
     */
    default int bus() {
        return config().bus();
    }

    /**
     * Indicates whether this device is currently open for communication.
     *
     * @return {@code true} while the device is open, {@code false} once it has been closed
     */
    boolean isOpen();

    /**
     * Returns the bus number this device is attached to.
     *
     * @return the I2C bus number taken from this instance's configuration
     */
    default int getBus() {
        return bus();
    }

    /**
     * Returns the device (slave) address this instance communicates with.
     *
     * @return the I2C device address taken from this instance's configuration
     */
    default int getDevice() {
        return device();
    }

    /**
     * Method to write the writeBuffer, and then a read into the readBuffer
     * in a single atomic operation.
     *
     * @param writeBuffer    the buffer to write respecting the given length and offset
     * @param writeOffset    the offset of the array to write
     * @param writeSize      the number of bytes to write
     * @param readDelayNanos delay after writing before reading; currently ignored for i2c.
     * @param readBuffer     the buffer into which to read the bytes
     * @param readOffset     the offset in the read buffer at which to insert the read bytes
     * @param readSize       the number of bytes to read
     */
    default void writeThenRead(byte[] writeBuffer, int writeOffset, int writeSize, int readDelayNanos, byte[] readBuffer, int readOffset, int readSize) {
        // Ideally, new implementations support this call directly.
        // however, we can emulate it using the multi-byte register call
        if (writeSize != writeBuffer.length) {
            writeBuffer = Arrays.copyOfRange(writeBuffer, writeOffset, writeOffset + writeSize);
        }
        readRegister(writeBuffer, readBuffer, readOffset, readSize);
    }

    // --------------------
    // Disambiguation
    // ---------------------

    @Override
    default int read(byte[] data) {
        return SerialCircuitIO.super.read(data);
    }

    @Override
    default int read(byte[] data, int offset, int length) {
        return SerialCircuitIO.super.read(data, offset, length);
    }

    @Override
    default int write(byte... data) {
        return SerialCircuitIO.super.write(data);
    }

    @Override
    default int write(byte[] data, int offset, int length) {
        return SerialCircuitIO.super.write(data, offset, length);
    }

    /**
     * Returns a handle for reading from and writing to a specific register of this I2C device.
     *
     * @param address the device register address
     * @return an {@link I2CRegister} bound to the given register address
     */
    I2CRegister getRegister(int address);

    /**
     * Returns a handle for reading from and writing to a specific register of this I2C device.
     *
     * @param address the device register address
     * @return an {@link I2CRegister} bound to the given register address
     */
    default I2CRegister register(int address) {
        return getRegister(address);
    }

    /**
     * Executes the given action while holding an exclusive lock on this device's I2C bus, allowing several
     * reads and/or writes to be performed as one uninterrupted unit relative to other devices on the bus.
     *
     * @param action the work to perform while the bus is locked
     * @param <T>    the result type produced by the action
     * @return the value returned by the action
     */
    <T> T execute(Callable<T> action);
}

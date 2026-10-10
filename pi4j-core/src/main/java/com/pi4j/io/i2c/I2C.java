package com.pi4j.io.i2c;

import com.pi4j.context.Context;
import com.pi4j.io.IO;
import com.pi4j.io.IODataReader;
import com.pi4j.io.IODataWriter;
import com.pi4j.io.SerialCircuitIO;

import java.util.concurrent.Callable;

/**
 * Represents a single I2C device on an I2C bus and is the primary handle through which application code reads and
 * writes data, both as raw byte streams and via device registers. Register-level access is obtained through {@link #getRegister(int)} and atomic
 * bus operations through {@link #execute(Callable)}.
 */
public interface I2C
    extends IO<I2C, I2CConfig>, IODataWriter, IODataReader, I2CRegisterDataReaderWriter, SerialCircuitIO, AutoCloseable {

    // Override to remove checked exception declaration
    @Override
    void close();

    /**
     * Creates a new configuration builder for an I2C device.
     *
     * @param context the Pi4J runtime context (unused by the current implementation)
     * @return a new {@link I2CConfigBuilder} instance
     * @deprecated user newConfigBuilder() instead.
     */
    @Deprecated(since="5.0")
    static I2CConfigBuilder newConfigBuilder(Context context) {
        return newConfigBuilder();
    }

    /**
     * Creates a new configuration builder for an I2C device.
     *
     * @return a new {@link I2CConfigBuilder} instance
     */
    static I2CConfigBuilder newConfigBuilder() {
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


    @Override
    default int read() {
        byte[] ioBuffer = new byte[1];
        read(ioBuffer, 0, 1);
        return ioBuffer[0] & 255;
    }

    @Override
    default int write(byte b) {
        write(new byte[] {b}, 0, 1);
        return 1;
    }

    @Override
    default int readRegister(int i) {
        byte[] ioBuffer = new byte[] {(byte) i};
        writeThenRead(ioBuffer, 0, 1, 0, ioBuffer, 0, 1);
        return ioBuffer[0] & 255;
    }

    @Override
    default int readRegister(byte[] register, byte[] data, int offset, int length) {
        writeThenRead(register, 0, register.length, 0, data, offset, length);
        return length;
    }

    @Override
    default int readRegister(int i, byte[] bytes, int offset, int count) {
        byte[] ioBuffer = new byte[] {(byte) i};
        writeThenRead(ioBuffer, 0, 1, 0, bytes, offset, count);
        return count;
    }

    @Override
    default int writeRegister(int i, byte b) {
        write(new byte[] {(byte) i, b}, 0, 2);
        return 1;
    }

    @Override
    default int writeRegister(int i, byte[] bytes, int offset, int length) {
        byte[] combined = new byte[length + 1];
        combined[0] = (byte) i;
        System.arraycopy(bytes, offset, combined, 1, length);
        write(combined, 0, combined.length);
        return length;
    }

    @Override
    default int writeRegister(byte[] register, byte[] data, int offset, int length) {
        byte[] combined = new byte[ register.length + length];
        System.arraycopy(register, 0, combined, 0, register.length);
        System.arraycopy(data, offset, combined, register.length, length);
        write(combined, 0, combined.length);
        return length;
    }
}

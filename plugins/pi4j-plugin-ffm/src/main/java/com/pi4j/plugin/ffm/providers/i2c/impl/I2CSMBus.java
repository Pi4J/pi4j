package com.pi4j.plugin.ffm.providers.i2c.impl;

import com.pi4j.context.Context;
import com.pi4j.exception.InitializeException;
import com.pi4j.exception.Pi4JException;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CBase;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.i2c.I2CProvider;
import com.pi4j.plugin.ffm.common.i2c.SMBusNative;
import com.pi4j.plugin.ffm.providers.i2c.FFMI2CBus;
import com.pi4j.plugin.ffm.providers.i2c.I2CFunctionality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Objects;

/**
 * {@link I2C} implementation that communicates with an I2C device using the Linux SMBus protocol,
 * issued through the {@code I2C_SMBUS} ioctl wrapped by {@link SMBusNative}.
 * <p>
 * The concrete SMBus transaction (byte, word or block read/write) is chosen at runtime from the
 * adapter's advertised {@link I2CFunctionality} capabilities. Operations that the SMBus protocol cannot
 * express - notably register-less array transfers and multi-byte register addressing - throw
 * {@link UnsupportedOperationException}; use {@link I2CFile} for those cases.
 *
 * @see com.pi4j.io.i2c.I2C
 * @see SMBusNative
 * @see FFMI2CBus
 */
public class I2CSMBus extends I2CBase<FFMI2CBus> {
    private static final Logger logger = LoggerFactory.getLogger(I2CSMBus.class);
    private final SMBusNative SMBUS = new SMBusNative();

    /**
     * Creates an SMBus-based I2C device bound to the given bus.
     *
     * @param provider the {@link I2CProvider} that created this instance
     * @param config   the I2C configuration carrying the bus number and target slave device address
     * @param i2CBus   the shared {@link FFMI2CBus} wrapping the open {@code /dev/i2c-N} file descriptor
     */
    public I2CSMBus(I2CProvider provider, I2CConfig config, FFMI2CBus i2CBus) {
        super(provider, config, i2CBus);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Selects the configured slave address on the bus (issuing the {@code I2C_SLAVE} ioctl) before
     * delegating to the superclass initialization.
     */
    @Override
    public I2C initialize(Context context) throws InitializeException {
        i2CBus.selectDevice(config.device());
        logger.debug("{} - selected device '{}'", i2CBus.getBusName(), Integer.toHexString(config.device()));
        return super.initialize(context);
    }

    /**
     * Writes the given bytes to the specified register of the selected device, choosing the SMBus
     * write transaction (byte, word or block) according to the adapter's advertised functionality.
     *
     * @param register the device register address (used as the SMBus command byte)
     * @param data     the payload bytes to write
     * @return the native return code of the SMBus write call
     * @throws Pi4JException if the adapter advertises none of the supported SMBus write modes
     */
    private int writeInternal(int register, byte[] data) {
        return i2CBus.execute(this, (i2cFileDescriptor) -> {
            logger.trace("{} - writing into register '{}' data '{}'", i2CBus.getBusName(), Integer.toHexString(register), Arrays.toString(data));
            if (i2CBus.hasFunctionality(I2CFunctionality.I2C_FUNC_SMBUS_QUICK) && data.length == 1) {
                return SMBUS.writeByteData(i2cFileDescriptor, (byte) register, data[0]);
            } else if (i2CBus.hasFunctionality(I2CFunctionality.I2C_FUNC_SMBUS_WRITE_BLOCK_DATA)) {
                return SMBUS.writeBlockData(i2cFileDescriptor, (byte) register, data);
            } else if (i2CBus.hasFunctionality(I2CFunctionality.I2C_FUNC_SMBUS_WRITE_WORD_DATA)) {
                return SMBUS.writeWordData(i2cFileDescriptor, (byte) register, data[0]);
            } else {
                throw new Pi4JException("No support any of I2C device write mode.");
            }
        });
    }

    /**
     * Reads bytes from the specified register of the selected device, choosing the SMBus read
     * transaction (byte, word or block) according to the adapter's advertised functionality.
     *
     * @param register the device register address (used as the SMBus command byte)
     * @param size     the number of bytes requested; a value of 1 prefers the single-byte read
     * @return the bytes read from the register
     * @throws Pi4JException if the adapter advertises none of the supported SMBus read modes
     */
    private byte[] readInternal(int register, int size) {
        return i2CBus.execute(this, (i2cFileDescriptor) -> {
            logger.trace("{} - reading from register '{}' data size '{}'", i2CBus.getBusName(), Integer.toHexString(register), size);
            if (i2CBus.hasFunctionality(I2CFunctionality.I2C_FUNC_SMBUS_QUICK) && size == 1) {
                return new byte[]{SMBUS.readByteData(i2cFileDescriptor, (byte) register)};
            } else if (i2CBus.hasFunctionality(I2CFunctionality.I2C_FUNC_SMBUS_READ_BLOCK_DATA)) {
                return SMBUS.readBlockData(i2cFileDescriptor, (byte) register, new byte[size]);
            } else if (i2CBus.hasFunctionality(I2CFunctionality.I2C_FUNC_SMBUS_READ_WORD_DATA)) {
                return new byte[]{(byte) SMBUS.readWordData(i2cFileDescriptor, (byte) register)};
            } else {
                throw new Pi4JException("No support any of I2C device read mode.");
            }
        });
    }

    @Override
    public byte readByte() {
        return i2CBus.execute(this, SMBUS::readByte).byteValue();
    }

    @Override
    public int read() {
        // this is needed, because we are receiving raw bytes, which we have to convert to proper int
        return i2CBus.execute(this, SMBUS::readByte);
    }

    @Override
    public int read(byte[] data, int offset, int length) {
        throw new UnsupportedOperationException("SMBus protocol does not support reading to data arrays without register. " +
            "Please, use I2CDirect or I2CFile provider instead.");
    }

    @Override
    public int write(byte b) {
        return i2CBus.execute(this, (i2cFileDescriptor) -> SMBUS.writeByte(i2cFileDescriptor, b));
    }

    @Override
    public int write(byte[] data, int offset, int length) {
        throw new UnsupportedOperationException("SMBus protocol does not support writing data arrays without register. " +
            "Please, use I2CDirect or I2CFile provider instead.");
    }

    @Override
    public int readRegister(int register) {
        // this is needed, because we are receiving raw bytes, which we have to convert to proper int
        return Byte.toUnsignedInt(readInternal(register, 1)[0]);
    }

    @Override
    public int readRegister(byte[] register, byte[] data, int offset, int length) {
        throw new UnsupportedOperationException("SMBus protocol does not support reading multiregister devices. " +
            "Please, use I2CDirect or I2CFile provider instead.");
    }

    @Override
    public int readRegister(int register, byte[] buffer, int offset, int length) {
        Objects.checkFromIndexSize(offset, length, buffer.length);
        var result = readInternal(register, buffer.length);
        System.arraycopy(result, 0, buffer, offset, length);
        return result.length;
    }

    @Override
    public int writeRegister(int register, byte b) {
        return writeInternal(register, new byte[]{b});
    }

    @Override
    public int writeRegister(int register, byte[] data, int offset, int length) {
        Objects.checkFromIndexSize(offset, length, data.length);
        var writeData = Arrays.copyOfRange(data, offset, offset + length);
        return writeInternal(register, writeData);
    }

    @Override
    public int writeRegister(byte[] register, byte[] data, int offset, int length) {
        Objects.checkFromIndexSize(offset, length, data.length);
        // Prepare the data for multibyte register
        // Sequence:
        //      - set address to first byte, all other bytes send with data.
        //      - read the byte without address (internal chip cursor will point to the correct address)
        var byteRegister = register[0];
        var writeData = new byte[data.length + register.length - 1];
        System.arraycopy(Arrays.copyOfRange(register, 1, register.length), 0, writeData, 0, register.length - 1);
        System.arraycopy(data, 0, writeData, register.length - 1, data.length);
        return writeRegister(byteRegister, writeData);
    }

    @Override
    public void close() {
        super.close();
        i2CBus.close();
    }
}

package com.pi4j.plugin.ffm.providers.i2c.impl;

import com.pi4j.context.Context;
import com.pi4j.exception.InitializeException;
import com.pi4j.exception.Pi4JException;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CBase;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.i2c.I2CProvider;
import com.pi4j.plugin.ffm.common.i2c.SMBusNative;
import com.pi4j.plugin.ffm.providers.i2c.I2CBusFFM;
import com.pi4j.plugin.ffm.providers.i2c.I2CFunctionality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Objects;

public class I2CSMBus extends I2CBase<I2CBusFFM> {
    private static final Logger logger = LoggerFactory.getLogger(I2CSMBus.class);
    private final SMBusNative SMBUS = new SMBusNative();

    /**
     * <p>Constructor for I2CBase.</p>
     *
     * @param provider a {@link I2CProvider} object.
     * @param config   a {@link I2CConfig} object.
     * @param i2CBus   a {@link I2CBusFFM} object.
     */
    public I2CSMBus(I2CProvider provider, I2CConfig config, I2CBusFFM i2CBus) {
        super(provider, config, i2CBus);
    }

    @Override
    public I2C initialize(Context context) throws InitializeException {
        i2CBus.selectDevice(config.device());
        logger.debug("{} - selected device '{}'", i2CBus.getBusName(), Integer.toHexString(config.device()));
        return super.initialize(context);
    }

    /**
     * Internal method.
     * Writes the data array into the register address of device selected previously.
     *
     * @param data data array to be written
     */
    private int writeInternal(int register, byte[] data) {
        return i2CBus.execute(this, (i2cFileDescriptor) -> {
            logger.trace("{} - writing into register '{}' data '{}'", i2CBus.getBusName(), Integer.toHexString(register), Arrays.toString(data));
            if (i2CBus.hasFunctionality(I2CFunctionality.I2C_FUNC_SMBUS_QUICK) && data.length == 1) {
                logger.trace("{} - writing SMBUS.writeByte", i2CBus.getBusName());
                return SMBUS.writeByte(i2cFileDescriptor, data[0]);
            } else if (i2CBus.hasFunctionality(I2CFunctionality.I2C_FUNC_SMBUS_WRITE_BYTE_DATA) && data.length == 1) {
                logger.trace("{} - writing SMBUS.writeByteData", i2CBus.getBusName());
                return SMBUS.writeByteData(i2cFileDescriptor, (byte) register, data[0]);
            } else if (i2CBus.hasFunctionality(I2CFunctionality.I2C_FUNC_SMBUS_WRITE_WORD_DATA) && data.length == 2) {
                logger.trace("{} - writing SMBUS.writeWordData '{}'", i2CBus.getBusName(), fromTwoByteArray(data));
                return SMBUS.writeWordData(i2cFileDescriptor, (byte) register, fromTwoByteArray(data));
            } else if (i2CBus.hasFunctionality(I2CFunctionality.I2C_FUNC_SMBUS_WRITE_BLOCK_DATA)) {
                logger.trace("{} - writing SMBUS.writeBlockData", i2CBus.getBusName());
                return SMBUS.writeBlockData(i2cFileDescriptor, (byte) register, data);
            }
            else {
                throw new Pi4JException("No support any of I2C device write mode.");
            }
        });
    }

    private static int fromTwoByteArray(byte[] bytes) {
        return ((bytes[0] & 0xFF) << 8 ) |
            ((bytes[1] & 0xFF));
    }

    private static byte[] toTwoByteArray(int value) {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) (value & 0xFF);
        bytes[1] = (byte) ((value >> 8) & 0xFF);
        return bytes;
    }

    /**
     * Internal method.
     * Reads the data byte from the register address of device selected previously.
     *
     * @param register register address of selected device
     * @return data byte read from register
     */
    private byte[] readInternal(int register, int size) {
        return i2CBus.execute(this, (i2cFileDescriptor) -> {
            logger.trace("{} - reading from register '{}' data size '{}'", i2CBus.getBusName(), Integer.toHexString(register), size);
            if (i2CBus.hasFunctionality(I2CFunctionality.I2C_FUNC_SMBUS_QUICK) && size == 1) {
                logger.trace("{} - reading SMBUS.readByte", i2CBus.getBusName());
                return new byte[] {SMBUS.readByte(i2cFileDescriptor)};
            } else if (i2CBus.hasFunctionality(I2CFunctionality.I2C_FUNC_SMBUS_WRITE_BYTE_DATA) && size == 1) {
                logger.trace("{} - reading SMBUS.readByteData", i2CBus.getBusName());
                return new byte[] {SMBUS.readByteData(i2cFileDescriptor, (byte) register)};
            } else if (i2CBus.hasFunctionality(I2CFunctionality.I2C_FUNC_SMBUS_READ_WORD_DATA) && size == 2) {
                logger.trace("{} - reading SMBUS.readWordData", i2CBus.getBusName());
                var word = SMBUS.readWordData(i2cFileDescriptor, (byte) register);
                return toTwoByteArray(word);
            } else if (i2CBus.hasFunctionality(I2CFunctionality.I2C_FUNC_SMBUS_READ_BLOCK_DATA)) {
                logger.trace("{} - reading SMBUS.readBlockData", i2CBus.getBusName());
                return SMBUS.readBlockData(i2cFileDescriptor, (byte) register, new byte[size]);
            } else {
                throw new Pi4JException("No support any of I2C device read mode.");
            }
        });
    }

    @Override
    public byte readByte() {
        return i2CBus.execute(this, SMBUS::readByte);
    }

    @Override
    public int read() {
        // this is needed, because we are receiving raw bytes, which we have to convert to proper int
        return Byte.toUnsignedInt(i2CBus.execute(this, SMBUS::readByte));
    }

    @Override
    public int read(byte[] data, int offset, int length) {
        throw new UnsupportedOperationException("SMBus protocol does not support reading to data arrays without register. " +
            "Please, use I2CDirect or I2CFile provider instead.");
    }

    @Override
    public int write(byte b) {
        i2CBus.execute(this, (i2cFileDescriptor) -> SMBUS.writeByte(i2cFileDescriptor, b));
        return 1;
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
        writeInternal(register, new byte[]{b});
        return 1;
    }

    @Override
    public int writeRegister(int register, byte[] data, int offset, int length) {
        Objects.checkFromIndexSize(offset, length, data.length);
        var writeData = Arrays.copyOfRange(data, offset, offset + length);
        writeInternal(register, writeData);
        return length;
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
        writeRegister(byteRegister, writeData);
        return length;
    }

    @Override
    public void close() {
        super.close();
        i2CBus.close();
    }
}

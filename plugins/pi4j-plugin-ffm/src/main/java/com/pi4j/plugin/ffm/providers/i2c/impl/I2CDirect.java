package com.pi4j.plugin.ffm.providers.i2c.impl;

import com.pi4j.context.Context;
import com.pi4j.exception.InitializeException;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CBase;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.i2c.I2CProvider;
import com.pi4j.plugin.ffm.common.i2c.I2cConstants;
import com.pi4j.plugin.ffm.common.i2c.rdwr.I2CMessage;
import com.pi4j.plugin.ffm.common.i2c.rdwr.RDWRData;
import com.pi4j.plugin.ffm.common.ioctl.IoctlNative;
import com.pi4j.plugin.ffm.providers.i2c.FFMI2CBus;

public class I2CDirect extends I2CBase<FFMI2CBus> {
    private final IoctlNative ioctl = new IoctlNative();

    public I2CDirect(I2CProvider provider, I2CConfig config, FFMI2CBus i2CBus) {
        super(provider, config, i2CBus);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public I2C initialize(Context context) throws InitializeException {
        return super.initialize(context);
    }

    /**
     * Read data from the I/O device into the provided byte array at the given offset and up to the specified data length (number of bytes).
     *
     * @param buffer the buffer to read the data
     * @param offset position in the buffer for the read data
     * @param length max data length to be added to the buffer
     * @return the same buffer instance that was passed in, with read data written to it starting at the specified offset
     */
    private byte[] internalRead(byte[] buffer, int offset, int length) {
        if (offset < 0) {
            throw new IllegalArgumentException("Offset cannot be negative");
        } else if (offset == 0 && length == buffer.length) {
            // Direct read into buffer without intermediate allocation
            var messages = new I2CMessage[]{
                new I2CMessage(config.device(), I2cConstants.I2C_M_RD.getValue(), length, buffer),
            };
            var packets = new RDWRData(messages, 1);
            return i2CBus.execute(this, i2cFileDescriptor -> {
                ioctl.call(i2cFileDescriptor, I2cConstants.I2C_RDWR.getValue(), packets);
                return buffer;
            });
        } else {
            // Read into an intermediate buffer, then copy to the target offset
            var readBuffer = new byte[length];
            var messages = new I2CMessage[]{
                new I2CMessage(config.device(), I2cConstants.I2C_M_RD.getValue(), length, readBuffer),
            };
            var packets = new RDWRData(messages, 1);
            return i2CBus.execute(this, i2cFileDescriptor -> {
                var result = ioctl.call(i2cFileDescriptor, I2cConstants.I2C_RDWR.getValue(), packets);
                var resultBuffer = result.msgs()[0].buf();
                System.arraycopy(resultBuffer, 0, buffer, offset, length);
                return buffer;
            });
        }
    }

    /**
     * Read data from the I/O device register into the provided byte array at the given offset and up to the specified data length (number of bytes).
     *
     * @param register the register address to read from
     * @param buffer   the buffer to read the data
     * @param offset   position in the buffer for the read data
     * @param length   max data length to be added to the buffer
     * @return the same buffer instance that was passed in, with read data written to it starting at the specified offset
     */
    private byte[] internalRead(byte[] register, byte[] buffer, int offset, int length) {
        if (offset < 0) {
            throw new IllegalArgumentException("Offset cannot be negative");
        } else if (offset == 0 && length == buffer.length) {
            // Direct read into buffer without intermediate allocation
            var messages = new I2CMessage[]{
                new I2CMessage(config.device(), 0, register.length, register),
                new I2CMessage(config.device(), I2cConstants.I2C_M_RD.getValue(), length, buffer),
            };
            var packets = new RDWRData(messages, 2);
            return i2CBus.execute(this, i2cFileDescriptor -> {
                ioctl.call(i2cFileDescriptor, I2cConstants.I2C_RDWR.getValue(), packets);
                return buffer;
            });
        } else {
            // Read into an intermediate buffer, then copy to the target offset
            var readBuffer = new byte[length];
            var messages = new I2CMessage[]{
                new I2CMessage(config.device(), 0, register.length, register),
                new I2CMessage(config.device(), I2cConstants.I2C_M_RD.getValue(), length, readBuffer),
            };
            var packets = new RDWRData(messages, 2);
            return i2CBus.execute(this, i2cFileDescriptor -> {
                var result = ioctl.call(i2cFileDescriptor, I2cConstants.I2C_RDWR.getValue(), packets);
                var resultBuffer = result.msgs()[1].buf();
                System.arraycopy(resultBuffer, 0, buffer, offset, length);
                return buffer;
            });
        }
    }

    /**
     * Write data to the I/O device register.
     *
     * @param register the register address to write to
     * @param data     the data to write
     * @return the number of data bytes successfully written (excluding register bytes)
     */
    private int internalWrite(byte[] register, byte[] data) {
        return internalWrite(register, data, 0, data.length);
    }

    /**
     * Write data to the I/O device register with a specified offset and length from the data array.
     *
     * @param register the register address to write to
     * @param data     the data array to write from
     * @param offset   starting position in the data array
     * @param length   number of bytes to write from the data array
     * @return the number of data bytes successfully written (excluding register bytes)
     */
    private int internalWrite(byte[] register, byte[] data, int offset, int length) {
        var buffer = new byte[register.length + length];
        System.arraycopy(register, 0, buffer, 0, register.length);
        System.arraycopy(data, offset, buffer, register.length, length);
        return internalWrite(buffer) - register.length;
    }

    /**
     * Write data to the I/O device.
     *
     * @param data the data to write
     * @return the number of bytes successfully written
     */
    private int internalWrite(byte[] data) {
        return internalWrite(data, 0, data.length);
    }

    /**
     * Write an array of byte values with given offset (starting position) and length in the provided data array.
     *
     * @param data   the data array to write from
     * @param offset starting position in the data array
     * @param length number of bytes to write from the data array
     * @return the number of bytes successfully written
     */
    private int internalWrite(byte[] data, int offset, int length) {
        var writeBuffer = new byte[length];
        System.arraycopy(data, offset, writeBuffer, 0, length);
        var messages = new I2CMessage[]{
            new I2CMessage(config.device(), 0, length, writeBuffer)
        };
        var packets = new RDWRData(messages, 1);
        return i2CBus.execute(this, i2cFileDescriptor -> {
            var result = ioctl.call(i2cFileDescriptor, I2cConstants.I2C_RDWR.getValue(), packets);
            return result.msgs()[0].len();
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        super.close();
        i2CBus.close();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read() {
        return internalRead(new byte[1], 0, 1)[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read(byte[] buffer, int offset, int length) {
        internalRead(buffer, offset, length);
        return length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int write(byte b) {
        return internalWrite(new byte[]{b});
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int write(byte[] data, int offset, int length) {
        return internalWrite(data, offset, length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int readRegister(int register) {
        return Byte.toUnsignedInt(internalRead(new byte[]{(byte) register}, new byte[0], 0, 1)[0]);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int readRegister(byte[] register, byte[] buffer, int offset, int length) {
        internalRead(register, buffer, offset, length);
        return length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int readRegister(int register, byte[] buffer, int offset, int length) {
        internalRead(new byte[]{(byte) register}, buffer, offset, length);
        return length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int writeRegister(int register, byte b) {
        return internalWrite(new byte[]{(byte) register}, new byte[]{b});
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int writeRegister(int register, byte[] data, int offset, int length) {
        return internalWrite(new byte[]{(byte) register}, data, offset, length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int writeRegister(byte[] register, byte[] data, int offset, int length) {
        return internalWrite(register, data, offset, length);
    }
}

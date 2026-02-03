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

import java.nio.ByteBuffer;

public class I2CDirect extends I2CBase<FFMI2CBus> {
    private final IoctlNative ioctl;

    public I2CDirect(I2CProvider provider, I2CConfig config, FFMI2CBus i2CBus) {
        super(provider, config, i2CBus);
        ioctl = new IoctlNative();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public I2C initialize(Context context) throws InitializeException {
        return super.initialize(context);
    }

    private byte[] internalRead(byte[] buffer) {
        return internalRead(0, buffer.length, buffer);
    }

    private byte[] internalRead(int offset, int length, byte[] buffer) {
        var messages = new I2CMessage[]{
            new I2CMessage(config.device(), I2cConstants.I2C_M_RD.getValue(), length, buffer),
        };
        var packets = new RDWRData(messages, 1);
        return i2CBus.execute(this, i2cFileDescriptor -> {
            var result = ioctl.call(i2cFileDescriptor, I2cConstants.I2C_RDWR.getValue(), packets);
            return result.msgs()[0].buf();
        });
    }

    private byte[] internalRead(byte[] register, int offset, int length, byte[] buffer) {
        var messages = new I2CMessage[]{
            new I2CMessage(config.device(), 0, register.length, register),
            new I2CMessage(config.device(), I2cConstants.I2C_M_RD.getValue(), length, buffer),
        };
        var packets = new RDWRData(messages, 2);
        return i2CBus.execute(this, i2cFileDescriptor -> {
            var result = ioctl.call(i2cFileDescriptor, I2cConstants.I2C_RDWR.getValue(), packets);
            return result.msgs()[1].buf();
        });
    }

    private int internalWrite(byte[] register, byte[] data) {
        return internalWrite(register, 0, data.length, data);
    }

    private int internalWrite(byte[] register, int offset, int length, byte[] data) {
        var buffer = new byte[register.length + data.length];
        System.arraycopy(register, 0, buffer, 0, register.length);
        System.arraycopy(data, 0, buffer, offset, length);
        return internalWrite(buffer) - register.length;
    }

    private int internalWrite(byte[] data) {
        return internalWrite(0, data.length, data);
    }

    private int internalWrite(int offset, int length, byte[] data) {
        var messages = new I2CMessage[]{
            new I2CMessage(config.device(), 0, length, data)
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
        return internalRead(new byte[1])[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read(byte[] buffer, int offset, int length) {
        var read = internalRead(offset, length, buffer);
        ByteBuffer.wrap(buffer).put(read);
        return read.length;
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
        return internalWrite(offset, length, data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int readRegister(int register) {
        return Byte.toUnsignedInt(internalRead(new byte[]{(byte) register}, 0, 1, new byte[0])[0]);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int readRegister(byte[] register, byte[] buffer, int offset, int length) {
        var read = internalRead(register, offset, length, buffer);
        ByteBuffer.wrap(buffer).put(read);
        return read.length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int readRegister(int register, byte[] buffer, int offset, int length) {
        var read = internalRead(new byte[]{(byte) register}, offset, length, buffer);
        ByteBuffer.wrap(buffer).put(read);
        return read.length;
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
        return internalWrite(new byte[]{(byte) register}, offset, length, data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int writeRegister(byte[] register, byte[] data, int offset, int length) {
        return internalWrite(register, offset, length, data);
    }
}

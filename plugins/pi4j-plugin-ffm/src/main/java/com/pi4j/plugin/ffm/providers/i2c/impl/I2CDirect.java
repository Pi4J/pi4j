package com.pi4j.plugin.ffm.providers.i2c.impl;

import com.pi4j.context.Context;
import com.pi4j.exception.InitializeException;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CBase;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.i2c.I2CProvider;
import com.pi4j.plugin.ffm.common.i2c.rdwr.I2CMessage;
import com.pi4j.plugin.ffm.common.i2c.I2cConstants;
import com.pi4j.plugin.ffm.common.i2c.rdwr.RDWRData;
import com.pi4j.plugin.ffm.common.ioctl.IoctlNative;
import com.pi4j.plugin.ffm.providers.i2c.I2CBusFFM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class I2CDirect extends I2CBase<I2CBusFFM> {
    private static final Logger logger = LoggerFactory.getLogger(I2CDirect.class);
    private final IoctlNative IOCTL = new IoctlNative();

    public I2CDirect(I2CProvider provider, I2CConfig config, I2CBusFFM i2CBus) {
        super(provider, config, i2CBus);
    }

    @Override
    public I2C initialize(Context context) throws InitializeException {
        return super.initialize(context);
    }

    private byte[] internalRead(int size, byte[] buffer) {
        var messages = new I2CMessage[]{
            new I2CMessage(config.device(), I2cConstants.I2C_M_RD.getValue(), size, buffer),
        };
        var packets = new RDWRData(messages, 1);
        return i2CBus.execute(this, (i2cFileDescriptor) -> {
            var result = IOCTL.call(i2cFileDescriptor, I2cConstants.I2C_RDWR.getValue(), packets);
            return result.msgs()[0].buf();
        });
    }

    private byte[] internalRead(byte[] register, int size, byte[] buffer) {
        var messages = new I2CMessage[]{
            new I2CMessage(config.device(), 0, register.length, register),
            new I2CMessage(config.device(), I2cConstants.I2C_M_RD.getValue(), size, buffer),
        };
        var packets = new RDWRData(messages, 2);
        return i2CBus.execute(this, (i2cFileDescriptor) -> {
            var result = IOCTL.call(i2cFileDescriptor, I2cConstants.I2C_RDWR.getValue(), packets);
            return result.msgs()[1].buf();
        });
    }

    private int internalWrite(byte[] register, byte[] data) {
        var messages = new I2CMessage[]{
            new I2CMessage(config.device(), 0, register.length, register),
            new I2CMessage(config.device(), 0, data.length, data)
        };
        var packets = new RDWRData(messages, 2);
        return i2CBus.execute(this, (i2cFileDescriptor) -> {
            var result = IOCTL.call(i2cFileDescriptor, I2cConstants.I2C_RDWR.getValue(), packets);
            return result.msgs()[1].len();
        });
    }

    private int internalWrite(byte[] data) {
        var messages = new I2CMessage[]{
            new I2CMessage(config.device(), 0, data.length, data)
        };
        var packets = new RDWRData(messages, 1);
        return i2CBus.execute(this, (i2cFileDescriptor) -> {
            var result = IOCTL.call(i2cFileDescriptor, I2cConstants.I2C_RDWR.getValue(), packets);
            return result.msgs()[0].len();
        });
    }


    @Override
    public void close() {
        super.close();
        i2CBus.close();
    }

    @Override
    public int read() {
        return internalRead(0, new byte[1])[0];
    }

    @Override
    public int read(byte[] buffer, int offset, int length) {
        var read = internalRead(buffer.length, buffer);
        ByteBuffer.wrap(buffer).put(read);
        return read.length;
    }

    @Override
    public int write(byte b) {
        return internalWrite(new byte[]{b});
    }

    @Override
    public int write(byte[] data, int offset, int length) {
        return internalWrite(data);
    }

    @Override
    public int readRegister(int register) {
        return internalRead(new byte[]{(byte) register}, 0, new byte[0])[0];
    }

    @Override
    public int readRegister(byte[] register, byte[] buffer, int offset, int length) {
        var read = internalRead(register, buffer.length, buffer);
        ByteBuffer.wrap(buffer).put(read);
        return read.length;
    }

    @Override
    public int readRegister(int register, byte[] buffer, int offset, int length) {
        var read = internalRead(new byte[]{(byte) register}, buffer.length, buffer);
        ByteBuffer.wrap(buffer).put(read);
        return read.length;
    }

    @Override
    public int writeRegister(int register, byte b) {
        return internalWrite(new byte[]{(byte) register}, new byte[]{b});
    }

    @Override
    public int writeRegister(int register, byte[] data, int offset, int length) {
        return internalWrite(new byte[]{(byte) register}, data);
    }

    @Override
    public int writeRegister(byte[] register, byte[] data, int offset, int length) {
        return internalWrite(register, data);
    }

}

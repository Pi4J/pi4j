package com.pi4j.plugin.ffm.providers.i2c.impl;

import com.pi4j.context.Context;
import com.pi4j.exception.InitializeException;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CBase;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.i2c.I2CProvider;
import com.pi4j.plugin.ffm.common.file.FileDescriptorNative;
import com.pi4j.plugin.ffm.providers.i2c.FFMI2CBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

public class I2CFile extends I2CBase<FFMI2CBus> {
    private static final Logger logger = LoggerFactory.getLogger(I2CFile.class);

    private final FileDescriptorNative FILE = new FileDescriptorNative();

    /**
     * <p>Constructor for I2CBase.</p>
     *
     * @param provider a {@link I2CProvider} object.
     * @param config   a {@link I2CConfig} object.
     * @param i2CBus   a {@link FFMI2CBus} object.
     */
    public I2CFile(I2CProvider provider, I2CConfig config, FFMI2CBus i2CBus) {
        super(provider, config, i2CBus);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public I2C initialize(Context context) throws InitializeException {
        i2CBus.selectDevice(config.device());
        return super.initialize(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read() {
        var buffer = new byte[1];
        return i2CBus.execute(this, (i2cFileDescriptor) -> FILE.read(i2cFileDescriptor, buffer, buffer.length))[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read(byte[] buffer, int offset, int length) {
        var data = i2CBus.execute(this, (i2cFileDescriptor) -> FILE.read(i2cFileDescriptor, buffer, buffer.length));
        ByteBuffer.wrap(buffer).put(data);
        return data.length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int write(byte b) {
        return i2CBus.execute(this, (i2cFileDescriptor) -> FILE.write(i2cFileDescriptor, new byte[]{b}));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int write(byte[] data, int offset, int length) {
        return i2CBus.execute(this, (i2cFileDescriptor) -> FILE.write(i2cFileDescriptor, data));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int readRegister(int register) {
        var buffer = new byte[1];
        var read = i2CBus.execute(this, (i2cFileDescriptor) -> FILE.read(i2cFileDescriptor, buffer, buffer.length));
        ByteBuffer.wrap(buffer).put(read);
        return read.length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int readRegister(byte[] register, byte[] buffer, int offset, int length) {
        var read = i2CBus.execute(this, (i2cFileDescriptor) -> FILE.read(i2cFileDescriptor, buffer, buffer.length));
        ByteBuffer.wrap(buffer).put(read);
        return read.length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int readRegister(int register, byte[] buffer, int offset, int length) {
        var read = i2CBus.execute(this, (i2cFileDescriptor) -> FILE.read(i2cFileDescriptor, buffer, buffer.length));
        ByteBuffer.wrap(buffer).put(read);
        return read.length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int writeRegister(int register, byte b) {
        return i2CBus.execute(this, (i2cFileDescriptor) -> FILE.write(i2cFileDescriptor, new byte[]{(byte) register, b})) - 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int writeRegister(int register, byte[] data, int offset, int length) {
        var buffer = new byte[data.length + 1];
        buffer[0] = (byte) register;
        System.arraycopy(data, 0, buffer, 1, data.length);
        return i2CBus.execute(this, (i2cFileDescriptor) -> FILE.write(i2cFileDescriptor, buffer)) - 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int writeRegister(byte[] register, byte[] data, int offset, int length) {
        var buffer = new byte[register.length + data.length];
        System.arraycopy(register, 0, buffer, 0, register.length);
        System.arraycopy(data, 0, buffer, register.length, data.length);
        return i2CBus.execute(this, (i2cFileDescriptor) -> FILE.write(i2cFileDescriptor, buffer)) - 1;
    }
}

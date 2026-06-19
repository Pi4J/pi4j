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

/**
 * {@link I2C} implementation that communicates with an I2C device through plain {@code read(2)}/{@code write(2)}
 * system calls on the {@code /dev/i2c-N} character device, rather than the SMBus ioctl protocol.
 * <p>
 * The target slave address is selected once during {@link #initialize(Context)} (via {@code I2C_SLAVE}),
 * after which raw byte transfers are issued against the bus file descriptor. This is the most flexible
 * transport and works with multi-byte registers, in contrast to {@link I2CSMBus}.
 *
 * @see com.pi4j.io.i2c.I2C
 * @see FFMI2CBus
 */
public class I2CFile extends I2CBase<FFMI2CBus> {
    private static final Logger logger = LoggerFactory.getLogger(I2CFile.class);

    private final FileDescriptorNative FILE = new FileDescriptorNative();

    /**
     * Creates a file-based I2C device bound to the given bus.
     *
     * @param provider the {@link I2CProvider} that created this instance
     * @param config   the I2C configuration carrying the bus number and target slave device address
     * @param i2CBus   the shared {@link FFMI2CBus} wrapping the open {@code /dev/i2c-N} file descriptor
     */
    public I2CFile(I2CProvider provider, I2CConfig config, FFMI2CBus i2CBus) {
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
        return super.initialize(context);
    }

    @Override
    public int read() {
        var buffer = new byte[1];
        return i2CBus.execute(this, (i2cFileDescriptor) -> FILE.read(i2cFileDescriptor, buffer, buffer.length))[0];
    }

    @Override
    public int read(byte[] buffer, int offset, int length) {
        var data = i2CBus.execute(this, (i2cFileDescriptor) -> FILE.read(i2cFileDescriptor, buffer, buffer.length));
        ByteBuffer.wrap(buffer).put(data);
        return data.length;
    }

    @Override
    public int write(byte b) {
        return i2CBus.execute(this, (i2cFileDescriptor) -> FILE.write(i2cFileDescriptor, new byte[]{b}));
    }

    @Override
    public int write(byte[] data, int offset, int length) {
        return i2CBus.execute(this, (i2cFileDescriptor) -> FILE.write(i2cFileDescriptor, data));
    }

    @Override
    public int readRegister(int register) {
        var buffer = new byte[1];
        var read = i2CBus.execute(this, (i2cFileDescriptor) -> FILE.read(i2cFileDescriptor, buffer, buffer.length));
        ByteBuffer.wrap(buffer).put(read);
        return read.length;
    }

    @Override
    public int readRegister(byte[] register, byte[] buffer, int offset, int length) {
        var read = i2CBus.execute(this, (i2cFileDescriptor) -> FILE.read(i2cFileDescriptor, buffer, buffer.length));
        ByteBuffer.wrap(buffer).put(read);
        return read.length;
    }

    @Override
    public int readRegister(int register, byte[] buffer, int offset, int length) {
        var read = i2CBus.execute(this, (i2cFileDescriptor) -> FILE.read(i2cFileDescriptor, buffer, buffer.length));
        ByteBuffer.wrap(buffer).put(read);
        return read.length;
    }

    @Override
    public int writeRegister(int register, byte b) {
        return i2CBus.execute(this, (i2cFileDescriptor) -> FILE.write(i2cFileDescriptor, new byte[]{(byte) register, b})) - 1;
    }

    @Override
    public int writeRegister(int register, byte[] data, int offset, int length) {
        var buffer = new byte[data.length + 1];
        buffer[0] = (byte) register;
        System.arraycopy(data, 0, buffer, 1, data.length);
        return i2CBus.execute(this, (i2cFileDescriptor) -> FILE.write(i2cFileDescriptor, buffer)) - 1;
    }

    @Override
    public int writeRegister(byte[] register, byte[] data, int offset, int length) {
        var buffer = new byte[register.length + data.length];
        System.arraycopy(register, 0, buffer, 0, register.length);
        System.arraycopy(data, 0, buffer, register.length, data.length);
        return i2CBus.execute(this, (i2cFileDescriptor) -> FILE.write(i2cFileDescriptor, buffer)) - register.length;
    }

    @Override
    public void close() {
        super.close();
        i2CBus.close();
    }
}

package com.pi4j.plugin.ffm.providers.i2c;

import com.pi4j.context.Context;
import com.pi4j.exception.InitializeException;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CBase;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.i2c.I2CProvider;
import com.pi4j.plugin.ffm.common.file.FileDescriptorNative;
import com.pi4j.plugin.ffm.common.ioctl.IoctlNative;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Objects;

public class I2CDirect extends I2CBase<I2CBusFFM> {
    private static final Logger logger = LoggerFactory.getLogger(I2CDirect.class);
    private static final FileDescriptorNative FILE = new FileDescriptorNative();
    private static final IoctlNative IOCTL = new IoctlNative();

    public I2CDirect(I2CProvider provider, I2CConfig config, I2CBusFFM i2CBus) {
        super(provider, config, i2CBus);
    }

    @Override
    public I2C initialize(Context context) throws InitializeException {
        super.initialize(context);
        return this;
    }

    @Override
    public void close() {
        super.close();
        i2CBus.close();
    }

    /**
     * Internal method.
     * Writes the data array into the register address of device selected previously.
     *
     * @param data data array to be written
     */
    private int writeInternal(int register, byte[] data) {
            i2CBus.selectDevice(config.device());
            return i2CBus.execute(this, (i2cFileDescriptor) -> {
                var buffer = new byte[data.length + 1];
                buffer[0] = (byte) register;
                System.arraycopy(data, 0, buffer, 1, data.length);
                return FILE.write(i2cFileDescriptor, buffer);
            });
    }

    @Override
    public int write(byte b) {
        return writeInternal(this.config.device(), new byte[]{b});
    }

    @Override
    public int write(byte[] data, int offset, int length) {
        Objects.checkFromIndexSize(offset, length, data.length);
        var writeData = Arrays.copyOfRange(data, offset, offset + length);
        return writeInternal(this.config.device(), writeData);
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
        var byteRegister = register[0];
        var writeData = new byte[data.length + register.length - 1];
        System.arraycopy(Arrays.copyOfRange(register, 1, register.length), 0, writeData, 0, register.length - 1);
        System.arraycopy(data, 0, writeData, register.length - 1, data.length);
        return writeInternal(byteRegister, writeData);
    }


    /**
     * Internal method.
     * Reads the data byte from the register address of device selected previously.
     *
     * @param register register address of selected device
     * @return data byte read from register
     */
    private byte[] readInternal(int register, int size) {
            i2CBus.selectDevice(config.device());
            return i2CBus.execute(this, (i2cFileDescriptor) -> FILE.read(i2cFileDescriptor, new byte[size], size));
    }

    @Override
    public int read() {
        var dataRead = readInternal(this.config.device(), 1);
        return dataRead[0];
    }

    @Override
    public int read(byte[] buffer, int offset, int length) {
        Objects.checkFromIndexSize(offset, length, buffer.length);
        System.arraycopy(readInternal(this.config.device(), buffer.length), 0, buffer, offset, length);
        return length;
    }

    @Override
    public int readRegister(int register) {
        var dataRead = readInternal(register, 1);
        return dataRead[0];
    }

    @Override
    public int readRegister(byte[] register, byte[] buffer, int offset, int length) {
        // TODO: how to implement?
        return length;
    }

    @Override
    public int readRegister(int register, byte[] buffer, int offset, int length) {
        Objects.checkFromIndexSize(offset, length, buffer.length);
        System.arraycopy(readInternal(register, length), 0, buffer, offset, length);
        return length;
    }

}

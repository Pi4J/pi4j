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

    @Override
    public void close() {
        super.close();
        i2CBus.close();
    }

    @Override
    public int read() {
        return 0;
    }

    @Override
    public int read(byte[] buffer, int offset, int length) {
        return 0;
    }

    @Override
    public int write(byte b) {
        return 0;
    }

    @Override
    public int write(byte[] data, int offset, int length) {
        return 0;
    }

    @Override
    public int readRegister(int register) {
        var messages = new I2CMessage[] {
            new I2CMessage(config.device(), 0,1, new byte[] {(byte) register}),
            new I2CMessage(config.device(), I2cConstants.I2C_M_RD.getValue(),0, new byte[0]),
        };
        var packets = new RDWRData(messages, 2);
        return i2CBus.execute(this, (i2cFileDescriptor) -> {
            var result = IOCTL.call(i2cFileDescriptor, I2cConstants.I2C_RDWR.getValue(), packets);
            return (int) result.msgs()[1].buf()[0];
        });
    }

    @Override
    public int readRegister(byte[] register, byte[] buffer, int offset, int length) {
        return 0;
    }

    @Override
    public int readRegister(int register, byte[] buffer, int offset, int length) {
        return 0;
    }

    @Override
    public int writeRegister(int register, byte b) {
        return 0;
    }

    @Override
    public int writeRegister(int register, byte[] data, int offset, int length) {
        var buffer = new byte[data.length + 1];
        buffer[0] = (byte) register;
        System.arraycopy(data, 0, buffer, 1, data.length);
        var messages = new I2CMessage[] {new I2CMessage(config.device(), 0, data.length, buffer)};
        var packets = new RDWRData(messages, 1);
        return i2CBus.execute(this, (i2cFileDescriptor) -> {
            IOCTL.call(i2cFileDescriptor, I2cConstants.I2C_RDWR.getValue(), packets);
            return 0;
        });
    }

    @Override
    public int writeRegister(byte[] register, byte[] data, int offset, int length) {
        return 0;
    }

}

package com.pi4j.plugin.ffm.providers.i2c;

import com.pi4j.context.Context;
import com.pi4j.exception.InitializeException;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CBase;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.i2c.I2CProvider;

public class FFMI2C extends I2CBase<FFMI2CBus> implements I2C {

    public FFMI2C(FFMI2CBus i2CBus, I2CProvider provider, I2CConfig config) {
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
    }

    // -------------------------------------------------------------------
    // RAW DEVICE WRITE FUNCTIONS
    // -------------------------------------------------------------------

    @Override
    public int write(byte b) {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int write(byte[] data, int offset, int length) {
        return 0;
    }

    // -------------------------------------------------------------------
    // RAW DEVICE READ FUNCTIONS
    // -------------------------------------------------------------------

    @Override
    public int read() {
        return 0;
    }

    @Override
    public int read(byte[] buffer, int offset, int length) {
        return 0;
    }

    // -------------------------------------------------------------------
    // DEVICE REGISTER WRITE FUNCTIONS
    // -------------------------------------------------------------------

    @Override
    public int writeRegister(int register, byte b) {
        return 0;
    }

    @Override
    public int writeRegister(int register, byte[] data, int offset, int length) {
        return 0;
    }

    @Override
    public int writeRegister(byte[] register, byte[] data, int offset, int length) {
        throw new IllegalStateException("Not supported, please use LinuxFS plugin");
    }

    // -------------------------------------------------------------------
    // DEVICE REGISTER READ FUNCTIONS
    // -------------------------------------------------------------------

    @Override
    public int readRegister(int register) {
        return 0;
    }

    @Override
    public int readRegister(byte[] register, byte[] buffer, int offset, int length) {
        throw new IllegalStateException("Not supported, please use LinuxFS plugin");
    }

    @Override
    public int readRegister(int register, byte[] buffer, int offset, int length) {
        return 0;
    }

    @Override
    public int writeReadRegisterWord(int register, int word) {
        return 0;
    }

}

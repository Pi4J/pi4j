package com.pi4j.io.i2c.impl;

import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CRegister;
import com.pi4j.io.i2c.I2CRegisterDataReader;
import com.pi4j.io.i2c.I2CRegisterDataWriter;

import java.nio.charset.Charset;
import java.util.Objects;

public class DefaultI2CRegister implements I2CRegister, I2CRegisterDataReader, I2CRegisterDataWriter {

    protected final int address;
    protected final I2C i2c;

    public DefaultI2CRegister(I2C i2c, int address) {
        this.i2c = i2c;
        this.address = address;
    }

    @Override
    public int getAddress() {
        return this.address;
    }

    @Override
    public int write(byte b) {
        return this.i2c.writeRegister(this.address, b);
    }


    /**
     * {@inheritDoc}
     * <p>
     * Write a single word value (16-bit) to the I2C device register.
     */
    @Override
    public void writeWord(int word) {
        this.i2c.writeRegisterWord(this.address, word);
    }

    @Override
    public int write(byte[] data, int offset, int length) {
        Objects.checkFromIndexSize(offset, length, data.length);
        return this.i2c.writeRegister(this.address, data, offset, length);
    }

    @Override
    public int readWord() {
        return this.i2c.readRegisterWord(this.address);
    }

    @Override
    public int writeReadWord(int word) {
        return this.i2c.writeReadRegisterWord(this.address, word);
    }

    @Override
    public int read() {
        return this.i2c.readRegister(this.address);
    }

    @Override
    public int read(byte[] buffer, int offset, int length) {
        Objects.checkFromIndexSize(offset, length, buffer.length);
        return this.i2c.readRegister(this.address, buffer, offset, length);
    }

    @Override
    public String readString(Charset charset, int length) {
        return this.i2c.readRegisterString(this.address, charset, length);
    }

    @Override
    public int readRegister(int register) {
        return this.i2c.readRegister(register);
    }


    @Override
    public int readRegister(byte[] register, byte[] buffer, int offset, int length) {
        return this.i2c.readRegister(register, buffer, offset, length);
    }


    @Override
    public int readRegister(int register, byte[] buffer, int offset, int length) {
        return this.i2c.readRegister(register, buffer, offset, length);
    }


    @Override
    public int writeRegister(int register, byte b) {
        return this.i2c.writeRegister(register, b);
    }

    @Override
    public int writeRegister(int register, byte[] data, int offset, int length) {
        return this.i2c.writeRegister(register, data, offset, length);
    }

    @Override
    public int writeRegister(byte[] register, byte[] data, int offset, int length) {
        return this.i2c.writeRegister(register, data, offset, length);
    }
}

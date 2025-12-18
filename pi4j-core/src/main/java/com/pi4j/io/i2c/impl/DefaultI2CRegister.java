package com.pi4j.io.i2c.impl;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  DefaultI2CRegister.java
 *
 * This file is part of the Pi4J project. More information about
 * this project can be found here:  https://pi4j.com/
 * **********************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CRegister;
import com.pi4j.io.i2c.I2CRegisterDataReader;
import com.pi4j.io.i2c.I2CRegisterDataWriter;

import java.nio.charset.Charset;
import java.util.Objects;

/**
 * <p>DefaultI2CRegister class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public class DefaultI2CRegister implements I2CRegister, I2CRegisterDataReader, I2CRegisterDataWriter {

    protected final int address;
    protected final I2C i2c;

    /**
     * <p>Constructor for DefaultI2CRegister.</p>
     *
     * @param i2c a {@link com.pi4j.io.i2c.I2C} object.
     * @param address a int.
     */
    public DefaultI2CRegister(I2C i2c, int address){
        this.i2c = i2c;
        this.address = address;
    }

    /** {@inheritDoc} */
    @Override
    public int getAddress() {
        return this.address;
    }

    /** {@inheritDoc} */
    @Override
    public int write(byte b) {
        return this.i2c.writeRegister(this.address, b);
    }



    /**
     * {@inheritDoc}
     *
     * Write a single word value (16-bit) to the I2C device register.
     */
    @Override
    public void writeWord(int word) {
        this.i2c.writeRegisterWord(this.address, word);
    }

    /** {@inheritDoc} */
    @Override
    public int write(byte[] data, int offset, int length) {
        Objects.checkFromIndexSize(offset, length, data.length);
        return this.i2c.writeRegister(this.address, data, offset, length);
    }

    /** {@inheritDoc} */
    @Override
    public int readWord() {
        return this.i2c.readRegisterWord(this.address);
    }

    /** {@inheritDoc} */
    @Override
    public int writeReadWord(int word) {
        return this.i2c.writeReadRegisterWord(this.address, word);
    }

    /** {@inheritDoc} */
    @Override
    public int read() {
        return this.i2c.readRegister(this.address);
    }

    /** {@inheritDoc} */
    @Override
    public int read(byte[] buffer, int offset, int length) {
        Objects.checkFromIndexSize(offset, length, buffer.length);
        return this.i2c.readRegister(this.address, buffer, offset, length);
    }

    /** {@inheritDoc} */
    @Override
    public String readString(Charset charset, int length) {
        return this.i2c.readRegisterString(this.address, charset, length);
    }

    /** {@inheritDoc} */
    @Override
    public int readRegister(int register) {
        return this.i2c.readRegister(register);
    }


    /** {@inheritDoc} */
    @Override
    public int readRegister(byte[] register, byte[] buffer, int offset, int length) {
        return this.i2c.readRegister(register,buffer,offset,length);
    }


    /** {@inheritDoc} */
    @Override
    public int readRegister(int register, byte[] buffer, int offset, int length) {
        return this.i2c.readRegister(register,buffer,offset,length);
    }


    /** {@inheritDoc} */
    @Override
    public int writeRegister(int register, byte b) {
        return this.i2c.writeRegister(register, b);
    }

    /** {@inheritDoc} */
    @Override
    public int writeRegister(int register, byte[] data, int offset, int length) {
        return this.i2c.writeRegister(register,data,offset,length);
    }

    /** {@inheritDoc} */
    @Override
    public int writeRegister(byte[] register, byte[] data, int offset, int length) {
        return this.i2c.writeRegister(register,data,offset,length);
    }
}

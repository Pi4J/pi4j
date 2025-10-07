package com.pi4j.io.i2c;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  I2CBase.java
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

import com.pi4j.context.Context;
import com.pi4j.exception.ShutdownException;
import com.pi4j.io.IOBase;
import com.pi4j.io.i2c.impl.DefaultI2CRegister;

import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * <p>Abstract I2CBase class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 * @param <T>
 */
public abstract class I2CBase<T extends I2CBus> extends IOBase<I2C, I2CConfig, I2CProvider> implements I2C {

    protected boolean isOpen;
    protected final T i2CBus;

    /**
     * <p>Constructor for I2CBase.</p>
     *
     * @param provider a {@link I2CProvider} object.
     * @param config   a {@link I2CConfig} object.
     * @param i2CBus   a {@link I2CBus} object.
     */
    public I2CBase(I2CProvider provider, I2CConfig config, T i2CBus) {
        super(provider, config);
        this.isOpen = true;
        this.i2CBus = i2CBus;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isOpen() {
        return this.isOpen;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        this.isOpen = false;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Get an encapsulated interface for reading and writing to a specific I2C device register
     */
    public I2CRegister getRegister(int address) {
        return new DefaultI2CRegister(this, address);
    }

    @Override
    public int readRegister(int register) {
        write(register);
        return read();
    }

    @Override
    public int readRegister(byte[] register, byte[] buffer, int offset, int length) {
        write(register);
        return read(buffer, offset, length);
    }

    @Override
    public int readRegister(int register, byte[] buffer, int offset, int length) {
        write(register);
        return read(buffer, offset, length);
    }

    @Override
    public int writeRegister(int register, byte b) {
        return write((byte) register, b);
    }

    @Override
    public int writeRegister(int register, byte[] data, int offset, int length) {
        Objects.checkFromIndexSize(offset, length, data.length);
        byte[] tmp = new byte[length + 1];
        tmp[0] = (byte) register;
        System.arraycopy(data, offset, tmp, 1, length);
        return write(tmp);
    }

    @Override
    public int writeRegister(byte[] register, byte[] data, int offset, int length) {
        Objects.checkFromIndexSize(offset, length, data.length);
        byte[] tmp = new byte[length + register.length];
        System.arraycopy(register,0, tmp, 0, register.length);
        System.arraycopy(data, offset, tmp, register.length, length);
        int rc = write(tmp);
        return (rc - register.length);  // do not include the register bytes as what was written...
    }

    @Override
    public <V> V execute(Callable<V> action) {
        if (action == null)
            throw new NullPointerException("Parameter 'action' is mandatory!");
        return this.i2CBus.execute(this, action);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public I2C shutdownInternal(Context context) throws ShutdownException {
        // if this I2C device is still open, then we need to close it since we are shutting down
        if (this.isOpen()) {
            try {
                close();
            } catch (Exception e) {
                throw new ShutdownException(e);
            }
        }
        return this;
    }
}

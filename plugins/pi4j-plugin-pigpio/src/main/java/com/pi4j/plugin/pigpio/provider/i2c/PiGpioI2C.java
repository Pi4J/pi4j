package com.pi4j.plugin.pigpio.provider.i2c;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: PLUGIN   :: PIGPIO I/O Providers
 * FILENAME      :  PiGpioI2C.java
 *
 * This file is part of the Pi4J project. More information about
 * this project can be found here:  https://pi4j.com/
 * **********************************************************************
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import com.pi4j.context.Context;
import com.pi4j.exception.InitializeException;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CBase;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.i2c.I2CProvider;
import com.pi4j.library.pigpio.PiGpio;
import com.pi4j.library.pigpio.PiGpioMode;

import java.util.Objects;

/**
 * <p>PiGpioI2C class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public class PiGpioI2C extends I2CBase<PiGpioI2CBus> implements I2C {

    protected final PiGpio piGpio;
    protected final int handle;

    /**
     * <p>Constructor for PiGpioI2C.</p>
     *
     * @param piGpio   a {@link PiGpio} object.
     * @param i2CBus   a {@link PiGpioI2CBus} object.
     * @param provider a {@link I2CProvider} object.
     * @param config   a {@link I2CConfig} object.
     */
    public PiGpioI2C(PiGpio piGpio, PiGpioI2CBus i2CBus, I2CProvider provider, I2CConfig config) {
        super(provider, config, i2CBus);

        // set local reference instance
        this.piGpio = piGpio;

        // set pin ALT0 modes for I2C BUS<1> or BUS<2> usage on RPI3B
        switch (config.bus()) {
            case 0: {
                piGpio.gpioSetMode(0, PiGpioMode.ALT0);
                piGpio.gpioSetMode(1, PiGpioMode.ALT0);
                break;
            }
            case 1: {
                piGpio.gpioSetMode(2, PiGpioMode.ALT0);
                piGpio.gpioSetMode(3, PiGpioMode.ALT0);
            }
        }

        // create I2C instance of PIGPIO I2C
        this.handle = piGpio.i2cOpen(config.bus(), config.device());

        // set open state flag
        this.isOpen = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public I2C initialize(Context context) throws InitializeException {
        super.initialize(context);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        piGpio.i2cClose(this.handle);
        super.close();
    }

    // -------------------------------------------------------------------
    // RAW DEVICE WRITE FUNCTIONS
    // -------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public int write(byte b) {
        return piGpio.i2cWriteByte(this.handle, b);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int write(byte[] data, int offset, int length) {
        Objects.checkFromIndexSize(offset, length, data.length);
        piGpio.i2cWriteDevice(this.handle, data, offset, length);
        return length;
    }

    // -------------------------------------------------------------------
    // RAW DEVICE READ FUNCTIONS
    // -------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public int read() {
        return piGpio.i2cReadByte(this.handle);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read(byte[] buffer, int offset, int length) {
        Objects.checkFromIndexSize(offset, length, buffer.length);
        return piGpio.i2cReadDevice(this.handle, buffer, offset, length);
    }

    // -------------------------------------------------------------------
    // DEVICE REGISTER WRITE FUNCTIONS
    // -------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public int writeRegister(int register, byte b) {
        return piGpio.i2cWriteByteData(this.handle, register, b);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int writeRegister(int register, byte[] data, int offset, int length) {
        Objects.checkFromIndexSize(offset, length, data.length);
        piGpio.i2cWriteI2CBlockData(this.handle, register, data, offset, length);
        return length;
    }

    /**
     * {@inheritDoc}
     * <p> Note: Function not supported with PIGPIO provider.
     * This method 'is' supported in the LinuxFS provider
     * </p>
     */
    @Override
    public int writeRegister(byte[] register, byte[] data, int offset, int length) {
        throw new IllegalStateException("Not supported, please use LinuxFS plugin");
    }

    // -------------------------------------------------------------------
    // DEVICE REGISTER READ FUNCTIONS
    // -------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public int readRegister(int register) {
        return piGpio.i2cReadByteData(this.handle, register);
    }

    /**
     * {@inheritDoc}
     * <p> Note: Function not supported with PIGPIO provider.
     * This method 'is' supported in the LinuxFS provider
     * </p>
     */
    @Override
    public int readRegister(byte[] register, byte[] buffer, int offset, int length) {
        throw new IllegalStateException("Not supported, please use LinuxFS plugin");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int readRegister(int register, byte[] buffer, int offset, int length) {
        Objects.checkFromIndexSize(offset, length, buffer.length);
        return piGpio.i2cReadI2CBlockData(this.handle, register, buffer, offset, length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int writeReadRegisterWord(int register, int word) {
        return piGpio.i2cProcessCall(this.handle, register, word);
    }
}

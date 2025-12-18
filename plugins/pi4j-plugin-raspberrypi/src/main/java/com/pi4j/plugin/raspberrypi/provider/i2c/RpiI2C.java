package com.pi4j.plugin.raspberrypi.provider.i2c;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: PLUGIN   :: RaspberryPi Platform & Providers
 * FILENAME      :  RpiI2C.java
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


import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CBase;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.i2c.I2CProvider;

/**
 * <p>RpiI2C class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public class RpiI2C extends I2CBase<RpiI2CBus> implements I2C {

    /**
     * <p>Constructor for RpiI2C.</p>
     *
     * @param provider a {@link com.pi4j.io.i2c.I2CProvider} object.
     * @param config a {@link com.pi4j.io.i2c.I2CConfig} object.
     */
    public RpiI2C(I2CProvider provider, I2CConfig config){
        super(provider, config, new RpiI2CBus(config));
    }

    /** {@inheritDoc} */
    @Override
    public int read() {
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public int read(byte[] buffer, int offset, int length) {
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public int write(byte b) {
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public int write(byte[] data, int offset, int length) {
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public int readRegister(int register) {
        return 0;
    }

    @Override
    public int readRegister(byte[] register, byte[] buffer, int offset, int length) {
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public int readRegister(int register, byte[] buffer, int offset, int length) {
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public int writeRegister(int register, byte b) {
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public int writeRegister(int register, byte[] data, int offset, int length) {
        return 0;
    }

    @Override
    public int writeRegister(byte[] register, byte[] data, int offset, int length) {
        return 0;
    }
}

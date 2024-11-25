package com.pi4j.plugin.linuxfs.provider.i2c;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: PLUGIN   :: PIGPIO I/O Providers
 * FILENAME      :  PiGpioI2CProviderImpl.java
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
import com.pi4j.exception.ShutdownException;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.i2c.I2CProvider;
import com.pi4j.io.i2c.I2CProviderBase;

import java.util.HashMap;
import java.util.Map;

public class LinuxFsI2CProviderImpl extends I2CProviderBase implements LinuxFsI2CProvider {

    private final Map<Integer, LinuxFsI2CBus> i2CBusMap;

    public LinuxFsI2CProviderImpl() {
        this.id = ID;
        this.name = NAME;
        this.i2CBusMap = new HashMap<>();
    }

    @Override
    public int getPriority() {
       // the linux FS driver should always be higher priority
       return 150;
    }

    @Override
    public synchronized I2C create(I2CConfig config) {
        LinuxFsI2CBus i2CBus = this.i2CBusMap.computeIfAbsent(config.getBus(), busNr -> new LinuxFsI2CBus(config));
        // create new I/O instance based on I/O config
        LinuxFsI2C i2C = new LinuxFsI2C(i2CBus, this, config);
        this.context.registry().add(i2C);
        return i2C;
    }

    @Override
    public I2CProvider shutdown(Context context) throws ShutdownException {
        this.i2CBusMap.forEach(((busNr, bus) -> bus.close()));
        this.i2CBusMap.clear();
        return super.shutdown(context);
    }
}

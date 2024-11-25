package com.pi4j.plugin.linuxfs.provider.gpio.digital;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: PLUGIN   :: LinuxFS I/O Providers
 * FILENAME      :  LinuxFsDigitalInputProviderImpl.java
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


import com.pi4j.boardinfo.util.BoardInfoHelper;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalInputConfig;
import com.pi4j.io.gpio.digital.DigitalInputProviderBase;
import com.pi4j.plugin.linuxfs.internal.LinuxGpio;

/**
 * <p>LinuxFsDigitalInputProviderImpl class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public class LinuxFsDigitalInputProviderImpl extends DigitalInputProviderBase implements LinuxFsDigitalInputProvider {

    final String gpioFileSystemPath;

    /**
     * <p>Constructor for LinuxFsDigitalInputProviderImpl.</p>
     * @param gpioFileSystemPath
     */
    public LinuxFsDigitalInputProviderImpl(String gpioFileSystemPath) {
        this.id = ID;
        this.name = NAME;
        this.gpioFileSystemPath = gpioFileSystemPath;
    }

    @Override
    public int getPriority() {
        // the linux FS Digital driver should be higher priority on RP1 chip
        return BoardInfoHelper.usesRP1() ? 100 : 50;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DigitalInput create(DigitalInputConfig config) {
        // create filesystem based GPIO instance using instance address (GPIO NUMBER)
        LinuxGpio gpio = new LinuxGpio(this.gpioFileSystemPath, config.address());
        LinuxFsDigitalInput digitalInput = new LinuxFsDigitalInput(gpio, this, config);
        this.context.registry().add(digitalInput);
        return digitalInput;
    }
}

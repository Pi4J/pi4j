package com.pi4j.plugin.linuxfs.provider.pwm;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: PLUGIN   :: LinuxFS I/O Providers
 * FILENAME      :  LinuxFsPwmProviderImpl.java
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
import com.pi4j.io.exception.IOException;
import com.pi4j.io.pwm.Pwm;
import com.pi4j.io.pwm.PwmConfig;
import com.pi4j.io.pwm.PwmProviderBase;
import com.pi4j.io.pwm.PwmType;
import com.pi4j.plugin.linuxfs.internal.LinuxPwm;

/**
 * <p>LinuxFsPwmProviderImpl class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public class LinuxFsPwmProviderImpl extends PwmProviderBase implements LinuxFsPwmProvider {

    final String pwmFileSystemPath;

    /**
     * <p>Constructor for LinuxFsPwmProviderImpl.</p>
     *
     * @param pwmFileSystemPath Path to PWM device tree
     */
    public LinuxFsPwmProviderImpl(String pwmFileSystemPath) {
        this.id = ID;
        this.name = NAME;
        this.pwmFileSystemPath = pwmFileSystemPath;
    }

    @Override
    public int getPriority() {
        // the linux FS PWM driver should be higher priority on RP1 chip
        return BoardInfoHelper.usesRP1() ? 100 : 50;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pwm create(PwmConfig config) {
        // validate PWM type
        if (config.pwmType() != PwmType.HARDWARE) {
            throw new IOException("The Linux file system PWM provider only supports HARDWARE PWM");
        }

        // validate the config
        if (config.chip() == null || config.channel() == null) {
            throw new IllegalArgumentException("PWM Chip and Channel are needed for hardware PWM with the LinuxFS I/O provider");
        }

        // create filesystem based PWM instance
        LinuxPwm pwm = new LinuxPwm(this.pwmFileSystemPath, config.chip(), config.channel());
        LinuxFsPwm fsPwm = new LinuxFsPwm(pwm, this, config);
        this.context.registry().add(fsPwm);
        return fsPwm;
    }
}

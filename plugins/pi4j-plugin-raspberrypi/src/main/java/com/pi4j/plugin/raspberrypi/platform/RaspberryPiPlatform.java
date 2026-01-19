package com.pi4j.plugin.raspberrypi.platform;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: PLUGIN   :: RaspberryPi Platform & Providers
 * FILENAME      :  RaspberryPiPlatform.java
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
import com.pi4j.platform.Platform;
import com.pi4j.platform.PlatformBase;
import com.pi4j.plugin.raspberrypi.RaspberryPi;
import com.pi4j.plugin.raspberrypi.provider.gpio.digital.RpiDigitalInputProvider;
import com.pi4j.plugin.raspberrypi.provider.gpio.digital.RpiDigitalOutputProvider;
import com.pi4j.plugin.raspberrypi.provider.i2c.RpiI2CProvider;
import com.pi4j.plugin.raspberrypi.provider.pwm.RpiPwmProvider;
import com.pi4j.plugin.raspberrypi.provider.spi.RpiSpiProvider;

/**
 * <p>RaspberryPiPlatform class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public class RaspberryPiPlatform extends PlatformBase<RaspberryPiPlatform> implements Platform {


    /**
     * <p>Constructor for RaspberryPiPlatform.</p>
     */
    public RaspberryPiPlatform() {
        super(RaspberryPi.PLATFORM_ID,
            RaspberryPi.PLATFORM_NAME,
            RaspberryPi.PLATFORM_DESCRIPTION);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int priority() {
        // this platform has a priority of 5 to indicate that it is likely to be used
        // in the case where other platforms are not found in the classpath
        return 5;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean enabled(Context context) {
        // the Mock Platform is always available when detected
        // there are no logic checked required to determine when
        // and if the mock platforms should be enabled
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String[] getProviders() {
        return new String[]{
            RpiDigitalInputProvider.ID,
            RpiDigitalOutputProvider.ID,
            RpiPwmProvider.ID,
            RpiI2CProvider.ID,
            RpiSpiProvider.ID
        };
    }
}

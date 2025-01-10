package com.pi4j.plugin.mock.platform;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: PLUGIN   :: Mock Platform & Providers
 * FILENAME      :  MockPlatform.java
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
import com.pi4j.plugin.mock.Mock;
import com.pi4j.plugin.mock.provider.gpio.analog.MockAnalogInputProvider;
import com.pi4j.plugin.mock.provider.gpio.analog.MockAnalogOutputProvider;
import com.pi4j.plugin.mock.provider.gpio.digital.MockDigitalInputProvider;
import com.pi4j.plugin.mock.provider.gpio.digital.MockDigitalOutputProvider;
import com.pi4j.plugin.mock.provider.i2c.MockI2CProvider;
import com.pi4j.plugin.mock.provider.onewire.MockOneWireProvider;
import com.pi4j.plugin.mock.provider.pwm.MockPwmProvider;
import com.pi4j.plugin.mock.provider.serial.MockSerialProvider;
import com.pi4j.plugin.mock.provider.spi.MockSpiProvider;

/**
 * <p>MockPlatform class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public class MockPlatform extends PlatformBase<MockPlatform> implements Platform {


    /**
     * <p>Constructor for MockPlatform.</p>
     */
    public MockPlatform(){
        super(Mock.PLATFORM_ID,
              Mock.PLATFORM_NAME,
              Mock.PLATFORM_DESCRIPTION);
    }

    /** {@inheritDoc} */
    @Override
    public int priority() {
        // the MOCK platform has a priority of -1000 to indicate that it has a very
        // low priority and should only be used in the case where other platforms
        // are not found in the classpath
        return -1000;
    }

    /** {@inheritDoc} */
    @Override
    public boolean enabled(Context context) {
        // the Mock Platform is always available when detected
        // there are no logic checked required to determine when
        // and if the mock platforms should be enabled
        return true;
    }

    /** {@inheritDoc} */
    @Override
    protected String[] getProviders() {
        return new String[] {
            MockAnalogInputProvider.ID,
            MockAnalogOutputProvider.ID,
            MockDigitalInputProvider.ID,
            MockDigitalOutputProvider.ID,
            MockPwmProvider.ID,
            MockSpiProvider.ID,
            MockI2CProvider.ID,
            MockSerialProvider.ID,
            MockOneWireProvider.ID
        };
    }
}

package com.pi4j.plugin.mock;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: PLUGIN   :: Mock Platform & Providers
 * FILENAME      :  MockPlugin.java
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

import com.pi4j.extension.Plugin;
import com.pi4j.extension.PluginService;
import com.pi4j.plugin.mock.provider.gpio.digital.MockDigitalInputProvider;
import com.pi4j.plugin.mock.provider.gpio.digital.MockDigitalOutputProvider;
import com.pi4j.plugin.mock.provider.i2c.MockI2CProvider;
import com.pi4j.plugin.mock.provider.pwm.MockPwmProvider;
import com.pi4j.plugin.mock.provider.spi.MockSpiProvider;
import com.pi4j.provider.Provider;

/**
 * Pi4J {@link Plugin} that contributes the in-memory, hardware-free Mock providers, allowing Pi4J to
 * run without real GPIO/I2C/SPI/PWM hardware (for example in unit tests).
 * <p>
 * On {@link #initialize(PluginService) initialization} it registers the mock implementations of the
 * pi4j-core I/O contracts: {@link MockDigitalInputProvider}, {@link MockDigitalOutputProvider},
 * {@link MockPwmProvider}, {@link MockI2CProvider} and {@link MockSpiProvider}.
 */
public class MockPlugin implements Plugin {

    private final Provider[] providers = {
        MockDigitalInputProvider.newInstance(),
        MockDigitalOutputProvider.newInstance(),
        MockPwmProvider.newInstance(),
        MockI2CProvider.newInstance(),
        MockSpiProvider.newInstance()
    };

    /**
     * {@inheritDoc}
     * <p>
     * Always returns {@code true}, marking this plugin and its providers as mock (non-hardware)
     * implementations.
     */
    @Override
    public boolean isMock() {
        return true;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Registers all Mock I/O providers (digital input/output, PWM, I2C and SPI) with the given
     * {@link PluginService} so they become available to the Pi4J runtime.
     */
    @Override
    public void initialize(PluginService service) {

        // register the Mock Platform and all Mock I/O Providers with the plugin service
        service.register().register(providers);
    }
}

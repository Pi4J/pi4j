package com.pi4j.plugin.pigpio;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: PLUGIN   :: PIGPIO I/O Providers
 * FILENAME      :  PiGpioPlugin.java
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
import com.pi4j.exception.ShutdownException;
import com.pi4j.extension.Plugin;
import com.pi4j.extension.PluginService;
import com.pi4j.library.pigpio.PiGpio;
import com.pi4j.plugin.pigpio.provider.gpio.digital.PiGpioDigitalInputProvider;
import com.pi4j.plugin.pigpio.provider.gpio.digital.PiGpioDigitalOutputProvider;
import com.pi4j.plugin.pigpio.provider.i2c.PiGpioI2CProvider;
import com.pi4j.plugin.pigpio.provider.pwm.PiGpioPwmProvider;
import com.pi4j.plugin.pigpio.provider.spi.PiGpioSpiProvider;
import com.pi4j.provider.Provider;

/**
 * <p>PiGpioPlugin class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public class PiGpioPlugin implements Plugin {

    /**
     * Constant <code>NAME="PiGpio"</code>
     */
    public static final String NAME = "PiGpio";
    /**
     * Constant <code>ID="pigpio"</code>
     */
    public static final String ID = "pigpio";

    // Digital Input (GPIO) Provider name and unique ID
    /**
     * Constant <code>DIGITAL_INPUT_PROVIDER_NAME="NAME +   Digital Input (GPIO) Provider"</code>
     */
    public static final String DIGITAL_INPUT_PROVIDER_NAME = NAME + " Digital Input (GPIO) Provider";
    /**
     * Constant <code>DIGITAL_INPUT_PROVIDER_ID="ID + -digital-input"</code>
     */
    public static final String DIGITAL_INPUT_PROVIDER_ID = ID + "-digital-input";

    // Digital Output (GPIO) Provider name and unique ID
    /**
     * Constant <code>DIGITAL_OUTPUT_PROVIDER_NAME="NAME +   Digital Output (GPIO) Provider"</code>
     */
    public static final String DIGITAL_OUTPUT_PROVIDER_NAME = NAME + " Digital Output (GPIO) Provider";
    /**
     * Constant <code>DIGITAL_OUTPUT_PROVIDER_ID="ID + -digital-output"</code>
     */
    public static final String DIGITAL_OUTPUT_PROVIDER_ID = ID + "-digital-output";

    // PWM Provider name and unique ID
    /**
     * Constant <code>PWM_PROVIDER_NAME="NAME +  PWM Provider"</code>
     */
    public static final String PWM_PROVIDER_NAME = NAME + " PWM Provider";
    /**
     * Constant <code>PWM_PROVIDER_ID="ID + -pwm"</code>
     */
    public static final String PWM_PROVIDER_ID = ID + "-pwm";

    // PWM Provider name and unique ID
    /**
     * Constant <code>HW_PWM_PROVIDER_NAME="NAME +  Hardware PWM Provider"</code>
     */
    public static final String HW_PWM_PROVIDER_NAME = NAME + " Hardware PWM Provider";
    /**
     * Constant <code>HW_PWM_PROVIDER_ID="ID + -hardware-pwm"</code>
     */
    public static final String HW_PWM_PROVIDER_ID = ID + "-hardware-pwm";

    // I2C Provider name and unique ID
    /**
     * Constant <code>I2C_PROVIDER_NAME="NAME +  I2C Provider"</code>
     */
    public static final String I2C_PROVIDER_NAME = NAME + " I2C Provider";
    /**
     * Constant <code>I2C_PROVIDER_ID="ID + -i2c"</code>
     */
    public static final String I2C_PROVIDER_ID = ID + "-i2c";

    // SPI Provider name and unique ID
    /**
     * Constant <code>SPI_PROVIDER_NAME="NAME +  SPI Provider"</code>
     */
    public static final String SPI_PROVIDER_NAME = NAME + " SPI Provider";
    /**
     * Constant <code>SPI_PROVIDER_ID="ID + -spi"</code>
     */
    public static final String SPI_PROVIDER_ID = ID + "-spi";

    // Serial Provider name and unique ID
    /**
     * Constant <code>SERIAL_PROVIDER_NAME="NAME +  Serial Provider"</code>
     */
    public static final String SERIAL_PROVIDER_NAME = NAME + " Serial Provider";
    /**
     * Constant <code>SERIAL_PROVIDER_ID="ID + -serial"</code>
     */
    public static final String SERIAL_PROVIDER_ID = ID + "-serial";

    protected PiGpio piGpio = null;

    /**
     * Constant <code>PI4J_HOST_PROPERTY="pi4j.host"</code>
     */
    public static String PI4J_HOST_PROPERTY = "pi4j.host";
    /**
     * Constant <code>PIGPIO_HOST_PROPERTY="pi4j.pigpio.host"</code>
     */
    public static String PIGPIO_HOST_PROPERTY = "pi4j.pigpio.host";
    /**
     * Constant <code>PIGPIO_PORT_PROPERTY="pi4j.pigpio.port"</code>
     */
    public static String PIGPIO_PORT_PROPERTY = "pi4j.pigpio.port";
    /**
     * Constant <code>DEFAULT_PIGPIO_HOST="127.0.0.1"</code>
     */
    public static String DEFAULT_PIGPIO_HOST = "127.0.0.1";
    /**
     * Constant <code>DEFAULT_PIGPIO_PORT</code>
     */
    public static Integer DEFAULT_PIGPIO_PORT = 8888;
    /**
     * Constant <code>DEFAULT_PIGPIO_REMOTE</code>
     */
    public static Boolean DEFAULT_PIGPIO_REMOTE = false;

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(PluginService service) throws InitializeException {

        // get PIGPIO hostname/ip-address and ip-port
        Boolean remote = DEFAULT_PIGPIO_REMOTE;
        String host = DEFAULT_PIGPIO_HOST;
        int port = DEFAULT_PIGPIO_PORT;

        // get the universal 'remote' setting for Pi4J context
        if (service.context().properties().has("remote")) {
            remote = Boolean.parseBoolean(service.context().properties().get("remote", remote.toString()));
        }

        // if there is an overriding 'pigpio.remote', then use that instead
        if (service.context().properties().has("pipgio.remote")) {
            remote = Boolean.parseBoolean(service.context().properties().get("pigpio.remote", remote.toString()));
        }

        // create an instance of PIGPIO (either remote or local?)
        if (remote) {
            // get the universal 'host' setting for Pi4J context
            if (service.context().properties().has("host")) {
                host = service.context().properties().get("host", host);
            }

            // if there is an overriding 'pigpio.host', then use that instead
            if (service.context().properties().has("pipgio.host")) {
                host = service.context().properties().get("pigpio.host", host);
            }

            // get the universal 'port' setting for Pi4J context
            if (service.context().properties().has("port")) {
                port = Integer.parseInt(service.context().properties().get("port", Integer.toString(port)));
            }

            // if there is an overriding 'pigpio.port', then use that instead
            if (service.context().properties().has("pipgio.port")) {
                port = Integer.parseInt(service.context().properties().get("pipgio.port", Integer.toString(port)));
            }

            // create remote socket connected instance of PIGPIO
            piGpio = PiGpio.newSocketInstance(host, port);
        } else {
            // create a local/native binding instance of PIGPIO
            piGpio = PiGpio.newNativeInstance();
        }

        // create new instances of the PIGPIO plugin I/O providers using the newly created PIGPIO lib reference
        Provider providers[] = {
            PiGpioDigitalInputProvider.newInstance(piGpio),
            PiGpioDigitalOutputProvider.newInstance(piGpio),
            PiGpioPwmProvider.newInstance(piGpio),
            PiGpioI2CProvider.newInstance(piGpio),
            PiGpioSpiProvider.newInstance(piGpio)
        };

        // register all PiGpio I/O Providers with the plugin service
        service.register(providers);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void shutdown(Context context) throws ShutdownException {
        // shutdown the PiGpio library
        if (piGpio != null && piGpio.isInitialized()) piGpio.shutdown();
    }
}

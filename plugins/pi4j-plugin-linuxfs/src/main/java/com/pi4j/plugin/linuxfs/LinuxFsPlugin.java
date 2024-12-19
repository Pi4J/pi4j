package com.pi4j.plugin.linuxfs;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: PLUGIN   :: LinuxFS I/O Providers
 * FILENAME      :  LinuxFsPlugin.java
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
import com.pi4j.extension.Plugin;
import com.pi4j.extension.PluginService;
import com.pi4j.plugin.linuxfs.internal.LinuxGpio;
import com.pi4j.plugin.linuxfs.provider.i2c.LinuxFsI2CProvider;
import com.pi4j.plugin.linuxfs.provider.gpio.digital.LinuxFsDigitalInputProvider;
import com.pi4j.plugin.linuxfs.provider.gpio.digital.LinuxFsDigitalOutputProvider;
import com.pi4j.plugin.linuxfs.provider.pwm.LinuxFsPwmProvider;
import com.pi4j.plugin.linuxfs.internal.LinuxPwm;
import com.pi4j.provider.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>LinuxFsPlugin class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public class LinuxFsPlugin implements Plugin {

    /**
     * Constant <code>NAME="LinuxFS"</code>
     */
    public static final String NAME = "LinuxFS";
    /**
     * Constant <code>ID="linuxfs"</code>
     */
    public static final String ID = "linuxfs";

//    // Analog Input (GPIO) Provider name and unique ID
//    public static final String ANALOG_INPUT_PROVIDER_NAME = NAME + " Analog Input (GPIO) Provider";
//    public static final String ANALOG_INPUT_PROVIDER_ID = ID + "-analog-input";

    // Analog Output (GPIO) Provider name and unique ID
    /**
     * Constant <code>ANALOG_OUTPUT_PROVIDER_NAME="NAME +  Analog Output (GPIO) Provider"</code>
     */
    public static final String ANALOG_OUTPUT_PROVIDER_NAME = NAME + " Analog Output (GPIO) Provider";
    /**
     * Constant <code>ANALOG_OUTPUT_PROVIDER_ID="ID + -analog-output"</code>
     */
    public static final String ANALOG_OUTPUT_PROVIDER_ID = ID + "-analog-output";

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
    public static final String PWM_PROVIDER_NAME = NAME + " PWM Provider";
    public static final String PWM_PROVIDER_ID = ID + "-pwm";

    // I2C Provider name and unique ID
    public static final String I2C_PROVIDER_NAME = NAME + " I2C Provider";
    public static final String I2C_PROVIDER_ID = ID + "-i2c";

//    // SPI Provider name and unique ID
//    public static final String SPI_PROVIDER_NAME = NAME + " SPI Provider";
//    public static final String SPI_PROVIDER_ID = ID + "-spi";
//
//    // Serial Provider name and unique ID
//    public static final String SERIAL_PROVIDER_NAME = NAME + " Serial Provider";
//    public static final String SERIAL_PROVIDER_ID = ID + "-serial";

    public static String DEFAULT_GPIO_FILESYSTEM_PATH = LinuxGpio.DEFAULT_SYSTEM_PATH;
    public static String DEFAULT_PWM_FILESYSTEM_PATH = LinuxPwm.DEFAULT_SYSTEM_PATH;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(PluginService service) {

        // get Linux file system path for GPIO & PWM
        String gpioFileSystemPath = DEFAULT_GPIO_FILESYSTEM_PATH;
        String pwmFileSystemPath = DEFAULT_PWM_FILESYSTEM_PATH;

        int pwmChip;
        if(BoardInfoHelper.usesRP1()) {
            pwmChip = LinuxPwm.DEFAULT_RP1_PWM_CHIP;
        }else{
            pwmChip = LinuxPwm.DEFAULT_LEGACY_PWM_CHIP;
        }

        // [GPIO] get overriding custom 'linux.gpio.system.path' setting from Pi4J context
        if(service.context().properties().has("linux.gpio.system.path")){
            gpioFileSystemPath = service.context().properties().get("linux.gpio.system.path", gpioFileSystemPath);
        }

        // [PWM] get overriding custom 'linux.gpio.system.path' setting from Pi4J context
        if(service.context().properties().has("linux.pwm.system.path")){
            pwmFileSystemPath = service.context().properties().get("linux.pwm.system.path", pwmFileSystemPath);
        }

        // [PWM] get overriding custom 'linux.gpio.system.path' setting from Pi4J context
        if(service.context().properties().has("linux.pwm.chip")){
            try {
                pwmChip = Integer.parseInt(service.context().properties().get("linux.pwm.chip", Integer.toString(pwmChip)));
            }
            catch (Exception e){
                logger.error(e.getMessage(), e);
            }
        }

        // create & define supported Linux file system I/O providers that will be exposed to Pi4J via this plugin
        Provider[] providers = {
            LinuxFsDigitalInputProvider.newInstance(gpioFileSystemPath),
            LinuxFsDigitalOutputProvider.newInstance(gpioFileSystemPath),
            LinuxFsPwmProvider.newInstance(pwmFileSystemPath, pwmChip),
            LinuxFsI2CProvider.newInstance()
            //TODO add 1-Wire provider
        };

        // register the LinuxFS I/O Providers with the plugin service
        service.register(providers);
    }
}

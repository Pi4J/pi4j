package com.pi4j.plugin.mock;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: PLUGIN   :: Mock Platform & Providers
 * FILENAME      :  Mock.java
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

/**
 * <p>Mock class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public class Mock {
    /** Constant <code>NAME="Mock"</code> */
    public static final String NAME = "Mock";
    /** Constant <code>ID="mock"</code> */
    public static final String ID = "mock";

    // Platform name and unique ID
    /** Constant <code>PLATFORM_NAME="NAME +  Platform"</code> */
    public static final String PLATFORM_NAME = NAME + " Platform";
    /** Constant <code>PLATFORM_ID="ID + -platform"</code> */
    public static final String PLATFORM_ID = ID + "-platform";
    /** Constant <code>PLATFORM_DESCRIPTION="Pi4J platform used for mock testing."</code> */
    public static final String PLATFORM_DESCRIPTION = "Pi4J platform used for mock testing.";

    // Analog Input (GPIO) Provider name and unique ID
    /** Constant <code>ANALOG_INPUT_PROVIDER_NAME="NAME +  Analog Input (GPIO) Provider"</code> */
    public static final String ANALOG_INPUT_PROVIDER_NAME = NAME + " Analog Input (GPIO) Provider";
    /** Constant <code>ANALOG_INPUT_PROVIDER_ID="ID + -analog-input"</code> */
    public static final String ANALOG_INPUT_PROVIDER_ID = ID + "-analog-input";

    // Analog Output (GPIO) Provider name and unique ID
    /** Constant <code>ANALOG_OUTPUT_PROVIDER_NAME="NAME +  Analog Output (GPIO) Provider"</code> */
    public static final String ANALOG_OUTPUT_PROVIDER_NAME = NAME + " Analog Output (GPIO) Provider";
    /** Constant <code>ANALOG_OUTPUT_PROVIDER_ID="ID + -analog-output"</code> */
    public static final String ANALOG_OUTPUT_PROVIDER_ID = ID + "-analog-output";

    // Digital Input (GPIO) Provider name and unique ID
    /** Constant <code>DIGITAL_INPUT_PROVIDER_NAME="NAME +   Digital Input (GPIO) Provider"</code> */
    public static final String DIGITAL_INPUT_PROVIDER_NAME = NAME +  " Digital Input (GPIO) Provider";
    /** Constant <code>DIGITAL_INPUT_PROVIDER_ID="ID + -digital-input"</code> */
    public static final String DIGITAL_INPUT_PROVIDER_ID = ID + "-digital-input";

    // Digital Output (GPIO) Provider name and unique ID
    /** Constant <code>DIGITAL_OUTPUT_PROVIDER_NAME="NAME +   Digital Output (GPIO) Provider"</code> */
    public static final String DIGITAL_OUTPUT_PROVIDER_NAME = NAME +  " Digital Output (GPIO) Provider";
    /** Constant <code>DIGITAL_OUTPUT_PROVIDER_ID="ID + -digital-output"</code> */
    public static final String DIGITAL_OUTPUT_PROVIDER_ID = ID + "-digital-output";

    // PWM Provider name and unique ID
    /** Constant <code>PWM_PROVIDER_NAME="NAME +  PWM Provider"</code> */
    public static final String PWM_PROVIDER_NAME = NAME + " PWM Provider";
    /** Constant <code>PWM_PROVIDER_ID="ID + -pwm"</code> */
    public static final String PWM_PROVIDER_ID = ID + "-pwm";

    // I2C Provider name and unique ID
    /** Constant <code>I2C_PROVIDER_NAME="NAME +  I2C Provider"</code> */
    public static final String I2C_PROVIDER_NAME = NAME + " I2C Provider";
    /** Constant <code>I2C_PROVIDER_ID="ID + -i2c"</code> */
    public static final String I2C_PROVIDER_ID = ID + "-i2c";

    // SPI Provider name and unique ID
    /** Constant <code>SPI_PROVIDER_NAME="NAME +  SPI Provider"</code> */
    public static final String SPI_PROVIDER_NAME = NAME + " SPI Provider";
    /** Constant <code>SPI_PROVIDER_ID="ID + -spi"</code> */
    public static final String SPI_PROVIDER_ID = ID + "-spi";

    // Serial Provider name and unique ID
    /** Constant <code>SERIAL_PROVIDER_NAME="NAME +  Serial Provider"</code> */
    public static final String SERIAL_PROVIDER_NAME = NAME + " Serial Provider";
    /** Constant <code>SERIAL_PROVIDER_ID="ID + -serial"</code> */
    public static final String SERIAL_PROVIDER_ID = ID + "-serial";

    // 1-Wire Provider name and unique ID
    /** Constant <code>ONE_WIRE_PROVIDER_NAME="NAME +  Serial Provider"</code> */
    public static final String ONE_WIRE_PROVIDER_NAME = NAME + " 1-Wire Provider";
    /** Constant <code>ONE_WIRE_PROVIDER_ID="ID + -serial"</code> */
    public static final String ONE_WIRE_PROVIDER_ID = ID + "-one-wire";
}

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
 * Constants identifying the Mock platform and the Mock I/O providers contributed by this plugin.
 * <p>
 * These names and unique IDs are used to register and look up the in-memory, hardware-free
 * implementations of the Pi4J I/O contracts (Digital I/O, PWM, I2C and SPI) so that Pi4J can run
 * without real GPIO/I2C/SPI/PWM hardware, for example in unit tests.
 *
 * @see MockPlugin
 */
public class Mock {
    /**
     * Human-readable display name shared by the Mock platform and providers.
     */
    public static final String NAME = "Mock";
    /**
     * Base identifier prefix used to build the unique IDs of the Mock platform and providers.
     */
    public static final String ID = "mock";

    // Platform name and unique ID
    /**
     * Display name of the Mock platform.
     */
    public static final String PLATFORM_NAME = NAME + " Platform";
    /**
     * Unique ID of the Mock platform, used when registering and resolving the platform.
     */
    public static final String PLATFORM_ID = ID + "-platform";
    /**
     * Human-readable description of the Mock platform.
     */
    public static final String PLATFORM_DESCRIPTION = "Pi4J platform used for mock testing.";

    // Digital Input (GPIO) Provider name and unique ID
    /**
     * Display name of the mock digital input (GPIO) provider.
     */
    public static final String DIGITAL_INPUT_PROVIDER_NAME = NAME + " Digital Input (GPIO) Provider";
    /**
     * Unique ID of the mock digital input (GPIO) provider, used when registering and resolving it.
     */
    public static final String DIGITAL_INPUT_PROVIDER_ID = ID + "-digital-input";

    // Digital Output (GPIO) Provider name and unique ID
    /**
     * Display name of the mock digital output (GPIO) provider.
     */
    public static final String DIGITAL_OUTPUT_PROVIDER_NAME = NAME + " Digital Output (GPIO) Provider";
    /**
     * Unique ID of the mock digital output (GPIO) provider, used when registering and resolving it.
     */
    public static final String DIGITAL_OUTPUT_PROVIDER_ID = ID + "-digital-output";

    // PWM Provider name and unique ID
    /**
     * Display name of the mock PWM provider.
     */
    public static final String PWM_PROVIDER_NAME = NAME + " PWM Provider";
    /**
     * Unique ID of the mock PWM provider, used when registering and resolving it.
     */
    public static final String PWM_PROVIDER_ID = ID + "-pwm";

    // I2C Provider name and unique ID
    /**
     * Display name of the mock I2C provider.
     */
    public static final String I2C_PROVIDER_NAME = NAME + " I2C Provider";
    /**
     * Unique ID of the mock I2C provider, used when registering and resolving it.
     */
    public static final String I2C_PROVIDER_ID = ID + "-i2c";

    // SPI Provider name and unique ID
    /**
     * Display name of the mock SPI provider.
     */
    public static final String SPI_PROVIDER_NAME = NAME + " SPI Provider";
    /**
     * Unique ID of the mock SPI provider, used when registering and resolving it.
     */
    public static final String SPI_PROVIDER_ID = ID + "-spi";
}

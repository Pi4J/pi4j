package com.pi4j.plugin.mock;

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

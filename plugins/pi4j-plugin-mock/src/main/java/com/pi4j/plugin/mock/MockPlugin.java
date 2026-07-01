package com.pi4j.plugin.mock;

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

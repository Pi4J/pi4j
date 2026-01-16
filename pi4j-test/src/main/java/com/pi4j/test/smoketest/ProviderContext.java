package com.pi4j.test.smoketest;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.plugin.ffm.providers.gpio.FFMDigitalInputProviderImpl;
import com.pi4j.plugin.ffm.providers.gpio.FFMDigitalOutputProviderImpl;
import com.pi4j.plugin.ffm.providers.i2c.FFMI2CProviderImpl;
import com.pi4j.plugin.ffm.providers.pwm.FFMPwmProviderImpl;
import com.pi4j.plugin.ffm.providers.spi.FFMSpiProviderImpl;
import com.pi4j.plugin.gpiod.provider.gpio.digital.GpioDDigitalInputProvider;
import com.pi4j.plugin.gpiod.provider.gpio.digital.GpioDDigitalOutputProvider;
import com.pi4j.plugin.linuxfs.provider.i2c.LinuxFsI2CProvider;
import com.pi4j.plugin.linuxfs.provider.pwm.LinuxFsPwmProvider;
import com.pi4j.plugin.linuxfs.provider.spi.LinuxFsSpiProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProviderContext {

    private static final Logger logger = LoggerFactory.getLogger(ProviderContext.class);

    public enum TestProvider {
        FFM,
        LINUXFS,
        UNDEFINED;

        public static TestProvider getByName(String name) {
            for (TestProvider provider : TestProvider.values()) {
                if (provider.name().equalsIgnoreCase(name)) {
                    return provider;
                }
            }
            return UNDEFINED;
        }
    }

    private final TestProvider testProvider;

    private Context pi4j = null;

    private static final String DEFAULT_PWM_FILESYSTEM_PATH = "/sys/class/pwm";

    /**
     *
     * @param testProvider Identifies which set of providers to create
     */
    public ProviderContext(TestProvider testProvider) {
        this.testProvider = testProvider;

        switch (testProvider) {
            case LINUXFS -> {
                pi4j = Pi4J.newContextBuilder().add(LinuxFsI2CProvider.newInstance())
                    .add(GpioDDigitalInputProvider.newInstance())
                    .add(GpioDDigitalOutputProvider.newInstance())
                    .add(LinuxFsPwmProvider.newInstance(DEFAULT_PWM_FILESYSTEM_PATH))
                    .add(LinuxFsI2CProvider.newInstance())
                    .add(LinuxFsSpiProvider.newInstance())
                    .build();
            }
            case FFM -> {
                pi4j = Pi4J.newContextBuilder()
                    .add(new FFMDigitalOutputProviderImpl())
                    .add(new FFMDigitalInputProviderImpl())
                    .add(new FFMI2CProviderImpl())
                    .add(new FFMSpiProviderImpl())
                    .add(new FFMPwmProviderImpl())
                    .build();
            }
            default -> logger.error("No test provider specified");
        }
    }

    public TestProvider getTestProvider() {
        return testProvider;
    }

    public Context getContext() {
        return pi4j;
    }
}
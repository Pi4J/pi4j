package com.pi4j.test.smoketest;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.plugin.ffm.providers.gpio.FFMDigitalInputProviderImpl;
import com.pi4j.plugin.ffm.providers.gpio.FFMDigitalOutputProviderImpl;
import com.pi4j.plugin.ffm.providers.i2c.FFMI2CProviderImpl;
import com.pi4j.plugin.ffm.providers.pwm.FFMPwmProviderImpl;
import com.pi4j.plugin.ffm.providers.spi.FFMSpiProviderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProviderContext {

    private static final Logger logger = LoggerFactory.getLogger(ProviderContext.class);

    public enum TestProvider {
        FFM,
        LINUXFS,
        NEWAUTOCONTEXT;

        public static TestProvider getByName(String name) {
            for (TestProvider provider : TestProvider.values()) {
                if (provider.name().equalsIgnoreCase(name)) {
                    return provider;
                }
            }

            logger.warn("No test provider found for name: {}, using {}", name, NEWAUTOCONTEXT.name());
            return NEWAUTOCONTEXT;
        }
    }

    private final TestProvider testProvider;

    private Context pi4j = null;

    /**
     *
     * @param testProvider Identifies which set of providers to create
     */
    public ProviderContext(TestProvider testProvider) {
        this.testProvider = testProvider;

        switch (testProvider) {
            case NEWAUTOCONTEXT -> pi4j = Pi4J.newAutoContext();
            case FFM -> pi4j = Pi4J
                .newContextBuilder()
                .add(new FFMDigitalOutputProviderImpl())
                .add(new FFMDigitalInputProviderImpl())
                .add(new FFMI2CProviderImpl())
                .add(new FFMSpiProviderImpl())
                .add(new FFMPwmProviderImpl())
                .build();
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
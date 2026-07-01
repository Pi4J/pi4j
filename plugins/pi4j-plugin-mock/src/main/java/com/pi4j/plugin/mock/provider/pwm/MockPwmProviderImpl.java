package com.pi4j.plugin.mock.provider.pwm;

import com.pi4j.io.pwm.Pwm;
import com.pi4j.io.pwm.PwmConfig;
import com.pi4j.io.pwm.PwmProviderBase;

/**
 * Default in-memory implementation of {@link MockPwmProvider}, extending {@link PwmProviderBase}.
 * It produces {@link MockPwm} instances that simulate PWM channels in memory rather than driving
 * real PWM hardware.
 */
public class MockPwmProviderImpl extends PwmProviderBase implements MockPwmProvider {

    /**
     * Creates the mock PWM provider, assigning its mock {@link #ID} and {@link #NAME}.
     */
    public MockPwmProviderImpl() {
        this.id = ID;
        this.name = NAME;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Returns a deliberately high priority so that, when the mock plugin is present on the
     * classpath, it is preferred over hardware providers during testing.
     */
    @Override
    public int getPriority() {
        // if the mock is loaded, then we most probably want to use it for testing
        return 1000;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Creates a new {@link MockPwm} instance that simulates the channel in memory and registers
     * it with the Pi4J context.
     */
    @Override
    public Pwm create(PwmConfig config) {
        MockPwm pwm = new MockPwm(this, config);
        this.context.register(pwm);
        return pwm;
    }
}

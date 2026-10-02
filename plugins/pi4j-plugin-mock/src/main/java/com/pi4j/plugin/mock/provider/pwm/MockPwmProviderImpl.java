package com.pi4j.plugin.mock.provider.pwm;

import com.pi4j.io.pwm.Pwm;
import com.pi4j.io.pwm.PwmConfig;
import com.pi4j.io.pwm.PwmProviderBase;
import com.pi4j.plugin.mock.Mock;

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
     * Returns Mock.MOCK_PROVIDER_PRIORITY.
     */
    @Override
    public int getPriority() {
        // if the mock is loaded, then we most probably want to use it for testing
        return Mock.MOCK_PROVIDER_PRIORITY;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Creates a new {@link MockPwm} instance that simulates the channel in memory.
     */
    @Override
    public Pwm create(PwmConfig config) {
        return new MockPwm(context, this, config);
    }
}

package com.pi4j.plugin.mock.provider.pwm;

import com.pi4j.io.pwm.PwmProvider;
import com.pi4j.plugin.mock.Mock;

/**
 * Mock, in-memory {@link PwmProvider} used for testing and for running Pi4J on machines
 * without real PWM hardware. It creates {@link MockPwm} instances that record the requested
 * PWM state in memory instead of driving a hardware PWM channel.
 */
public interface MockPwmProvider extends PwmProvider {
    /** The human-readable provider name, {@link Mock#PWM_PROVIDER_NAME}. */
    String NAME = Mock.PWM_PROVIDER_NAME;
    /** The unique provider identifier, {@link Mock#PWM_PROVIDER_ID}. */
    String ID = Mock.PWM_PROVIDER_ID;

    /**
     * Creates a new mock PWM provider instance.
     *
     * @return a new {@link MockPwmProvider} backed by an in-memory implementation
     */
    static MockPwmProvider newInstance() {
        return new MockPwmProviderImpl();
    }
}

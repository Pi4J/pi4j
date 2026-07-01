package com.pi4j.plugin.mock.provider.pwm;

import com.pi4j.io.exception.IOException;
import com.pi4j.io.pwm.Pwm;
import com.pi4j.io.pwm.PwmBase;
import com.pi4j.io.pwm.PwmConfig;
import com.pi4j.io.pwm.PwmProvider;


/**
 * Mock, in-memory implementation of the pi4j-core {@link Pwm} contract, extending {@link PwmBase}.
 * It tracks the requested on/off state and configured frequency and duty cycle purely in memory,
 * without driving any real PWM hardware, so PWM-based code can be exercised in unit tests.
 */
public class MockPwm extends PwmBase implements Pwm {

    /**
     * Creates a mock PWM instance for the given provider and configuration.
     *
     * @param provider the {@link PwmProvider} that created this instance
     * @param config   the {@link PwmConfig} describing the PWM channel, frequency and duty cycle
     */
    public MockPwm(PwmProvider provider, PwmConfig config){
        super(provider, config);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Simulates enabling the PWM signal by recording the on-state in memory; no hardware output
     * is produced.
     */
    @Override
    public Pwm on() throws IOException {
        this.onState = true;
        return this;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Simulates disabling the PWM signal by recording the off-state in memory; no hardware output
     * is produced.
     */
    @Override
    public Pwm off() throws IOException {
        this.onState = false;
        return this;
    }
}

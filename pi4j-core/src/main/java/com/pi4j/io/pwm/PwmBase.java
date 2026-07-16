package com.pi4j.io.pwm;

import com.pi4j.context.Context;
import com.pi4j.exception.InitializeException;
import com.pi4j.exception.ShutdownException;
import com.pi4j.io.IOBase;
import com.pi4j.io.exception.IOException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Abstract base implementation of the {@link Pwm} interface, providing the common
 * state and behaviour (frequency, duty-cycle, polarity, on/off state, presets and
 * initialize/shutdown handling) shared by all PWM provider implementations.
 * Concrete providers extend this class and supply the platform-specific logic for
 * actually driving the PWM hardware or software signal.
 */
public abstract class PwmBase extends IOBase<Pwm, PwmConfig, PwmProvider> implements Pwm {

    /** Staged frequency in hertz applied to the signal the next time it is turned on; defaults to 100 Hz. */
    protected double frequency = 100;
    /** Staged duty-cycle percentage (0-100) applied to the signal the next time it is turned on; defaults to 50%. */
    protected double dutyCycle = 50;
    /** Signal period in nanoseconds, derived from {@link #frequency}. */
    protected long period = Math.round(TimeUnit.NANOSECONDS.convert(1, TimeUnit.SECONDS) / frequency);
    /** Current on/off state of the PWM signal; {@code true} when enabled. */
    protected boolean onState = false;
    /** Configured signal polarity; defaults to {@link PwmPolarity#NORMAL}. */
    protected PwmPolarity polarity = PwmPolarity.NORMAL;

    /**
     * Creates a new PWM base instance and registers any presets defined in the
     * supplied configuration, keyed by their lower-cased, trimmed names.
     *
     * @param provider the PWM provider that created this instance
     * @param config   the configuration describing this PWM channel, including any initial presets
     */
    public PwmBase(PwmProvider provider, PwmConfig config) {
        super(provider, config);
    }

    @Override
    public double getDutyCycle() throws IOException {
        return this.dutyCycle;
    }

    @Override
    public double getFrequency() throws IOException {
        return this.frequency;
    }

    @Override
    public double getActualFrequency() throws IOException {
        return this.frequency;
    }

    @Override
    public void setDutyCycle(double dutyCycle) throws IOException {
        double dc = dutyCycle;

        // bounds check the duty-cycle value
        if (dc < 0) dc = 0;
        if (dc > 100) dc = 100;

        // update the duty-cycle member
        this.dutyCycle = dc;
    }

    @Override
    public void setFrequency(double frequency) throws IOException {
        this.frequency = frequency;
    }

    @Override
    public boolean isOn() {
        return this.onState;
    }

    @Override
    public Pwm initialize(Context context) throws InitializeException {

        // apply initial frequency value if configured
        if (this.config.frequency() != null) {
            this.frequency = config.frequency();
        }

        // apply initial duty-cycle value if configured
        if (config.dutyCycle() != null) {
            this.dutyCycle = config.dutyCycle();
        } else {
            this.dutyCycle = 50;  // default duty-cycle is 50% of total range
        }

        // apply initial polarity value if configured
        if (config.polarity() != null) {
            this.polarity = config.polarity();
        } else {
            this.polarity = PwmPolarity.NORMAL;
        }

        // apply an initial value if configured
        if (this.config.initialValue() != null) {
            try {
                if (this.config.initialValue() <= 0) {
                    if (this.isOn()) {
                        this.off();
                    }
                } else {
                    this.on(this.config.initialValue());
                }
            } catch (IOException e) {
                throw new InitializeException(e);
            }
        }

        return this;
    }

    @Override
    public Pwm shutdownInternal(Context context) throws ShutdownException {
        // apply a shutdown value if configured
        if (this.config.shutdownValue() != null) {
            try {
                if (this.config.shutdownValue() <= 0) {
                    this.off();
                } else {
                    this.on(this.config.shutdownValue());
                }
            } catch (IOException e) {
                throw new ShutdownException(e);
            }
        }
        return this;
    }
}

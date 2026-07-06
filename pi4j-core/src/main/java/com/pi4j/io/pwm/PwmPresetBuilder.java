package com.pi4j.io.pwm;

import com.pi4j.config.Builder;
import com.pi4j.io.pwm.impl.DefaultPwmPresetBuilder;

/**
 * Fluent builder for creating a named {@link PwmPreset} with a specific frequency
 * and duty-cycle. The completed preset is produced via the inherited
 * {@link Builder#build()} method.
 */
public interface PwmPresetBuilder extends Builder<PwmPreset> {
    /**
     * Creates a new PWM preset builder for the given preset name.
     *
     * @param name the unique name to assign to the preset being built
     * @return a new PWM preset builder instance
     */
    static PwmPresetBuilder newInstance(String name)  {
        return DefaultPwmPresetBuilder.newInstance(name);
    }

    /**
     *  Set the duty-cycle value as a decimal value that represents the
     *  percentage of the ON vs OFF time of the PWM signal for each
     *  period.  The duty-cycle range is valid from 0 to 100 including
     *  factional values.  (Values above 50% mean the signal will
     *  remain HIGH more time than LOW.)
     * <p>
     *  Example: A value of 50 represents a duty-cycle where half of
     *  the time period the signal is LOW and the other half is HIGH.
     *
     * @param dutyCycle duty-cycle value expressed as a percentage (rage: 0-100)
     * @return this builder instance
     */
    PwmPresetBuilder dutyCycle(Integer dutyCycle);

    /**
     *  Set the configured frequency value in Hertz (number of cycles per second)
     *  that the PWM signal generator should attempt to output when this preset
     *  is applied to a PWM instance.
     * <p>
     *  Please note that certain PWM signal generators may be limited to specific
     *  frequency bands and may not generate all possible explicit frequency values.
     *  After enabling the PWM signal using the 'on(...)' method, you can check the
     *  'Pwm::frequency()' or 'Pwm::getFrequency()' properties to determine what
     *  frequency the PWM generator actually applied.
     *
     * @param frequency the number of cycles per second (Hertz)
     * @return this builder instance
     */
    PwmPresetBuilder frequency(Integer frequency);
}

package com.pi4j.io.pwm;

import com.pi4j.config.ConfigBuilder;
import com.pi4j.context.Context;
import com.pi4j.io.IOConfigBuilder;
import com.pi4j.io.pwm.impl.DefaultPwmConfigBuilder;

/**
 * Fluent builder for assembling a {@link PwmConfig}. It exposes chained setters for
 * the addressing (chip, channel, BCM pin), {@link PwmType}, {@link PwmPolarity},
 * frequency, duty-cycle, and initial/shutdown values, and produces an immutable configuration via its {@code build()}
 * method.
 */
public interface PwmConfigBuilder extends
    IOConfigBuilder<PwmConfigBuilder, PwmConfig>,
    ConfigBuilder<PwmConfigBuilder, PwmConfig> {
    /**
     * Creates a new PWM configuration builder instance.
     *
     * @param context the Pi4J context the configuration will be bound to
     * @return a new, empty PWM configuration builder
     */
    static PwmConfigBuilder newInstance(Context context) {
        return DefaultPwmConfigBuilder.newInstance();
    }

    /**
     * Sets the chip number for the PWM configuration.
     *
     * @param chip the chip number to be configured, used for hardware PWM signals,
     *             to identify the specific hardware interface or connection point.
     * @return the current PwmConfigBuilder instance for method chaining
     */
    PwmConfigBuilder chip(Integer chip);

    /**
     * Sets the channel number for the PWM configuration.
     *
     * @param channel the channel number to be configured, used for hardware PWM signals,
     *                to specify a particular hardware channel or output line
     *                for the PWM signal.
     * @return the current PwmConfigBuilder instance for method chaining
     */
    PwmConfigBuilder channel(Integer channel);

    /**
     * Sets the bcm number for the PWM configuration.
     *
     * @param bcm the bcm number to be configured, used for software PWM signals.
     * @return the current PwmConfigBuilder instance for method chaining
     */
    PwmConfigBuilder bcm(Integer bcm);

    /**
     * Set the configured frequency value in Hertz (number of cycles per second)
     * that the PWM signal generator should attempt to output when the PWM state
     * is enabled.
     * <p>
     * Please note that certain PWM signal generators may be limited to specific
     * frequency bands and may not generate all possible explicit frequency values.
     * After enabling the PWM signal using the 'on(...)' method, you can check the
     * 'Pwm::frequency()' or 'Pwm::getFrequency()' properties to determine what
     * frequency the PWM generator actually applied.
     *
     * @param frequency the number of cycles per second (Hertz)
     * @return this builder instance
     */
    PwmConfigBuilder frequency(Double frequency);

    /**
     * Sets the configured frequency from an integer hertz value. Provided for
     * backward compatibility with Pi4J 4.x; delegates to {@link #frequency(Double)}.
     *
     * @param frequency the number of cycles per second (Hertz), or {@code null} to leave unset
     * @return this builder instance for method chaining
     */
    default PwmConfigBuilder frequency(Integer frequency) {
        return frequency(frequency == null ? null : frequency.doubleValue());
    }

    /**
     * Set the duty-cycle value as a decimal value that represents the
     * percentage of the ON vs OFF time of the PWM signal for each
     * period.  The duty-cycle range is valid from 0 to 100 including
     * factional values.  (Values above 50% mean the signal will
     * remain HIGH more time than LOW.)
     * <p>
     * Example: A value of 50 represents a duty-cycle where half of
     * the time period the signal is LOW and the other half is HIGH.
     *
     * @param dutyCycle duty-cycle value expressed as a percentage (rage: 0-100)
     * @return this builder instance
     */
    PwmConfigBuilder dutyCycle(Double dutyCycle);

    /**
     * Sets the duty-cycle from an integer percentage value. Provided for backward
     * compatibility with Pi4J 4.x; delegates to {@link #dutyCycle(Double)}.
     *
     * @param dutyCycle duty-cycle value expressed as a percentage (range: 0-100), or {@code null} to leave unset
     * @return this builder instance for method chaining
     */
    default PwmConfigBuilder dutyCycle(Integer dutyCycle) {
        return dutyCycle(dutyCycle == null ? null : (double) dutyCycle);
    }

    /**
     * Set the {@link PwmType} of this PWM instance (hardware or software).
     * Please note that not all PWM providers support both hardware and software
     * PWM generators.  Please consult the documentation for your PWM provider
     * to determine what support is available and what limitations may apply.
     *
     * @param pwmType the PWM generator type to use ({@link PwmType#HARDWARE} or {@link PwmType#SOFTWARE})
     * @return this builder instance for method chaining
     */
    PwmConfigBuilder pwmType(PwmType pwmType);

    /**
     * Set the {@link PwmPolarity} of this PWM instance (normal or inversed).
     * Please note that not all PWM providers support polarity.  Please
     * consult the documentation for your PWM provider to determine
     * what support is available and what limitations may apply.
     *
     * @param polarity the signal polarity to apply ({@link PwmPolarity#NORMAL} or {@link PwmPolarity#INVERSED})
     * @return this builder instance for method chaining
     */
    PwmConfigBuilder polarity(PwmPolarity polarity);

    /**
     * Optionally configure a PWM duty-cycle value that should automatically
     * be applied to the PWM instance when the Pi4J context is shutdown.
     * This option can be helpful if you wish to do something like stop a PWM
     * signal (by configuring this 'shutdown' value to zero) when your application
     * is terminated an Pi4J is shutdown.
     *
     * @param dutyCycle duty-cycle value expressed as a percentage (rage: 0-100)
     * @return this builder instance
     */
    PwmConfigBuilder shutdown(Double dutyCycle);

    /**
     * Configures the shutdown duty-cycle from an integer percentage value;
     * delegates to {@link #shutdown(Double)}.
     *
     * @param dutyCycle duty-cycle value expressed as a percentage (range: 0-100), or {@code null} to leave unset
     * @return this builder instance for method chaining
     */
    default PwmConfigBuilder shutdown(Integer dutyCycle) {
        return shutdown(dutyCycle == null ? null : (double) dutyCycle);
    };
    /**
     * Optionally configure a PWM duty-cycle value that should automatically
     * be applied to the PWM instance when this PWM instance is created and initialized.
     * This option can be helpful if you wish to do something like set a default PWM
     * signal (by configuring this 'initial' value to 50%) when your application
     * creates the PWM instance.  This just helps eliminate a second line of code
     * to manually start the PWM signal for cases where you prefer it is auto-started.
     *
     * @param dutyCycle duty-cycle value expressed as a percentage (rage: 0-100)
     * @return this builder instance
     */
    PwmConfigBuilder initial(Double dutyCycle);

    /**
     * Configures the initial duty-cycle from an integer percentage value;
     * delegates to {@link #initial(Double)}.
     *
     * @param dutyCycle duty-cycle value expressed as a percentage (range: 0-100), or {@code null} to leave unset
     * @return this builder instance for method chaining
     */
    default PwmConfigBuilder initial(Integer dutyCycle) {
        return initial(dutyCycle == null ? null : (double) dutyCycle);
    }
}

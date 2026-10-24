package com.pi4j.io.pwm;

import com.pi4j.config.Config;
import com.pi4j.context.Context;
import com.pi4j.io.IOConfig;
import com.pi4j.io.impl.IOConfigBuilderBase;
import com.pi4j.util.StringUtil;

/**
 * Fluent builder for assembling a {@link PwmConfig}. It exposes chained setters for
 * the addressing (chip, channel, BCM pin), {@link PwmType}, {@link PwmPolarity},
 * frequency, duty-cycle, and initial/shutdown values, and produces an immutable configuration via its {@code build()}
 * method.
 */
public class PwmConfigBuilder extends IOConfigBuilderBase<PwmConfigBuilder, PwmConfig> {
    private Integer chip = null;
    private Integer channel = null;
    private Integer bcm = null;
    private Double frequency = null;
    private Double dutyCycle = null;
    private PwmType pwmType = PwmType.HARDWARE;
    private PwmPolarity polarity = PwmPolarity.NORMAL;
    private Double shutdownValue = null;
    private Double initialValue = null;

    /**
     * Default constructor for creating a new PWM configuration builder.
     */
    public PwmConfigBuilder() {
    }

    /**
     * Creates a new PWM configuration builder instance.
     *
     * @param context the Pi4J context the configuration will be bound to
     * @return a new, empty PWM configuration builder
     * @deprecated Please use Pwm.newConfigBuilder() instead.
     */
    @Deprecated(since = "5.0")
    public static PwmConfigBuilder newInstance(Context context) {
        return new PwmConfigBuilder();
    }

    /**
     * Internal method to create a new PWM configuration builder instance; please use Pwm.newConfigBuilder() instead.
     *
     * @return a new, empty PWM configuration builder
     * @deprecated Please use Pwm.newConfigBuilder() instead.
     */
    public static PwmConfigBuilder newInstance() {
        return new PwmConfigBuilder();
    }

    /**
     * Sets the chip number for the PWM configuration.
     *
     * @param chip the chip number to be configured, used for hardware PWM signals,
     *             to identify the specific hardware interface or connection point.
     * @return the current PwmConfigBuilder instance for method chaining
     */
    public PwmConfigBuilder chip(Integer chip) {
        this.chip = chip;
        return this;
    }

    /**
     * Sets the channel number for the PWM configuration.
     *
     * @param channel the channel number to be configured, used for hardware PWM signals,
     *                to specify a particular hardware channel or output line
     *                for the PWM signal.
     * @return the current PwmConfigBuilder instance for method chaining
     */
    public PwmConfigBuilder channel(Integer channel) {
        this.channel = channel;
        return this;
    }

    /**
     * Sets the BCM number for the PWM configuration.
     *
     * @param bcm the BCM number to be configured, used for software PWM signals.
     * @return the current PwmConfigBuilder instance for method chaining
     */
    public PwmConfigBuilder bcm(Integer bcm) {
        this.bcm = bcm;
        return this;
    }

    /**
     * Sets the legacy address value, delegating to {@link #channel(Integer)}.
     *
     * @param address the legacy address
     * @return this builder instance
     * @deprecated use {@link #channel(Integer)} instead.
     */
    @Deprecated(forRemoval = true)
    public PwmConfigBuilder address(Integer address) {
        return channel(address);
    }

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
     * @param frequency the number of cycles per second (Hertz)
     * @return this builder instance
     */
    public PwmConfigBuilder frequency(Double frequency) {
        this.frequency = frequency;
        return this;
    }

    /**
     * Sets the configured frequency from an integer hertz value. Provided for
     * backward compatibility with Pi4J 4.x; delegates to {@link #frequency(Double)}.
     *
     * @param frequency the number of cycles per second (Hertz), or {@code null} to leave unset
     * @return this builder instance for method chaining
     */
    public PwmConfigBuilder frequency(Integer frequency) {
        return frequency(frequency == null ? null : frequency.doubleValue());
    }

    /**
     * period.  The duty-cycle range is valid from 0 to 100 including
     * factional values.  (Values above 50% mean the signal will
     * remain HIGH more time than LOW.)
     * <p>
     * Example: A value of 50 represents a duty-cycle where half of
     * the time period the signal is LOW and the other half is HIGH.
     * <p>
     * Note that setting this value does not turn PWM on at construction time. For this purpose,
     * use initial() instead.
     * <p>
     * @param dutyCycle duty-cycle value expressed as a percentage (range: 0-100)
     * @return this builder instance
     */
    public PwmConfigBuilder dutyCycle(Double dutyCycle) {
        if (dutyCycle == null) {
            this.dutyCycle = null;
        } else {
            double dc = dutyCycle;
            if (dc < 0) {
                dc = 0;
            }
            if (dc > 100) {
                dc = 100;
            }
            this.dutyCycle = dc;
        }
        return this;
    }

    /**
     * Sets the duty-cycle from an integer percentage value. Provided for backward
     * compatibility with Pi4J 4.x; delegates to {@link #dutyCycle(Double)}.
     * <p>
     * Note that setting this value does not turn PWM on at construction time. For this purpose,
     * use inital() instead.
     * <p>
     * @param dutyCycle duty-cycle value expressed as a percentage (range: 0-100), or {@code null} to leave unset
     * @return this builder instance for method chaining
     */
    public PwmConfigBuilder dutyCycle(Integer dutyCycle) {
        return dutyCycle(dutyCycle == null ? null : (double) dutyCycle);
    }

    /**
     * Set the {@link PwmType} of this PWM instance (hardware or software).
     * <p>
     * Please note that not all PWM providers support both hardware and software
     * PWM generators.  Please consult the documentation for your PWM provider
     * to determine what support is available and what limitations may apply.
     *
     * @param pwmType the PWM generator type to use ({@link PwmType#HARDWARE} or {@link PwmType#SOFTWARE})
     * @return this builder instance for method chaining
     */
    public PwmConfigBuilder pwmType(PwmType pwmType) {
        this.pwmType = pwmType;
        return this;
    }

    /**
     * Set the {@link PwmPolarity} of this PWM instance (normal or inversed).
     *
     * @param polarity the signal polarity to apply ({@link PwmPolarity#NORMAL} or {@link PwmPolarity#INVERSED})
     * @return this builder instance for method chaining
     */
    public PwmConfigBuilder polarity(PwmPolarity polarity) {
        this.polarity = polarity;
        return this;
    }

    /**
     * Optionally configure a PWM duty-cycle value that should automatically
     * be applied to the PWM instance when the Pi4J context is shutdown.
     * This option can be helpful if you wish to do something like stop a PWM
     * signal (by configuring this 'shutdown' value to zero) when your application
     * is terminated an Pi4J is shutdown.
     *
     * @param dutyCycle duty-cycle value expressed as a percentage (range: 0-100)
     * @return this builder instance
     */
    public PwmConfigBuilder shutdown(Double dutyCycle) {
        if (dutyCycle == null) {
            this.shutdownValue = null;
        } else {
            double dc = dutyCycle;
            if (dc < 0) {
                dc = 0.0;
            }
            if (dc > 100) {
                dc = 100.0;
            }
            this.shutdownValue = dc;
        }
        return this;
    }

    /**
     * Configures the shutdown duty-cycle from an integer percentage value;
     * delegates to {@link #shutdown(Double)}.
     *
     * @param dutyCycle duty-cycle value expressed as a percentage (range: 0-100), or {@code null} to leave unset
     * @return this builder instance for method chaining
     */
    public PwmConfigBuilder shutdown(Integer dutyCycle) {
        return shutdown(dutyCycle == null ? null : (double) dutyCycle);
    }

    /**
     * Optionally configure a PWM duty-cycle value that should automatically
     * be applied to the PWM instance when this PWM instance is created and initialized.
     *
     * This option can be helpful if you wish to do something like set a default PWM
     * signal (by configuring this 'initial' value to 50%) when your application
     * creates the PWM instance.  This just helps eliminate a second line of code
     * to manually start the PWM signal for cases where you prefer it is auto-started.
     * <p>
     * Note that this method (in contrast to dutyCycle()) will turn on PWM at construction
     * time if the value is greater than 0. If both are set, this value will win.
     *
     * @param dutyCycle duty-cycle value expressed as a percentage (range: 0-100)
     * @return this builder instance
     */
    public PwmConfigBuilder initial(Double dutyCycle) {
        if (dutyCycle == null) {
            this.initialValue = null;
        } else {
            double dc = dutyCycle;
            if (dc < 0) {
                dc = 0.0;
            }
            if (dc > 100) {
                dc = 100.0;
            }
            this.initialValue = dc;
        }
        return this;
    }

    /**
     * Configures the initial duty-cycle from an integer percentage value;
     * delegates to {@link #initial(Double)}.
     *
     * @param dutyCycle duty-cycle value expressed as a percentage (range: 0-100), or {@code null} to leave unset
     * @return this builder instance for method chaining
     */
    public PwmConfigBuilder initial(Integer dutyCycle) {
        return initial(dutyCycle == null ? null : (double) dutyCycle);
    }

    /**
     * Construct the {@link PwmConfig} instance.
     *
     * @return the constructed {@link PwmConfig} instance
     */
    @Override
    public PwmConfig build() {
        String defaultId = channel != null ? "PWM-" + channel : (bcm != null ? "PWM-" + bcm : "PWM");
        String id = StringUtil.setIfNullOrEmpty(this.id(), defaultId, true);
        String name = StringUtil.setIfNullOrEmpty(this.properties.get(Config.NAME_KEY), id, true);
        String description = StringUtil.setIfNullOrEmpty(this.properties.get(Config.DESCRIPTION_KEY), id, true);

        return new PwmConfig(
            id,
            name,
            description,
            this.properties.get(IOConfig.PROVIDER_KEY),
            this.chip,
            this.channel,
            this.bcm,
            this.pwmType,
            this.polarity,
            this.frequency,
            this.dutyCycle,
            this.initialValue,
            this.shutdownValue
        );
    }
}

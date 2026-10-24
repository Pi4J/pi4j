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
     *
     * @param frequency the number of cycles per second (Hertz)
     * @return this builder instance
     */
    public PwmConfigBuilder frequency(Double frequency) {
        this.frequency = frequency;
        return this;
    }

    /**
     * Sets the configured frequency from an integer hertz value.
     *
     * @param frequency the number of cycles per second (Hertz), or {@code null} to leave unset
     * @return this builder instance for method chaining
     */
    public PwmConfigBuilder frequency(Integer frequency) {
        return frequency(frequency == null ? null : frequency.doubleValue());
    }

    /**
     * Set the duty-cycle value as a decimal value that represents the
     * percentage of the ON vs OFF time of the PWM signal for each
     * period. The duty-cycle range is valid from 0 to 100 including
     * fractional values.
     *
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
     * Sets the duty-cycle from an integer percentage value.
     *
     * @param dutyCycle duty-cycle value expressed as a percentage (range: 0-100), or {@code null} to leave unset
     * @return this builder instance for method chaining
     */
    public PwmConfigBuilder dutyCycle(Integer dutyCycle) {
        return dutyCycle(dutyCycle == null ? null : (double) dutyCycle);
    }

    /**
     * Set the {@link PwmType} of this PWM instance (hardware or software).
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

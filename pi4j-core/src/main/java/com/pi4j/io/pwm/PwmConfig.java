package com.pi4j.io.pwm;

import com.pi4j.config.BcmConfig;
import com.pi4j.config.ChannelConfig;
import com.pi4j.config.ChipConfig;
import com.pi4j.io.IOConfig;

import java.util.Collection;

/**
 * Immutable configuration for a {@link Pwm} instance. It carries the addressing
 * (chip, channel and/or BCM pin), the generator {@link PwmType}, the
 * {@link PwmPolarity}, the initial frequency and duty-cycle, optional initial and
 * shutdown values, and any {@link PwmPreset} definitions. Instances are assembled
 * with a {@link PwmConfigBuilder} and consumed by a {@link PwmProvider} when
 * creating the PWM I/O.
 */
public interface PwmConfig extends ChipConfig<PwmConfig>, ChannelConfig<PwmConfig>, BcmConfig<PwmConfig>, IOConfig<PwmConfig> {

    /**
     * Property key for the legacy PWM address.
     *
     * @deprecated use {@link #channel()} instead.
     */
    @Deprecated(forRemoval = true)
    String PWM_ADDRESS = "address";
    /** Property key for the PWM chip number (hardware PWM). */
    String PWM_CHIP = "chip";
    /** Property key for the PWM channel number (hardware PWM). */
    String PWM_CHANNEL = "channel";
    /** Property key for the BCM GPIO pin number (software PWM). */
    String PWM_BCM = "bcm";
    /** Property key for the PWM generator type (hardware/software). */
    String PWM_TYPE_KEY = "pwm-type";
    /** Property key for the PWM signal polarity (normal/inversed). */
    String POLARITY_KEY = "polarity";
    /** Property key for the configured frequency in hertz. */
    String FREQUENCY_KEY = "frequency";
    /** Property key for the configured duty-cycle percentage. */
    String DUTY_CYCLE_KEY = "duty-cycle";
    /** Property key for the duty-cycle applied when the Pi4J context is shut down. */
    String SHUTDOWN_VALUE_KEY = "shutdown";
    /** Property key for the duty-cycle applied when the PWM instance is initialized. */
    String INITIAL_VALUE_KEY = "initial";

    /**
     * Returns the legacy PWM address value.
     *
     * @return the configured address, or {@code null} if not set
     * @deprecated use {@link #channel()} instead.
     */
    @Deprecated(forRemoval = true)
    Integer address();

    /**
     * Returns a unique identifier for this PWM device, derived by combining the
     * chip, channel and BCM pin values so that distinct PWM channels do not collide.
     *
     * @return a unique integer identifier for this PWM device
     */
    @Override
    default int getUniqueIdentifier() {
        return (chip() == null ? 0 : (chip() << 16))
            + (channel() == null ? 0 : (channel() << 8))
            + (bcm() == null ? 0 : bcm());
    }

    /**
     * Get the configured duty-cycle value as a decimal value that represents
     * the percentage of the ON vs OFF time of the PWM signal for each period.
     * The duty-cycle range is valid from 0 to 100 including factional values.
     * (Values above 50% mean the signal will remain HIGH more time than LOW.)
     * <p>
     * Example: A value of 50 represents a duty-cycle where half of
     * the time period the signal is LOW and the other half is HIGH.
     *
     * @return duty-cycle value expressed as a percentage (rage: 0-100)
     */
    Double dutyCycle();

    /**
     * Get the configured duty-cycle value as a decimal value that represents
     * the percentage of the ON vs OFF time of the PWM signal for each period.
     * The duty-cycle range is valid from 0 to 100 including factional values.
     * (Values above 50% mean the signal will remain HIGH more time than LOW.)
     * <p>
     * Example: A value of 50 represents a duty-cycle where half of
     * the time period the signal is LOW and the other half is HIGH.
     *
     * @return duty-cycle value expressed as a percentage (rage: 0-100)
     */
    default Double getDutyCycle() {
        return dutyCycle();
    }

    /**
     * Get the configured frequency value in Hertz (number of cycles per second)
     * that the PWM signal generator should attempt to output when the PWM state is
     * enabled.
     *
     * @return frequency value in Hz (number of cycles per second)
     */
    Double frequency();

    /**
     * Get the configured frequency value in Hertz (number of cycles per second)
     * that the PWM signal generator should attempt to output when the PWM state is
     * enabled.
     *
     * @return frequency value in Hz (number of cycles per second)
     */
    default Double getFrequency() {
        return frequency();
    }

    /**
     * Get the configured PwmType of this PWM instance. (Hardware/Software)
     * Please note that not all PWM providers support both hardware and software
     * PWM generators.  Please consult the documentation for your PWM provider
     * to determine what support is available and what limitations may apply.
     *
     * @return the PwmType for this PWM instance
     */
    PwmType pwmType();

    /**
     * Get the configured PwmType of this PWM instance. (Hardware/Software)
     * Please note that not all PWM providers support both hardware and software
     * PWM generators.  Please consult the documentation for your PWM provider
     * to determine what support is available and what limitations may apply.
     *
     * @return the PwmType for this PWM instance
     */
    default PwmType getPwmType() {
        return pwmType();
    }

    /**
     * Get the configured polarity of this PWM instance. (Normal/Inversed)
     * Please note that not all PWM providers support polarity. Please
     * consult the documentation for your PWM provider to determine what
     * support is available and what limitations may apply.
     *
     * @return the PwmPolarity for this PWM instance
     */
    PwmPolarity polarity();

    /**
     * Get the configured polarity of this PWM instance. (Normal/Inversed)
     * Please note that not all PWM providers support polarity. Please
     * consult the documentation for your PWM provider to determine what
     * support is available and what limitations may apply.
     *
     * @return the PwmPolarity for this PWM instance
     */
    default PwmPolarity getPolarity() {
        return polarity();
    }

    /**
     * Get configured PWM duty-cycle value that is automatically applied
     * to the PWM instance when the Pi4J context is shutdown.
     * <p>
     * This option can be helpful if you wish to do something like stop a PWM
     * signal (by configuring this 'shutdown' value to zero) when your application
     * is terminated an Pi4J is shutdown.
     *
     * @return optional duty-cycle value expressed as a percentage (rage: 0-100)
     * that is applied when shutting down the Pi4J context.
     */
    Double shutdownValue();

    /**
     * Get configured PWM duty-cycle value that is automatically applied
     * to the PWM instance when the Pi4J context is shutdown.
     * <p>
     * This option can be helpful if you wish to do something like stop a PWM
     * signal (by configuring this 'shutdown' value to zero) when your application
     * is terminated an Pi4J is shutdown.
     *
     * @return duty-cycle value expressed as a percentage (rage: 0-100)
     * that is applied when shutting down the Pi4J context.
     */
    default Double getShutdownValue() {
        return shutdownValue();
    }

    /**
     * Optionally configure a PWM duty-cycle value that should automatically
     * be applied to the PWM instance when the Pi4J context is shutdown.
     * This option can be helpful if you wish to do something like stop a PWM
     * signal (by configuring this 'shutdown' value to zero) when your application
     * is terminated an Pi4J is shutdown.
     *
     * @param dutyCycle duty-cycle value expressed as a percentage (rage: 0-100)
     * @return this PwmConfig instance
     */
    PwmConfig shutdownValue(Double dutyCycle);

    /**
     * Optionally configure a PWM duty-cycle value that should automatically
     * be applied to the PWM instance when the Pi4J context is shutdown.
     * This option can be helpful if you wish to do something like stop a PWM
     * signal (by configuring this 'shutdown' value to zero) when your application
     * is terminated an Pi4J is shutdown.
     *
     * @param dutyCycle duty-cycle value expressed as a percentage (rage: 0-100)
     */
    default void setShutdownValue(Double dutyCycle) {
        this.shutdownValue(dutyCycle);
    }

    /**
     * Get configured PWM duty-cycle value that is automatically applied to
     * the PWM instance when this PWM instance is created and initialized.
     * <p>
     * This option can be helpful if you wish to do something like set a default PWM
     * signal (by configuring this 'initial' value to 50%) when your application
     * creates the PWM instance.  This just helps eliminate a second line of code
     * to manually start the PWM signal for cases where you prefer it is auto-started.
     *
     * @return duty-cycle value expressed as a percentage (rage: 0-100)
     * that is applied when creating and initializing the PWM instance.
     */
    Double initialValue();

    /**
     * Get configured PWM duty-cycle value that is automatically applied to
     * the PWM instance when this PWM instance is created and initialized.
     * <p>
     * This option can be helpful if you wish to do something like set a default PWM
     * signal (by configuring this 'initial' value to 50%) when your application
     * creates the PWM instance.  This just helps eliminate a second line of code
     * to manually start the PWM signal for cases where you prefer it is auto-started.
     *
     * @return duty-cycle value expressed as a percentage (rage: 0-100)
     * that is applied when creating and initializing the PWM instance.
     */
    default Double getInitialValue() {
        return initialValue();
    }

    /**
     * Get the configured PwmPresets assigned to this PWM instance.
     *
     * @return collection of PwmPresets
     */
    Collection<PwmPreset> presets();

    /**
     * Get the configured PwmPresets assigned to this PWM instance.
     *
     * @return collection of PwmPresets
     */
    default Collection<PwmPreset> getPresets() {
        return presets();
    }
}

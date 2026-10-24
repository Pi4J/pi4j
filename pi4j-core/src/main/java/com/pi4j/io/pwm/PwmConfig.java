package com.pi4j.io.pwm;

import com.pi4j.io.IOConfig;

/**
 * Immutable configuration for a {@link Pwm} instance. It carries the addressing
 * (chip, channel and/or BCM pin), the generator {@link PwmType}, the
 * {@link PwmPolarity}, the initial frequency and duty-cycle, optional initial and
 * shutdown values. Instances are assembled with a {@link PwmConfigBuilder} and consumed by the {@link com.pi4j.context.Context} when
 * creating the PWM I/O.
 *
 * @param id the configuration identifier
 * @param name the configuration name
 * @param description the configuration description
 * @param chip the PWM chip number (hardware PWM)
 * @param channel the PWM channel number (hardware PWM)
 * @param bcm the BCM GPIO pin number (software PWM)
 * @param pwmType the generator type (hardware/software)
 * @param polarity the signal polarity (normal/inversed)
 * @param frequency the configured frequency in hertz
 * @param dutyCycle the configured duty-cycle percentage (0-100)
 * @param initialValue optional initial duty-cycle applied when initialized
 * @param shutdownValue optional duty-cycle applied when shut down
 */
public record PwmConfig(
    @Override String id,
    @Override String name,
    @Override String description,
    Integer chip,
    Integer channel,
    Integer bcm,
    PwmType pwmType,
    PwmPolarity polarity,
    Double frequency,
    Double dutyCycle,
    Double initialValue,
    Double shutdownValue
) implements IOConfig {

    /**
     * Returns the legacy PWM address value.
     *
     * @return the configured address, or {@code null} if not set
     * @deprecated use {@link #channel()} instead.
     */
    @Deprecated(forRemoval = true)
    public Integer address() {
        return channel;
    }

    /**
     * Returns the legacy PWM address value.
     *
     * @return the configured address, or {@code null} if not set
     * @deprecated use {@link #channel()} instead.
     */
    @Deprecated(forRemoval = true)
    public Integer getAddress() {
        return address();
    }

    /**
     * Returns a unique identifier for this PWM device, derived by combining the
     * chip, channel and BCM pin values so that distinct PWM channels do not collide.
     *
     * @return a unique integer identifier for this PWM device
     */
    @Override
    public int getUniqueIdentifier() {
        return (chip == null ? 0 : (chip << 16))
            + (channel == null ? 0 : (channel << 8))
            + (bcm == null ? 0 : bcm);
    }

    @Override
    public void validate() {
        if (id == null) {
            throw new IllegalStateException("ID cannot be null");
        }
    }

    /**
     * Get the configured duty-cycle value as a decimal value that represents
     * the percentage of the ON vs OFF time of the PWM signal for each period.
     * The duty-cycle range is valid from 0 to 100 including factional values.
     * (Values above 50% mean the signal will remain HIGH more time than LOW.)
     * <p>
     * Example: A value of 50 represents a duty-cycle where half of
     * the time period the signal is LOW and the other half is HIGH.
     * <p>
     * Note that (in contrast to initialValue), setting this will not turn the PWM on
     * implicitly
     * <p>
     *
     * @return duty-cycle value expressed as a percentage (rage: 0-100)
     */
    public Double getDutyCycle() {
        return dutyCycle;
    }

    /**
     * Get the configured frequency value in Hertz (number of cycles per second)
     * that the PWM signal generator should attempt to output when the PWM state is
     * enabled.
     *
     * @return frequency value in Hz (number of cycles per second)
     */
    public Double getFrequency() {
        return frequency;
    }

    /**
     * Get the configured PwmType of this PWM instance. (Hardware/Software)
     * Please note that not all PWM providers support both hardware and software
     * PWM generators.  Please consult the documentation for your PWM provider
     * to determine what support is available and what limitations may apply.
     *
     * @return the PwmType for this PWM instance
     */
    public PwmType getPwmType() {
        return pwmType;
    }

    /**
     * Get the configured polarity of this PWM instance. (Normal/Inversed)
     *
     * @return the PwmPolarity for this PWM instance
     */
    public PwmPolarity getPolarity() {
        return polarity;
    }

    /**
     * Get the configured PWM duty-cycle value that is automatically applied
     * to the PWM instance when the Pi4J context is shutdown.
     *
     * @return optional duty-cycle value expressed as a percentage (range: 0-100)
     * that is applied when shutting down the Pi4J context.
     */
    public Double getShutdownValue() {
        return shutdownValue;
    }

    /**
     * Get the configured PWM duty-cycle value that is automatically applied to
     * the PWM instance when this PWM instance is created and initialized.
     *
     * @return duty-cycle value expressed as a percentage (range: 0-100)
     * that is applied when creating and initializing the PWM instance.
     */
    public Double getInitialValue() {
        return initialValue;
    }

    /**
     * Returns the channel number this I/O is assigned to within its bus.
     *
     * @return the channel number, or {@code null} if not configured
     */
    public Integer getChannel() {
        return channel;
    }

    /**
     * Returns the chip number this I/O is assigned to.
     *
     * @return the chip number, or {@code null} if not configured
     */
    public Integer getChip() {
        return chip;
    }

    /**
     * Returns the BCM pin number this I/O is bound to.
     *
     * @return the BCM pin number, or {@code null} if not configured
     */
    public Integer getBcm() {
        return bcm;
    }
}

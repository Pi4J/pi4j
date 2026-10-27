package com.pi4j.io.gpio.digital;

import com.pi4j.context.Context;
import com.pi4j.io.IOConfig;

/**
 * Immutable configuration for a {@link DigitalOutput} pin, describing the pin address, initial state,
 * shutdown state, and on-state mapping. Instances are assembled with a {@link DigitalOutputConfigBuilder}.
 *
 * @param id the configuration identifier
 * @param name the configuration name
 * @param description the configuration description
 * @param bcm the Broadcom GPIO pin number
 * @param bus the GPIO bus/chip number (defaulting to 0)
 * @param initialState the state applied when the output is initialized
 * @param shutdownState the state applied when Pi4J shuts down
 * @param onState the {@link DigitalState} treated as the logical "on" state (defaulting to HIGH)
 */
public record DigitalOutputConfig(
    @Override String id,
    @Override String name,
    @Override String description,
    Integer bcm,
    Integer bus,
    DigitalState initialState,
    DigitalState shutdownState,
    DigitalState onState
) implements IOConfig {


    /**
     * Creates a new {@link DigitalOutputConfigBuilder}.
     *
     * @param context the Pi4J runtime context
     * @return a new configuration builder instance
     * @deprecated Please use DigitalOutput.newConfigBuilder() instead.
     */
    @Deprecated(since = "5.0")
    public static DigitalOutputConfigBuilder newBuilder(Context context) {
        return DigitalOutputConfigBuilder.newInstance();
    }

    /**
     * Returns the target pin using its legacy "address" name.
     *
     * @return the BCM pin number
     * @deprecated use {@link #bcm()} instead.
     */
    @Deprecated(forRemoval = true)
    public Integer address() {
        return bcm;
    }

    /**
     * Returns the target pin using its legacy "address" name.
     *
     * @return the BCM pin number
     * @deprecated use {@link #bcm()} instead.
     */
    @Deprecated(forRemoval = true)
    public Integer getAddress() {
        return bcm;
    }

    /**
     * Bean-style accessor equivalent to {@link #bcm()}.
     *
     * @return the BCM pin number
     */
    public Integer getBcm() {
        return bcm;
    }

    /**
     * Bean-style accessor equivalent to {@link #bus()}.
     *
     * @return the bus number
     */
    public Integer getBus() {
        return bus;
    }

    /**
     * Bean-style accessor equivalent to {@link #initialState()}.
     *
     * @return the configured initial state, or {@code null} if none was set
     */
    public DigitalState getInitialState() {
        return initialState;
    }

    /**
     * Bean-style accessor equivalent to {@link #shutdownState()}.
     *
     * @return the configured shutdown state, or {@code null} if none was set
     */
    public DigitalState getShutdownState() {
        return shutdownState;
    }

    /**
     * Sets the state the output is driven to when Pi4J shuts down, returning a new configuration instance.
     *
     * @param state the shutdown state to apply
     * @return a new {@link DigitalOutputConfig} with the updated shutdown state
     */
    public DigitalOutputConfig shutdownState(DigitalState state) {
        return new DigitalOutputConfig(id, name, description, bcm, bus, initialState, state, onState);
    }

    /**
     * Bean-style accessor equivalent to {@link #onState()}.
     *
     * @return the configured on-state, or {@code null} if none was set
     */
    public DigitalState getOnState() {
        return onState;
    }

    /**
     * Returns the unique identifier used to distinguish GPIO devices, derived from the configured
     * BCM pin number so that each physical GPIO pin maps to a distinct identifier.
     *
     * @return the BCM GPIO pin number serving as this device's unique identifier
     */
    @Override
    public int getUniqueIdentifier() {
        return bcm == null ? 0 : bcm;
    }

    @Override
    public void validate() {
        if (id == null) {
            throw new IllegalStateException("ID cannot be null");
        }
    }
}

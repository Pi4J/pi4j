package com.pi4j.io.gpio.digital;

import com.pi4j.context.Context;
import com.pi4j.io.IOConfig;

/**
 * Immutable configuration for a {@link DigitalInput} pin. Instances are typically assembled through a
 * {@link DigitalInputConfigBuilder}.
 *
 * @param id the configuration identifier
 * @param name the configuration name
 * @param description the configuration description
 * @param bcm the Broadcom GPIO pin number
 * @param bus the GPIO bus/chip number (defaulting to 0)
 * @param pull the pull resistance (pull-up, pull-down, or none) applied to the input pin
 * @param debounce the debounce interval in microseconds used to filter spurious state transitions
 * @param onState the {@link DigitalState} treated as the logical "on" state (defaulting to HIGH)
 */
public record DigitalInputConfig(
    @Override String id,
    @Override String name,
    @Override String description,
    Integer bcm,
    Integer bus,
    PullResistance pull,
    Long debounce,
    DigitalState onState
) implements IOConfig {

    /** Property key under which the Broadcom pin number is stored in a configuration map. */
    public static final String BCM_KEY = "bcm";

    /** Property key under which the bus number is stored in a configuration map. */
    public static final String BUS_KEY = "bus";

    /** Property key under which the on-state value is stored in a configuration map. */
    public static final String ON_STATE_KEY = "onstate";

    /** Property key under which the pull resistance value is stored in a configuration map. */
    public static final String PULL_RESISTANCE_KEY = "pull";

    /** Property key under which the debounce interval is stored in a configuration map. */
    public static final String DEBOUNCE_RESISTANCE_KEY = "debounce";

    /**
     * Creates a new {@link DigitalInputConfigBuilder}.
     *
     * @param context the Pi4J context
     * @return a new configuration builder instance
     * @deprecated As of version 5, please use DigitalInput.newConfigBuilder() instead.
     */
    @Deprecated(since = "5.0")
    public static DigitalInputConfigBuilder newBuilder(Context context) {
        return DigitalInputConfigBuilder.newInstance();
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
     * Bean-style accessor equivalent to {@link #pull()}.
     *
     * @return the configured {@link PullResistance}
     */
    public PullResistance getPull() {
        return pull;
    }

    /**
     * Bean-style accessor equivalent to {@link #debounce()}.
     *
     * @return the configured debounce interval in microseconds, or {@code null} if unset
     */
    public Long getDebounce() {
        return debounce;
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

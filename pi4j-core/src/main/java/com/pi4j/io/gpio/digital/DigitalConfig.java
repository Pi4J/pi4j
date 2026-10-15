package com.pi4j.io.gpio.digital;

import com.pi4j.config.BcmConfig;
import com.pi4j.config.BusConfig;
import com.pi4j.io.IOConfig;

/**
 * Configuration contract for a {@link Digital} I/O instance, extending the generic {@link DigitalConfig}
 * with the {@link DigitalState} that is considered the logical "on" state.
 */
public interface DigitalConfig extends BusConfig, BcmConfig, IOConfig {

    /**
     * Property key under which the on-state value is stored in a configuration map.
     */
    String ON_STATE_KEY = "onstate";

    /**
     * Returns the {@link DigitalState} that is treated as the logical "on" state for this instance,
     * used by on/off convenience methods to map a physical level to an on/off meaning.
     *
     * @return the configured on-state, or {@code null} if none was set (defaulting to HIGH)
     */
    DigitalState onState();

    /**
     * Bean-style accessor equivalent to {@link #onState()}.
     *
     * @return the configured on-state, or {@code null} if none was set
     */
    default DigitalState getOnState() {
        return this.onState();
    }

    /**
     * Returns the unique identifier used to distinguish GPIO devices, derived from the configured
     * BCM pin number so that each physical GPIO pin maps to a distinct identifier.
     *
     * @return the BCM GPIO pin number serving as this device's unique identifier
     */
    @Override
    default int getUniqueIdentifier() {
        return bcm();
    }
}

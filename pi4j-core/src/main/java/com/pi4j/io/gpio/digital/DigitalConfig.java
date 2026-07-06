package com.pi4j.io.gpio.digital;

import com.pi4j.config.Config;
import com.pi4j.io.gpio.GpioConfig;

/**
 * Configuration contract for a {@link Digital} I/O instance, extending the generic {@link GpioConfig}
 * with the BCM pin assignment and the {@link DigitalState} that is considered the logical "on" state.
 *
 * @param <CONFIG_TYPE> the concrete configuration type, used as the self-referencing builder/return type
 */
public interface DigitalConfig<CONFIG_TYPE extends Config> extends GpioConfig<CONFIG_TYPE> {

    /**
     * Property key under which the on-state value is stored in a configuration map.
     */
    String ON_STATE_KEY = "onstate";

    /**
     * Returns the BCM (Broadcom) GPIO pin number this I/O instance is bound to.
     *
     * @return the configured BCM pin number, or {@code null} if none was set
     */
    Integer bcm();

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
}

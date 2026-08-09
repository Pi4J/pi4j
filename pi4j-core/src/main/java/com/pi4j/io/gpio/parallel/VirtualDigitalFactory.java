package com.pi4j.io.gpio.parallel;

import com.pi4j.config.Config;
import com.pi4j.io.gpio.digital.Digital;
import com.pi4j.io.gpio.digital.DigitalConfig;

/**
 * Package-private function specification for constructing {@link Digital} devices.
 * This is essentially shorthand for what would otherwise be a verbose type definition.
 * <p>
 * This represents a high-level abstraction for what is exposed by the framework as
 * {@link com.pi4j.provider.Provider#create(Config)}, but does not require the implementation to be a
 * complete {@link com.pi4j.provider.Provider}.
 *
 * @param <D> type of digital device
 * @param <C> type of digital device config
 */
@FunctionalInterface
interface VirtualDigitalFactory<D extends Digital<D, C, ?>, C extends DigitalConfig> {
    /**
     * Creates a new virtual digital device.
     * @param config the device configuration
     * @return the new virtual digital device
     */
    D createDigital(C config);
}

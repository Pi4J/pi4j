package com.pi4j.io.gpio;

import com.pi4j.io.IO;
import com.pi4j.provider.Provider;

/**
 * Common base contract for Pi4J GPIO-based I/O instances such as digital inputs/outputs and PWM pins.
 * It refines {@link IO} for GPIO pins, where the underlying {@link GpioConfig} addresses the I/O by a
 * Broadcom (BCM) GPIO pin number and the backing {@link Provider} is typically a {@link GpioProvider}.
 *
 * @param <IO_TYPE>       the concrete GPIO I/O type, returned by fluent identity setters for chaining
 * @param <CONFIG_TYPE>   the {@link GpioConfig} type describing and creating this I/O instance
 * @param <PROVIDER_TYPE> the {@link Provider} type that instantiated and backs this I/O instance
 */
public interface Gpio<IO_TYPE extends IO<IO_TYPE, CONFIG_TYPE, PROVIDER_TYPE>,
    CONFIG_TYPE extends GpioConfig,
    PROVIDER_TYPE extends Provider>
    extends IO<IO_TYPE, CONFIG_TYPE, PROVIDER_TYPE> {
}

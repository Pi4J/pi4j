package com.pi4j.io.gpio;

import com.pi4j.io.IO;
import com.pi4j.provider.Provider;

/**
 * Common base contract for Pi4J {@link Provider}s that create and manage GPIO-based I/O such as digital
 * inputs/outputs and PWM pins. It specializes {@link Provider} to GPIO instances configured by a
 * {@link GpioConfig}, serving as the parent type for the concrete GPIO provider interfaces.
 *
 * @param <PROVIDER_TYPE> the concrete GPIO provider sub-type
 * @param <IO_TYPE>       the {@link IO} type this provider creates
 * @param <CONFIG_TYPE>   the {@link GpioConfig} type accepted when creating I/O instances
 */
public interface GpioProvider<PROVIDER_TYPE extends GpioProvider,
        IO_TYPE extends IO,
        CONFIG_TYPE extends GpioConfig>
        extends Provider<PROVIDER_TYPE, IO_TYPE, CONFIG_TYPE> {
    // MARKER INTERFACE
}

package com.pi4j.io.gpio.digital;

import com.pi4j.io.gpio.GpioProvider;

/**
 * Common base interface for providers that create digital (on/off) GPIO I/O instances, shared by the input
 * and output specializations such as {@link DigitalOutputProvider}. It refines {@link GpioProvider} to bind
 * the digital-specific I/O and configuration types.
 *
 * @param <PROVIDER_TYPE> the concrete provider type
 * @param <DIGITAL_TYPE>  the concrete {@link Digital} I/O type this provider creates
 * @param <CONFIG_TYPE>   the {@link DigitalConfig} type used to configure created instances
 */
public interface DigitalProvider<PROVIDER_TYPE extends DigitalProvider, DIGITAL_TYPE extends Digital, CONFIG_TYPE extends DigitalConfig>
        extends GpioProvider<PROVIDER_TYPE, DIGITAL_TYPE, CONFIG_TYPE> {

}

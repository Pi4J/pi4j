package com.pi4j.io.gpio.digital;

import com.pi4j.event.Event;

/**
 * Base contract for events originating from a {@link Digital} I/O instance, extending the generic
 * Pi4J {@link Event} with access to the {@link Digital} source that produced the event.
 *
 * @param <DIGITAL_TYPE> the concrete {@link Digital} type that is the source of the event
 * @param <CONFIG_TYPE> the {@link DigitalConfig} type of the source
 * @param <PROVIDER_TYPE> the {@link DigitalProvider} type of the source
 */
public interface DigitalEvent<DIGITAL_TYPE extends Digital<DIGITAL_TYPE, CONFIG_TYPE, PROVIDER_TYPE>,
        CONFIG_TYPE extends DigitalConfig<CONFIG_TYPE>,
        PROVIDER_TYPE extends DigitalProvider>
        extends Event {
    /**
     * Returns the digital I/O instance that produced this event.
     *
     * @return the source {@link Digital} instance
     */
    DIGITAL_TYPE source();
}

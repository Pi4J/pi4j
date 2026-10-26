package com.pi4j.io.gpio.digital;

import com.pi4j.event.Event;
import com.pi4j.io.IOConfig;

/**
 * Base contract for events originating from a {@link Digital} I/O instance, extending the generic
 * Pi4J {@link Event} with access to the {@link Digital} source that produced the event.
 *
 * @param <DIGITAL_TYPE> the concrete {@link Digital} type that is the source of the event
 * @param <CONFIG_TYPE> the {@link IOConfig} type of the source
 */
public interface DigitalEvent<DIGITAL_TYPE extends Digital<CONFIG_TYPE>,
        CONFIG_TYPE extends IOConfig>
        extends Event {
    /**
     * Returns the digital I/O instance that produced this event.
     *
     * @return the source {@link Digital} instance
     */
    DIGITAL_TYPE source();
}

package com.pi4j.io.gpio.digital;

import com.pi4j.event.Listener;

/**
 * Marker interface for listeners of a specific {@link DigitalEvent} type, serving as the common super-type
 * for typed digital event listeners in Pi4J.
 *
 * @param <EVENT_TYPE> the {@link DigitalEvent} subtype this listener handles
 */
public interface DigitalListener<EVENT_TYPE extends DigitalEvent> extends Listener {
    // MARKER INTERFACE
}

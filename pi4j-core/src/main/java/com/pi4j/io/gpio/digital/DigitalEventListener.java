package com.pi4j.io.gpio.digital;

import com.pi4j.event.Listener;

/**
 * Marker interface identifying listeners that handle digital I/O events, serving as the common
 * super-type for the digital event listeners registered via {@link Digital#addListener}.
 */
public interface DigitalEventListener extends Listener {
    // MARKER INTERFACE
}

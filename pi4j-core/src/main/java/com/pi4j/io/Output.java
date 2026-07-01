package com.pi4j.io;

/**
 * Marker interface identifying an I/O instance as an output-capable device, that is one that
 * drives signals or data to external hardware. It is implemented by output types such as
 * {@link com.pi4j.io.gpio.digital.DigitalOutput} and complements the {@link Input} marker used
 * for input-capable devices.
 */
public interface Output {
    // MARKER INTERFACE
}

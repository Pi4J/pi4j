package com.pi4j.io;

import com.pi4j.io.exception.IOException;

/**
 * Write side of a binary on/off I/O capability, switching a device or signal between its
 * two logical states. It is combined with its read counterpart in {@link OnOff} and is
 * implemented by binary outputs such as {@link com.pi4j.io.gpio.digital.DigitalOutput}.
 */
public interface OnOffWrite {
    /**
     * Switches this I/O to its "on" (active/high) state.
     *
     * @throws IOException if the underlying I/O operation fails
     */
    void on() throws IOException;

    /**
     * Switches this I/O to its "off" (inactive/low) state.
     *
     * @throws IOException if the underlying I/O operation fails
     */
    void off() throws IOException;

    /**
     * Sets the state from a boolean, switching on when {@code true} and off when {@code false}.
     *
     * @param state {@code true} to switch on, {@code false} to switch off
     * @throws IOException if the underlying I/O operation fails
     */
    default void setState(boolean state) throws IOException {
        if (state) {
            on();
        } else {
            off();
        }
        return (T) this;
    }
}

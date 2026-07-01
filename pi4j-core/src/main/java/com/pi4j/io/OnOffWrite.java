package com.pi4j.io;

import com.pi4j.io.exception.IOException;

/**
 * Write side of a binary on/off I/O capability, switching a device or signal between its
 * two logical states. It is combined with its read counterpart in {@link OnOff} and is
 * implemented by binary outputs such as {@link com.pi4j.io.gpio.digital.DigitalOutput}.
 *
 * @param <T> the concrete I/O type returned by the state-changing methods, enabling fluent
 *            method chaining on the implementing instance
 */
public interface OnOffWrite<T> {
    /**
     * Switches this I/O to its "on" (active/high) state.
     *
     * @return this instance for method chaining
     * @throws IOException if the underlying I/O operation fails
     */
    T on() throws IOException;

    /**
     * Switches this I/O to its "off" (inactive/low) state.
     *
     * @return this instance for method chaining
     * @throws IOException if the underlying I/O operation fails
     */
    T off() throws IOException;

    /**
     * Sets the state from a boolean, switching on when {@code true} and off when {@code false}.
     *
     * @param state {@code true} to switch on, {@code false} to switch off
     * @return this instance for method chaining
     * @throws IOException if the underlying I/O operation fails
     */
    @SuppressWarnings("unchecked")
    default T setState(boolean state) throws IOException {
        if (state) {
            on();
        } else {
            off();
        }
        return (T) this;
    }
}

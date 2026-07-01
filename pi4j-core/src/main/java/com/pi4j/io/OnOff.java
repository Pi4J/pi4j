package com.pi4j.io;

import com.pi4j.io.exception.IOException;

/**
 * A two-state (on/off) I/O that can be both read and written, combining {@link OnOffRead} and
 * {@link OnOffWrite}. It adds a {@link #toggle()} convenience for inverting the current state.
 *
 * @param <T> the concrete type returned by the fluent operations for method chaining
 */
public interface OnOff<T> extends OnOffRead<T>, OnOffWrite<T> {
    /**
     * Inverts the current state: turns off if currently on, otherwise turns on.
     *
     * @return this instance for method chaining
     * @throws IOException if the underlying I/O operation fails
     */
    default T toggle() throws IOException {
        if (isOn()) {
            return off();
        } else {
            return on();
        }
    }
}

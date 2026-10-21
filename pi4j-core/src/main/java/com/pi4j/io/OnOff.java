package com.pi4j.io;

import com.pi4j.io.exception.IOException;

/**
 * A two-state (on/off) I/O that can be both read and written, combining {@link OnOffRead} and
 * {@link OnOffWrite}. It adds a {@link #toggle()} convenience for inverting the current state.
 */
public interface OnOff extends OnOffRead, OnOffWrite {
    /**
     * Inverts the current state: turns off if currently on, otherwise turns on.
     *
     * @throws IOException if the underlying I/O operation fails
     */
    default void toggle() throws IOException {
        if (isOn()) {
            off();
        } else {
            on();
        }
    }
}

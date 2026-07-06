package com.pi4j.io;

/**
 * Read-only view of a two-state (on/off) I/O, exposing whether it is currently on or off.
 * It is the read half of {@link OnOff} and the basis for {@link ListenableOnOffRead}.
 *
 * @param <T> the concrete implementing type
 */
public interface OnOffRead<T> {
    /**
     * Returns the current state.
     *
     * @return {@code true} if currently on, {@code false} if off
     */
    boolean isOn();

    /**
     * Returns the inverse of {@link #isOn()}.
     *
     * @return {@code true} if currently off, {@code false} if on
     */
    default boolean isOff() { return !isOn(); };
}

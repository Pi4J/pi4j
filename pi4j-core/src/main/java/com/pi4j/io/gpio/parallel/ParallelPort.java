package com.pi4j.io.gpio.parallel;

import com.pi4j.io.IOType;
import com.pi4j.io.gpio.Gpio;
import com.pi4j.io.gpio.digital.DigitalState;

/**
 * A {@link Gpio} which is capable of reading and writing multiple bits to a device.
 */
public interface ParallelPort extends com.pi4j.io.IO<ParallelPort, ParallelPortConfig> {

    /**
     * Enumeration representing the direction of the parallel port.
     */
    enum Direction {
        INPUT, OUTPUT
    }

    /**
     * Get the {@link IOType} of the parallel port {@link com.pi4j.io.IO}.
     * @return the type of {@link com.pi4j.io.IO} being implemented
     */
    @Override
    default IOType type() {
        return IOType.PARALLEL;
    }

    /**
     * Write the bits specified by `value` to the device.
     * @param value the value to write
     */
    void write(int value);

    /**
     * Read the current value from the parallel port.
     * @return a bitmask of the current value
     */
    int read();

    /**
     * Set the direction of the parallel port.
     * @param direction the direction to set
     */
    void setDirection(Direction direction);

    /**
     * Get the direction of the parallel port.
     * @return the current direction
     */
    Direction getDirection();

    /**
     * Add a listener for state changes on the parallel port.
     * @param listener the listener to add
     * @return the parallel port instance
     */
    ParallelPort addListener(PinStateChangeListener listener);

    /**
     * Remove a listener for state changes on the parallel port.
     * @param listener the listener to remove
     * @return the parallel port instance
     */
    ParallelPort removeListener(PinStateChangeListener listener);

    /**
     * Event representing a change in the state of a pin on the parallel port.
     * @param offset the offset of the pin that changed
     * @param newState the new state of the pin
     */
    record PinStateChangedEvent(int offset, DigitalState newState) {}

    /**
     * Notification representing a change in the state of one or more pins on the parallel port.
     * @param source the parallel port instance
     * @param eventsMissing whether any events are missing
     * @param events the events that occurred
     */
    record PinStateChangedNotification(ParallelPort source, boolean eventsMissing, PinStateChangedEvent... events) {}

    /**
     * Listener for low-level pin-state change events on the parallel port.
     */
    @FunctionalInterface
    interface PinStateChangeListener extends com.pi4j.event.Listener {
        void onPinStateChanged(PinStateChangedNotification notification);
    }
}

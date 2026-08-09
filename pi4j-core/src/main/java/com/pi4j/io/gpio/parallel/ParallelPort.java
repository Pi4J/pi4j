package com.pi4j.io.gpio.parallel;

import com.pi4j.io.IOType;
import com.pi4j.io.gpio.Gpio;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalInputProvider;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalOutputProvider;

/**
 * A {@link Gpio} which is capable of reading and writing multiple bits to a device.
 */
public interface ParallelPort extends com.pi4j.io.IO<ParallelPort, ParallelPortConfig, ParallelPortProvider> {

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
    ParallelPort addListener(Listener listener);

    /**
     * Remove a listener for state changes on the parallel port.
     * @param listener the listener to remove
     * @return the parallel port instance
     */
    ParallelPort removeListener(Listener listener);

    /**
     * Event representing a change in the value of the parallel port.
     */
    record ValueChangeEvent(ParallelPort source, int value) {}

    /**
     * Listener for value change events on the parallel port.
     */
    @FunctionalInterface
    interface Listener extends com.pi4j.event.Listener {
        void onValueChange(ValueChangeEvent event);
    }

    /**
     * Create a digital input provider based on the parallel port.
     * <p>
     * Implementing classes ideally provide a singleton instance of this provider to avoid potential conflicts
     * in BCM allocations.
     *
     * @return a provider for creating {@link DigitalInput}s backed by this port
     */
    default DigitalInputProvider digitalInputProvider() {
        return new VirtualDigitalInputProvider(this);
    }

    /**
     * Create a digital output provider based on the parallel port.
     * <p>
     * Implementing classes ideally provide a singleton instance of this provider to avoid potential conflicts
     * in BCM allocations.
     *
     * @return a provider for creating {@link DigitalOutput}s backed by this port
     */
    default DigitalOutputProvider digitalOutputProvider() {
        return new VirtualDigitalOutputProvider(this);
    }
}

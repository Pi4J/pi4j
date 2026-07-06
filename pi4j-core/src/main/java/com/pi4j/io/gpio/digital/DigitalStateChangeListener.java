package com.pi4j.io.gpio.digital;


/**
 * Listener that is notified whenever a digital I/O instance changes state. Register an implementation with a
 * {@link Digital} instance to receive {@link DigitalStateChangeEvent}s.
 */
public interface DigitalStateChangeListener extends DigitalListener<DigitalStateChangeEvent> {
    /**
     * Invoked when the state of the observed digital I/O changes.
     *
     * @param event the event describing the source and its new state
     */
    void onDigitalStateChange(DigitalStateChangeEvent event);
}

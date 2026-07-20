package com.pi4j.io.gpio.digital;

/**
 * Entry point for runtime GPIO pin direction reconfiguration. Obtained by calling
 * {@link DigitalInput#reconfigure()} or {@link DigitalOutput#reconfigure()} on an
 * FFM-backed pin; implementations that do not support reconfiguration throw
 * {@link UnsupportedOperationException} from those default methods.
 */
public interface PinReconfigurer {

    /**
     * Begins reconfiguration of the pin as a digital output.
     *
     * @return an {@link OutputReconfigurer} whose {@code create} method releases the current
     *         GPIO line and re-requests it as an output with the supplied configuration
     */
    OutputReconfigurer digitalOutput();

    /**
     * Begins reconfiguration of the pin as a digital input.
     *
     * @return an {@link InputReconfigurer} whose {@code create} method releases the current
     *         GPIO line and re-requests it as an input with the supplied configuration
     */
    InputReconfigurer digitalInput();
}

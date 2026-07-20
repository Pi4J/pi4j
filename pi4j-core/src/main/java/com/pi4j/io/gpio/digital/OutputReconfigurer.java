package com.pi4j.io.gpio.digital;

/**
 * Step of the pin-reconfiguration fluent API that creates a {@link DigitalOutput}, atomically
 * releasing the previous GPIO line claim and re-requesting it as an output. Obtained from
 * {@link PinReconfigurer#digitalOutput()}.
 */
public interface OutputReconfigurer {

    /**
     * Releases the currently claimed GPIO line and re-requests it as a digital output
     * with the supplied configuration.
     *
     * @param config the output configuration (BCM pin, bus, initial state, etc.)
     * @return the newly created {@link DigitalOutput}
     * @throws Exception if shutting down the old pin or creating the new one fails
     */
    DigitalOutput create(DigitalOutputConfig config) throws Exception;

    /**
     * Releases the currently claimed GPIO line and re-requests it as a digital output, reusing the
     * addressing (BCM pin and bus) of the pin being reconfigured. Output-specific settings such as
     * the initial and shutdown states are left at their defaults; use {@link #create(DigitalOutputConfig)}
     * to specify them explicitly.
     *
     * @return the newly created {@link DigitalOutput}
     * @throws Exception if shutting down the old pin or creating the new one fails
     */
    DigitalOutput create() throws Exception;
}

package com.pi4j.io.gpio.digital;

import com.pi4j.exception.Pi4JException;

/**
 * Step of the pin-reconfiguration fluent API that creates a {@link DigitalInput}, atomically
 * releasing the previous GPIO line claim and re-requesting it as an input. Obtained from
 * {@link PinReconfigurer#digitalInput()}.
 */
public interface InputReconfigurer {

    /**
     * Releases the currently claimed GPIO line and re-requests it as a digital input
     * with the supplied configuration.
     *
     * @param config the input configuration (BCM pin, bus, pull resistance, debounce, etc.)
     * @return the newly created {@link DigitalInput}
     * @throws Pi4JException if shutting down the old pin or creating the new one fails
     */
    DigitalInput create(DigitalInputConfig config) throws Pi4JException;

    /**
     * Releases the currently claimed GPIO line and re-requests it as a digital input, reusing the
     * addressing (BCM pin and bus) of the pin being reconfigured. Input-specific settings such as
     * pull resistance and debounce are left at their defaults; use {@link #create(DigitalInputConfig)}
     * to specify them explicitly.
     *
     * @return the newly created {@link DigitalInput}
     * @throws Pi4JException if shutting down the old pin or creating the new one fails
     */
    DigitalInput create() throws Pi4JException;
}

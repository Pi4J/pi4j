package com.pi4j.io.exception;

import com.pi4j.exception.Pi4JException;

/**
 * Base unchecked exception for failures originating in the Pi4J I/O subsystem
 * (GPIO, I2C, SPI, PWM, and related providers). It extends
 * {@link Pi4JException} and is the common supertype for the more specific I/O
 * exceptions in this package, allowing callers to catch all I/O-related
 * failures uniformly.
 *
 * @see <a href="http://www.pi4j.com/">http://www.pi4j.com/</a>
 */
public class IOException extends Pi4JException {

    /**
     * Creates an exception with a descriptive error message.
     *
     * @param message human-readable description of the I/O failure
     */
    public IOException(String message) {
        super(message);
    }

    /**
     * Creates an exception that wraps an underlying cause.
     *
     * @param cause the throwable that triggered this I/O failure
     */
    public IOException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates an exception with a descriptive error message and an underlying cause.
     *
     * @param message human-readable description of the I/O failure
     * @param cause   the throwable that triggered this I/O failure
     */
    public IOException(String message, Throwable cause) {
        super(message, cause);
    }
}

package com.pi4j.io.exception;

/**
 * Thrown when an I/O operation receives a value that cannot be interpreted as a
 * valid integer. A specialization of {@link IOException}.
 *
 * @see <a href="http://www.pi4j.com/">http://www.pi4j.com/</a>
 */
public class IOIllegalValueException extends IOException {

    /**
     * Creates an exception reporting that the supplied value is not a valid integer.
     */
    public IOIllegalValueException(){
        super("The requested value is not a valid Integer.");
    }
}

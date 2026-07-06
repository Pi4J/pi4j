package com.pi4j.io.exception;


/**
 * Thrown when reading data from an I/O device or bus fails. A specialization of
 * {@link IOException}.
 *
 * @see <a href="http://www.pi4j.com/">http://www.pi4j.com/</a>
 */
public class IOReadException extends IOException {

    /**
     * Creates an exception with a descriptive read-failure message.
     *
     * @param message human-readable description of the read failure
     */
    public IOReadException(String message){
        super(message);
    }

    /**
     * Creates an exception from a low-level read error code returned by the underlying I/O layer.
     *
     * @param error the native error code reported by the failed read operation
     */
    public IOReadException(int error){
        super("I/O READ ERROR: " + error);
    }
}

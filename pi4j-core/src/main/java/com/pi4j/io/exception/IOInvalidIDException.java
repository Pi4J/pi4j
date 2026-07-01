package com.pi4j.io.exception;

/**
 * Thrown when an I/O operation requires an instance identifier but none was
 * supplied. A specialization of {@link IOException}.
 *
 * @see <a href="http://www.pi4j.com/">http://www.pi4j.com/</a>
 */
public class IOInvalidIDException extends IOException {

    /**
     * Creates an exception reporting that the required ID attribute is missing from the request.
     */
    public IOInvalidIDException(){
        super("The requested operation is missing the ID attribute.  Unable to complete request.");
    }
}

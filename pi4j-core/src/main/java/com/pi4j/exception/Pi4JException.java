package com.pi4j.exception;

/**
 * Root unchecked exception type for all errors raised by the Pi4J library. All other
 * Pi4J-specific exceptions extend this class, so callers may catch {@code Pi4JException}
 * to handle any failure originating from Pi4J. It is a {@link RuntimeException}, so it is
 * not required to be declared or caught.
 *
 * @see <a href="http://www.pi4j.com/">http://www.pi4j.com/</a>
 */
public class Pi4JException extends RuntimeException {

    /**
     * Creates a new exception with the given detail message.
     *
     * @param message human-readable description of the failure
     */
    public Pi4JException(String message){
        super(message);
    }

    /**
     * Creates a new exception wrapping an underlying cause. The message is inherited from
     * the cause.
     *
     * @param cause the underlying throwable that triggered this failure
     */
    public Pi4JException(Throwable cause){
        super(cause);
    }

    /**
     * Creates a new exception with the given detail message and underlying cause.
     *
     * @param message human-readable description of the failure
     * @param cause   the underlying throwable that triggered this failure
     */
    public Pi4JException(String message, Throwable cause){
        super(message,cause);
    }
}

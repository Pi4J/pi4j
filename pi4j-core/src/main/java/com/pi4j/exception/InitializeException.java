package com.pi4j.exception;

/**
 * Thrown when a Pi4J component fails to initialize, for example when the runtime context,
 * a provider, or an I/O instance cannot be brought into a usable state. It is the
 * initialization-phase counterpart of {@link ShutdownException}.
 *
 * @see <a href="http://www.pi4j.com/">http://www.pi4j.com/</a>
 */
public class InitializeException extends Pi4JException {

    /**
     * Creates a new exception with the given detail message.
     *
     * @param message human-readable description of the initialization failure
     */
    public InitializeException(String message){
        super(message);
    }

    /**
     * Creates a new exception wrapping an underlying cause. The cause's message is reused
     * as the detail message.
     *
     * @param cause the underlying throwable that triggered the initialization failure
     */
    public InitializeException(Throwable cause){
        super(cause.getMessage(), cause);
    }

    /**
     * Creates a new exception with the given detail message and underlying cause.
     *
     * @param message human-readable description of the initialization failure
     * @param cause   the underlying throwable that triggered the initialization failure
     */
    public InitializeException(String message, Throwable cause){
        super(message, cause);
    }
}

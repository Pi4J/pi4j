package com.pi4j.exception;

/**
 * Indicates a failure during a Pi4J lifecycle transition, such as initializing or shutting
 * down the runtime context, a provider, or an I/O instance. It serves as the common base
 * for the more specific {@link InitializeException} and {@link ShutdownException}.
 *
 * @see <a href="http://www.pi4j.com/">http://www.pi4j.com/</a>
 */
public class LifecycleException extends Pi4JException {

    /**
     * Creates a new exception with the given detail message.
     *
     * @param message human-readable description of the lifecycle failure
     */
    public LifecycleException(String message){
        super(message);
    }

    /**
     * Creates a new exception wrapping an underlying cause. The message is inherited from
     * the cause.
     *
     * @param cause the underlying throwable that triggered this failure
     */
    public LifecycleException(Throwable cause){
        super(cause);
    }

    /**
     * Creates a new exception with the given detail message and underlying cause.
     *
     * @param message human-readable description of the lifecycle failure
     * @param cause   the underlying throwable that triggered this failure
     */
    public LifecycleException(String message, Throwable cause){
        super(message,cause);
    }
}

package com.pi4j.exception;

/**
 * Thrown when a Pi4J component fails to shut down cleanly, for example when the runtime
 * context, a provider, or an I/O instance cannot release its resources. It is the
 * shutdown-phase counterpart of {@link InitializeException}.
 *
 * @see <a href="http://www.pi4j.com/">http://www.pi4j.com/</a>
 */
public class ShutdownException extends LifecycleException {

    /**
     * Creates a new exception with the given detail message.
     *
     * @param message human-readable description of the shutdown failure
     */
    public ShutdownException(String message){
        super(message);
    }

    /**
     * Creates a new exception wrapping an underlying cause. The message is inherited from
     * the cause.
     *
     * @param cause the underlying throwable that triggered the shutdown failure
     */
    public ShutdownException(Throwable cause){
        super(cause);
    }

    /**
     * Creates a new exception with the given detail message and underlying cause.
     *
     * @param message human-readable description of the shutdown failure
     * @param cause   the underlying throwable that triggered the shutdown failure
     */
    public ShutdownException(String message, Throwable cause){
        super(message,cause);
    }
}

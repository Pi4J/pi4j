package com.pi4j.exception;

/**
 * Thrown when a Pi4J I/O instance cannot reacquire its native resources after the JVM was restored from a
 * CRaC (Coordinated Restore at Checkpoint) image, for example because another process claimed a GPIO line
 * or a device file disappeared while the resources were released.
 *
 * @see com.pi4j.io.Checkpointable
 * @see <a href="https://docs.azul.com/crac/usage/integrating-crac">Integrating CRaC</a>
 */
public class RestoreException extends LifecycleException {

    /**
     * Creates a new exception with the given detail message.
     *
     * @param message human-readable description of the restore failure
     */
    public RestoreException(String message) {
        super(message);
    }

    /**
     * Creates a new exception with the given detail message and underlying cause.
     *
     * @param message human-readable description of the restore failure
     * @param cause   the underlying throwable that triggered the restore failure
     */
    public RestoreException(String message, Throwable cause) {
        super(message, cause);
    }
}

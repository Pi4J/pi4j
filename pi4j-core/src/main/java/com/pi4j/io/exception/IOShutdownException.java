package com.pi4j.io.exception;


import com.pi4j.io.IO;

/**
 * Thrown when an {@link IO} instance fails to shut down cleanly, for example
 * while releasing its underlying hardware resources during Pi4J teardown. A
 * specialization of {@link IOException}.
 *
 * @see <a href="http://www.pi4j.com/">http://www.pi4j.com/</a>
 */
public class IOShutdownException extends IOException {

    /**
     * Creates an exception describing a failed shutdown of a specific I/O instance.
     *
     * @param instance the {@link IO} instance that failed to shut down; its id is included in the message
     * @param e        the underlying error raised during shutdown, retained as the cause
     */
    public IOShutdownException(IO instance, Exception e){
        super("IO instance [" + instance.getId() + "] failed to properly shutdown: " + e.getMessage(), e);
    }
}

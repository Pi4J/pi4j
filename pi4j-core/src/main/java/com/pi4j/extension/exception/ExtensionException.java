package com.pi4j.extension.exception;

import com.pi4j.exception.Pi4JException;

/**
 * Signals a failure raised by the Pi4J extension subsystem (see {@link com.pi4j.extension.Extension}
 * and {@link com.pi4j.extension.Plugin}), for example when an extension cannot be loaded,
 * initialized, or registered. It extends {@link Pi4JException} and serves as the base type
 * for more specific extension failures such as {@link com.pi4j.provider.exception.ProviderException}.
 *
 * @see <a href="http://www.pi4j.com/">http://www.pi4j.com/</a>
 */
public class ExtensionException extends Pi4JException {

    /**
     * Creates a new exception with the given detail message.
     *
     * @param message human-readable description of the extension failure
     */
    public ExtensionException(String message){
        super(message);
    }

    /**
     * Creates a new exception wrapping an underlying cause. The message is inherited from
     * the cause.
     *
     * @param cause the underlying throwable that triggered this extension failure
     */
    public ExtensionException(Throwable cause){
        super(cause);
    }

    /**
     * Creates a new exception with the given detail message and underlying cause.
     *
     * @param message human-readable description of the extension failure
     * @param cause   the underlying throwable that triggered this extension failure
     */
    public ExtensionException(String message, Throwable cause){
        super(message,cause);
    }
}

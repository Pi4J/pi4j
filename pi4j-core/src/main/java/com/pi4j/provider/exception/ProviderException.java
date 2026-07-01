package com.pi4j.provider.exception;

import com.pi4j.extension.exception.ExtensionException;
import com.pi4j.provider.Provider;

/**
 * Base unchecked exception for all error conditions related to Pi4J {@link Provider}s,
 * such as a provider being missing, of the wrong type, or failing to initialize. It
 * extends {@link ExtensionException} since providers are loaded as Pi4J extensions, and
 * serves as the common superclass for the more specific provider exceptions in this
 * package.
 *
 * @see <a href="http://www.pi4j.com/">http://www.pi4j.com/</a>
 */
public class ProviderException extends ExtensionException {

    /**
     * Creates a provider exception with a descriptive error message.
     *
     * @param message the human-readable description of the failure
     */
    public ProviderException(String message){
        super(message);
    }

    /**
     * Creates a provider exception wrapping an underlying cause.
     *
     * @param cause the underlying throwable that triggered this exception
     */
    public ProviderException(Throwable cause){
        super(cause);
    }

    /**
     * Creates a provider exception with a descriptive message and an underlying cause.
     *
     * @param message the human-readable description of the failure
     * @param cause   the underlying throwable that triggered this exception
     */
    public ProviderException(String message, Throwable cause){
        super(message,cause);
    }

    /**
     * Creates a provider exception for the given provider, building a message from the
     * provider's id and concrete class name.
     *
     * @param provider the provider associated with the failure
     * @param cause    the underlying throwable that triggered this exception
     */
    public ProviderException(Provider provider, Throwable cause){
        super("Provider exception: " + provider.id() + "(" + provider.getClass().getName() + ")",cause);
    }

}

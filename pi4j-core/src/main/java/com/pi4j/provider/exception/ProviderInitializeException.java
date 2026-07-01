package com.pi4j.provider.exception;


/**
 * Thrown when a {@link com.pi4j.provider.Provider} fails during its initialization phase,
 * wrapping the underlying cause that prevented the provider from starting up.
 *
 * @see <a href="http://www.pi4j.com/">http://www.pi4j.com/</a>
 */
public class ProviderInitializeException extends ProviderException {

    /**
     * Creates the exception for a provider that failed to initialize.
     *
     * @param providerId the identifier of the provider that failed to initialize
     * @param ex         the underlying throwable that caused initialization to fail
     */
    public ProviderInitializeException(String providerId, Throwable ex){
        super("Pi4J io [" + providerId + "] failed to initialize(); " + ex.getMessage(), ex);
    }
}

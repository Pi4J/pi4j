package com.pi4j.provider.exception;

/**
 * Thrown when a {@link com.pi4j.provider.Provider} is registered with an identifier that
 * is already in use, since provider identifiers must be unique within the Pi4J runtime.
 *
 * @see <a href="http://www.pi4j.com/">http://www.pi4j.com/</a>
 */
public class ProviderAlreadyExistsException extends ProviderException {

    /**
     * Creates the exception for a duplicate provider identifier.
     *
     * @param providerId the identifier of the provider that already exists
     */
    public ProviderAlreadyExistsException(String providerId){
        super("The Pi4J io [" + providerId + "] already exists.");
    }
}

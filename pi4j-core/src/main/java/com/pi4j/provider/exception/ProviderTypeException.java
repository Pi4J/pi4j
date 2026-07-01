package com.pi4j.provider.exception;


import com.pi4j.provider.Provider;

/**
 * Thrown when a resolved {@link Provider} instance is not assignable to the provider
 * class or interface that the caller requested, indicating a provider type mismatch.
 *
 * @see <a href="http://www.pi4j.com/">http://www.pi4j.com/</a>
 */
public class ProviderTypeException extends ProviderException {

    /**
     * Creates the exception for a provider that does not match the requested provider type.
     *
     * @param provider      the provider instance that was resolved
     * @param providerClass the provider class or interface that was expected
     */
    public ProviderTypeException(Provider provider, Class<? extends Provider> providerClass){
        super("Pi4J provider type mismatch for [" + provider.id() + "(" + provider.getClass().getName() + ")]; provider instance is not of type [" + providerClass.getName() + "]");
    }

}

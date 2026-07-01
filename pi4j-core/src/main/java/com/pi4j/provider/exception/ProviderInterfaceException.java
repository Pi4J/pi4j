package com.pi4j.provider.exception;


import com.pi4j.provider.Provider;

/**
 * Thrown when a {@link Provider} is requested by class but a concrete implementation
 * class is supplied instead of a provider interface, since lookups by type expect an
 * interface that the registered providers implement.
 *
 * @see <a href="http://www.pi4j.com/">http://www.pi4j.com/</a>
 */
public class ProviderInterfaceException extends ProviderException {

    /**
     * Creates the exception for a provider class that is a concrete class rather than an interface.
     *
     * @param providerClass the offending provider class that was supplied instead of an interface
     */
    public ProviderInterfaceException(Class<? extends Provider> providerClass){
        super("Pi4J provider class [" + providerClass + "] is not an Interface but rather a concrete class. Please specify an Interface when requesting a provider by type.");
    }

}

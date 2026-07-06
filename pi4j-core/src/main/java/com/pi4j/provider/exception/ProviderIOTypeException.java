package com.pi4j.provider.exception;


import com.pi4j.io.IOType;
import com.pi4j.provider.Provider;

/**
 * Thrown when a resolved {@link Provider} does not support the expected {@link IOType}
 * (for example, requesting an I2C provider but receiving one for a different I/O type).
 *
 * @see <a href="http://www.pi4j.com/">http://www.pi4j.com/</a>
 */
public class ProviderIOTypeException extends ProviderException {

    /**
     * Creates the exception for a provider whose I/O type does not match the one expected.
     *
     * @param provider the provider instance that was resolved
     * @param ioType   the {@link IOType} that was expected but not supported by the provider
     */
    public ProviderIOTypeException(Provider provider, IOType ioType){
        super("Pi4J provider IO type mismatch for [" + provider.id() + "(" + provider.type().name() + ")]; provider instance is not of IO type [" + ioType.name() + "]");
    }

}

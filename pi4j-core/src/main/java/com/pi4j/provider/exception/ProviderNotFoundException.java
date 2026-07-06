package com.pi4j.provider.exception;


import com.pi4j.io.IOType;
import com.pi4j.provider.Provider;

/**
 * Thrown when no matching {@link Provider} can be located in the Pi4J runtime, typically
 * because the relevant provider JAR is missing from the classpath. The several
 * constructors describe the missing provider by identifier, {@link IOType}, provider
 * class, or a combination thereof.
 *
 * @see <a href="http://www.pi4j.com/">http://www.pi4j.com/</a>
 */
public class ProviderNotFoundException extends ProviderException {

    /**
     * Creates the exception indicating that no providers could be detected at all.
     */
    public ProviderNotFoundException(){
        super("Pi4J provider could not be detected.  Please include a 'provider' JAR in the classpath.");
    }

    /**
     * Creates the exception for a provider that could not be found by identifier.
     *
     * @param providerId the identifier of the provider that was requested
     */
    public ProviderNotFoundException(String providerId){
        super("Pi4J provider [" + providerId + "] could not be found.  Please include this 'provider' JAR in the classpath.");
    }

    /**
     * Creates the exception for a provider that could not be found by I/O type.
     *
     * @param ioType the {@link IOType} for which no provider was available
     */
    public ProviderNotFoundException(IOType ioType){
        super("Pi4J provider IO type [" + ioType + "] could not be found.  Please include this 'provider' JAR in the classpath for this provider type.");
    }

    /**
     * Creates the exception for a provider that could not be found by identifier and I/O type.
     *
     * @param providerId the identifier of the provider that was requested
     * @param ioType     the {@link IOType} that was requested
     */
    public ProviderNotFoundException(String providerId, IOType ioType){
        super("Pi4J provider [" + providerId + "] of type [" + ioType + "] could not be found.  Please include this 'provider' JAR in the classpath.");
    }

    /**
     * Creates the exception for a provider that could not be found by identifier and class.
     *
     * @param providerId    the identifier of the provider that was requested
     * @param providerClass the expected provider class or interface that was requested
     */
    public ProviderNotFoundException(String providerId, Class<? extends Provider> providerClass){
        super("Pi4J provider [" + providerId + "] of class [" + providerClass.getName() + "] could not be found.  Please include this 'provider' JAR in the classpath.");
    }

    /**
     * Creates the exception for a provider that could not be found by class.
     *
     * @param providerClass the provider class or interface for which no provider was available
     */
    public ProviderNotFoundException(Class<? extends Provider> providerClass){
        super("Pi4J provider class [" + providerClass + "] could not be found.  Please include this 'provider' JAR in the classpath for this provider class.");
    }

}

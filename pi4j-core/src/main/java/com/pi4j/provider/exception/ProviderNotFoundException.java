package com.pi4j.provider.exception;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  ProviderNotFoundException.java
 *
 * This file is part of the Pi4J project. More information about
 * this project can be found here:  https://pi4j.com/
 * **********************************************************************
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import com.pi4j.io.IOType;
import com.pi4j.provider.Provider;

/**
 * <p>
 * This exception is thrown if a platform assignment is attempted when a
 * platform instance has already been assigned.
 * </p>
 *
 * @see <a href="http://www.pi4j.com/">http://www.pi4j.com/</a>
 */
public class ProviderNotFoundException extends ProviderException {

    /**
     * Default Constructor
     */
    public ProviderNotFoundException(){
        super("Pi4J provider could not be detected.  Please include a 'provider' JAR in the classpath.");
    }

    /**
     * Alternate Constructor
     */
    public ProviderNotFoundException(String providerId){
        super("Pi4J provider [" + providerId + "] could not be found.  Please include this 'provider' JAR in the classpath.");
    }

    /**
     * Alternate Constructor
     */
    public ProviderNotFoundException(IOType ioType){
        super("Pi4J provider IO type [" + ioType + "] could not be found.  Please include this 'provider' JAR in the classpath for this provider type.");
    }

    /**
     * Alternate Constructor
     */
    public ProviderNotFoundException(String providerId, IOType ioType){
        super("Pi4J provider [" + providerId + "] of type [" + ioType + "] could not be found.  Please include this 'provider' JAR in the classpath.");
    }

    /**
     * Alternate Constructor
     *
     * @param providerClass a {@link Class} object of type {@link Provider}.
     */
    public ProviderNotFoundException(String providerId, Class<? extends Provider> providerClass){
        super("Pi4J provider [" + providerId + "] of class [" + providerClass.getName() + "] could not be found.  Please include this 'provider' JAR in the classpath.");
    }

    /**
     * Alternate Constructor
     *
     * @param providerClass a {@link Class} object of type {@link Provider}.
     */
    public ProviderNotFoundException(Class<? extends Provider> providerClass){
        super("Pi4J provider class [" + providerClass + "] could not be found.  Please include this 'provider' JAR in the classpath for this provider class.");
    }

}

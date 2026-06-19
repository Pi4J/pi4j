package com.pi4j.provider.exception;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  ProviderInterfaceException.java
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

package com.pi4j.provider.exception;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  ProviderTypeException.java
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

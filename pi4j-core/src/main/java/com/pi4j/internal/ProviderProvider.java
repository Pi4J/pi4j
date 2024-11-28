package com.pi4j.internal;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  ProviderProvider.java
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
import com.pi4j.provider.exception.ProviderInterfaceException;
import com.pi4j.provider.exception.ProviderNotFoundException;

public interface ProviderProvider extends ProviderAliases {

    /**
     * <p>provider.</p>
     *
     * @param providerId a {@link java.lang.String} object.
     * @param <T> a T object.
     * @return a T object.
     * @throws com.pi4j.provider.exception.ProviderNotFoundException if any.
     */
    <T extends Provider> T provider(String providerId) throws ProviderNotFoundException;

    /**
     * <p>provider.</p>
     *
     * @param providerId a {@link java.lang.String} object.
     * @param providerClass a T object.
     * @param <T> the provider type
     * @return a T object.
     * @throws ProviderNotFoundException if any.
     */
    <T extends Provider> T provider(String providerId, Class<T> providerClass) throws ProviderNotFoundException;

    /**
     * <p>provider.</p>
     *
     * @param providerClass a {@link java.lang.Class} object.
     * @param <T> a T object.
     * @throws ProviderNotFoundException if any.
     * @throws ProviderInterfaceException if any.
     * @return a T object.
     */
    <T extends Provider> T provider(Class<T> providerClass) throws ProviderNotFoundException, ProviderInterfaceException;

    /**
     * <p>provider.</p>
     *
     * @param ioType a {@link com.pi4j.io.IOType} object.
     * @param <T> a T object.
     * @return a T object.
     * @throws ProviderNotFoundException if the provider specified by {@code ioType} can not be found.
     */
    <T extends Provider> T provider(IOType ioType) throws ProviderNotFoundException;

    /**
     * <p>hasProvider.</p>
     *
     * @param providerId a {@link java.lang.String} object.
     * @return a boolean.
     */
    boolean hasProvider(String providerId);

    /**
     * <p>hasProvider.</p>
     *
     * @param providerClass a {@link java.lang.Class} object.
     * @param <T> a T object.
     * @return a boolean.
     */
    <T extends Provider> boolean hasProvider(Class<T> providerClass);

    /**
     * <p>hasProvider.</p>
     *
     * @param ioType a {@link com.pi4j.io.IOType} object.
     * @param <T> a T object.
     * @return a boolean.
     */
    <T extends Provider> boolean hasProvider(IOType ioType);

    /**
     * <p>provider.</p>
     *
     * @param providerId a {@link java.lang.String} object.
     * @param <T> a T object.
     * @return a T object.
     * @throws ProviderNotFoundException if the provider specified by {@code providerId} can not be found.
     */
    default <T extends Provider> T getProvider(String providerId) throws ProviderNotFoundException{
        return provider(providerId);
    }

    /**
     * <p>provider.</p>
     *
     * @param providerId a {@link java.lang.String} object.
     * @param providerClass
     * @param <T> a T object.
     * @return a T object.
     * @throws ProviderNotFoundException if the provider specified by {@code providerId} can not be found.
     */
    default <T extends Provider> T getProvider(String providerId, Class<T> providerClass) throws ProviderNotFoundException{
        return provider(providerId, providerClass);
    }

    /**
     * <p>provider.</p>
     *
     * @param providerClass a {@link java.lang.Class} object.
     * @param <T> a T object.
     * @return a T object.
     * @throws ProviderNotFoundException if the provider specified by {@code providerClass} can not be found.
     * @throws ProviderInterfaceException if {@code providerClass} is not a valid provider.
     */
    default <T extends Provider> T getProvider(Class<T> providerClass) throws ProviderNotFoundException,
                                                                              ProviderInterfaceException{
        return provider(providerClass);
    }

    /**
     * <p>provider.</p>
     *
     * @param ioType a {@link com.pi4j.io.IOType} object.
     * @param <T> a T object.
     * @return a T object.
     * @throws ProviderNotFoundException if the provider specified by {@code ioType} can not be found.
     */
    default <T extends Provider> T getProvider(IOType ioType) throws ProviderNotFoundException{
        return provider(ioType);
    }

}

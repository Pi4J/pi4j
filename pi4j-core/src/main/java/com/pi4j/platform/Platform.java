package com.pi4j.platform;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  Platform.java
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

import com.pi4j.common.Descriptor;
import com.pi4j.context.Context;
import com.pi4j.extension.Extension;
import com.pi4j.internal.IOCreator;
import com.pi4j.internal.ProviderProvider;
import com.pi4j.io.IO;
import com.pi4j.io.IOConfig;
import com.pi4j.io.IOType;
import com.pi4j.provider.Provider;
import com.pi4j.provider.exception.ProviderInterfaceException;
import com.pi4j.provider.exception.ProviderNotFoundException;

import java.util.Map;

/**
 * <p>Platform interface.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public interface Platform extends IOCreator, ProviderProvider, Extension<Platform> {

    /**
     * <p>The priority in which platforms must be handled. This will be used to determine which one to use if multiple are provided with the same providers.</p>
     * <ul>
     *     <li>Negative number: used e.g. for a Mock provider</li>
     *     <li>Low number: very unlikely to be used</li>
     *     <li>Higher number: very likely to be used</li>
     * </ul>
     *
     * @return a int.
     */
    int priority();

    /**
     *
     * <p>enabled.</p>
     *
     * @param context a {@link com.pi4j.context.Context} object.
     * @return a boolean.
     */
    boolean enabled(Context context);


    // ------------------------------------------------------------------------
    // PROVIDER ACCESSOR METHODS
    // ------------------------------------------------------------------------

    /**
     * <p>providers.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    Map<IOType, Provider> providers();

    /**
     * {@inheritDoc}
     */
    @Override
    default <T extends Provider> T provider(Class<T> providerClass) throws ProviderNotFoundException, ProviderInterfaceException {
        for (Provider p : providers().values()) {
            if (providerClass.isAssignableFrom(p.getClass()))
                return (T) p;
        }
        if (providerClass.isInterface()) {
            throw new ProviderNotFoundException(providerClass);
        } else {
            throw new ProviderInterfaceException(providerClass);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default <T extends Provider> T provider(IOType ioType) throws ProviderNotFoundException {
        if (providers().containsKey(ioType))
            return (T) providers().get(ioType);
        throw new ProviderNotFoundException(ioType);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    default <T extends Provider> T provider(String providerId, Class<T> providerClass) throws ProviderNotFoundException {
        for (Provider provider : providers().values()) {
            if (provider.id().equalsIgnoreCase(providerId) && providerClass.isInstance(provider)) {
                return (T) provider;
            }
        }
        throw new ProviderNotFoundException(providerId, providerClass);
    }

    /**
     * <p>provider.</p>
     *
     * @param providerId a {@link java.lang.String} object.
     * @param <T>
     * @return
     * @throws ProviderNotFoundException
     */
    default <T extends Provider> T provider(String providerId) throws ProviderNotFoundException {

        // first attempt to resolve by direct unique identifier
        if (providers().containsKey(providerId)) {
            return (T) providers().get(providerId);
        }

        // additionally attempt to resolve the provider by its class name
        try {
            Class providerClass = Class.forName(providerId);
            if (providerClass != null && Provider.class.isAssignableFrom(providerClass)) {
                // iterate over providers looking for a matching class/interface
                for (Provider provider : providers().values()) {
                    if (providerClass.isInstance(provider)) {
                        return (T) provider;
                    }
                }
            }
        } catch (ClassNotFoundException e) {
        }

        // unable to resolve provider by 'id' or class name
        throw new ProviderNotFoundException(providerId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default boolean hasProvider(String providerId) {

        // first attempt to resolve by direct unique identifier
        if (providers().containsKey(providerId)) {
            return true;
        }

        // additionally attempt to resolve the provider by its class name
        try {
            Class providerClass = Class.forName(providerId);
            if (providerClass != null && Provider.class.isAssignableFrom(providerClass)) {
                // iterate over providers looking for a matching class/interface
                for (Provider provider : providers().values()) {
                    if (providerClass.isInstance(provider)) {
                        return true;
                    }
                }
            }
        } catch (ClassNotFoundException e) {
        }

        // unable to resolve provider by 'id' or class name
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default <T extends Provider> boolean hasProvider(Class<T> providerClass) {
        try {
            return provider(providerClass) != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default <T extends Provider> boolean hasProvider(IOType ioType) {
        try {
            return provider(ioType) != null;
        } catch (Exception e) {
            return false;
        }
    }

    // ------------------------------------------------------------------------
    // I/O INSTANCE ACCESSOR/CREATOR METHODS
    // ------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    <I extends IO> I create(IOConfig config, IOType ioType);

    /**
     * {@inheritDoc}
     */
    @Override
    <T extends IO> T create(String id);

    /**
     * {@inheritDoc}
     */
    @Override
    <T extends IO> T create(String id, IOType ioType);

    // ------------------------------------------------------------------------
    // DESCRIPTOR
    // ------------------------------------------------------------------------

    /**
     * <p>describe.</p>
     *
     * @return a {@link com.pi4j.common.Descriptor} object.
     */
    default Descriptor describe() {
        return Descriptor.create()
            .id(this.id())
            .name(this.name())
            .category("PLATFORM")
            .description(this.description()).type(this.getClass());
    }
}

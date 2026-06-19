package com.pi4j.registry;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  Registry.java
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

import com.pi4j.common.Describable;
import com.pi4j.common.Descriptor;
import com.pi4j.io.IO;
import com.pi4j.io.IOType;
import com.pi4j.io.exception.IOInvalidIDException;
import com.pi4j.io.exception.IONotFoundException;
import com.pi4j.provider.Provider;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Read-only view of the runtime registry that tracks every {@link IO} instance created through the Pi4J context.
 * Each I/O instance is registered under its unique string id when created and removed when shut down, so the
 * registry reflects the live set of GPIO, I2C, SPI, PWM and similar I/O currently in use. Use it to look up an
 * existing instance by id, to test for the presence of an id, or to enumerate instances filtered by
 * {@link IOType} or by the {@link Provider} that produced them.
 */
public interface Registry extends Describable {
    /**
     * Tests whether an I/O instance is currently registered under the given id.
     *
     * @param id the unique id assigned to the I/O instance when it was created
     * @return {@code true} if an instance with this id is registered, otherwise {@code false}
     */
    boolean exists(String id);

    /**
     * Tests whether an I/O instance of the given type is registered for the given hardware identifier.
     *
     * @param ioType     the category of I/O to match (for example {@link IOType#DIGITAL_OUTPUT}, {@code I2C} or {@code SPI})
     * @param identifier the hardware address for the device, whose meaning depends on the type: BCM pin for GPIO,
     *                   device address for I2C, or channel number for PWM and SPI
     * @return {@code true} if a matching instance is registered, otherwise {@code false}
     */
    boolean exists(IOType ioType, int identifier);

    /**
     * Returns all currently registered I/O instances keyed by their unique id.
     *
     * @return a map of I/O id to I/O instance for every registered instance
     */
    Map<String, ? extends IO> all();

    /**
     * Returns the registered I/O instance for the given id, cast to the caller's expected type.
     *
     * @param <T> the expected {@link IO} subtype of the returned instance
     * @param id  the unique id of the I/O instance to retrieve
     * @return the registered I/O instance associated with the id
     * @throws IOInvalidIDException if the supplied id is {@code null} or otherwise not a valid identifier
     * @throws IONotFoundException  if no I/O instance is registered under the given id
     */
    <T extends IO> T get(String id) throws IOInvalidIDException, IONotFoundException;

    /**
     * Returns the registered I/O instance for the given id, verifying that it is assignable to the given type.
     *
     * @param <T>  the expected {@link IO} subtype of the returned instance
     * @param id   the unique id of the I/O instance to retrieve
     * @param type the I/O class the instance is expected to be an instance of
     * @return the registered I/O instance associated with the id
     * @throws IOInvalidIDException if the supplied id is {@code null} or otherwise not a valid identifier
     * @throws IONotFoundException  if no I/O instance is registered under the given id, or it is not of the requested type
     */
    <T extends IO> T get(String id, Class<T> type) throws IOInvalidIDException, IONotFoundException;

    /**
     * Returns all registered I/O instances that are assignable to the given I/O class.
     *
     * @param <T>     the {@link IO} subtype used to filter and type the result
     * @param ioClass the I/O class to match instances against
     * @return an unmodifiable map of I/O id to matching instance
     */
    default <T extends IO> Map<String, T> allByType(Class<T> ioClass) {
        // create a map <io-id, io-instance> of I/O instances that extend of the given IO class
        var result = new ConcurrentHashMap<String, T>();
        this.all().values().stream().filter(ioClass::isInstance).forEach(p -> {
            result.put(p.id(), ioClass.cast(p));
        });
        return Collections.unmodifiableMap(result);
    }

    /**
     * Returns all registered I/O instances belonging to the given {@link IOType} category.
     *
     * @param <P>    the {@link Provider} type parameter (unused; retained for binary compatibility)
     * @param ioType the I/O category to match
     * @return an unmodifiable map of I/O id to matching instance
     */
    default <P extends Provider> Map<String, ? extends IO> allByIoType(IOType ioType) {
        return allByType(ioType.getIOClass());
    }

    /**
     * Returns all registered I/O instances produced by the I/O type associated with the given provider class.
     *
     * @param <P>           the {@link Provider} subtype
     * @param providerClass the provider class whose I/O type is used to select instances
     * @return an unmodifiable map of I/O id to matching instance
     */
    default <P extends Provider> Map<String, ? extends IO> allByProvider(Class<P> providerClass) {
        return allByIoType(IOType.getByProviderClass(providerClass));
    }

    /**
     * Returns all registered I/O instances that were created by the provider with the given id.
     *
     * @param <P>        the {@link Provider} type parameter (unused; retained for binary compatibility)
     * @param providerId the id of the provider to match, compared case-insensitively
     * @return an unmodifiable map of I/O id to matching instance
     */
    default <P extends Provider> Map<String, ? extends IO> allByProvider(String providerId) {

        // create a map <io-id, io-instance> of providers that extend of the given io class
        var result = this.all().values().stream()
            .filter(instance -> providerId.equalsIgnoreCase(((IO) instance).provider().id()))
            .collect(Collectors.toMap(IO::id, c -> c));

        return Collections.unmodifiableMap(result);
    }

    /**
     * Returns all registered I/O instances that were created by the provider with the given id and are also
     * assignable to the given I/O class.
     *
     * @param <P>        the {@link Provider} type parameter (unused; retained for binary compatibility)
     * @param <T>        the {@link IO} subtype used to filter and type the result
     * @param providerId the id of the provider to match, compared case-insensitively
     * @param ioClass    the I/O class to match instances against
     * @return an unmodifiable map of I/O id to matching instance
     */
    default <P extends Provider, T extends IO> Map<String, T> allByProvider(String providerId, Class<T> ioClass) {
        // create a map <io-id, io-instance> of providers that extend of the given io class
        var result = new ConcurrentHashMap<String, T>();
        this.all().values().stream()
            .filter(instance -> providerId.equalsIgnoreCase(((IO) instance).provider().id()))
            .filter(ioClass::isInstance).forEach(p -> {
                result.put(p.id(), ioClass.cast(p));
            });
        return Collections.unmodifiableMap(result);
    }

    default Descriptor describe() {

        Map<String, ? extends IO> instances = all();
        Descriptor descriptor = Descriptor.create()
            .category("REGISTRY")
            .name("I/O Registered Instances")
            .quantity((instances == null) ? 0 : instances.size())
            .type(this.getClass());

        if (instances != null && !instances.isEmpty()) {
            instances.forEach((id, instance) -> {
                descriptor.add(instance.describe());
            });
        }

        return descriptor;
    }
}

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
import com.pi4j.io.exception.IOAlreadyExistsException;
import com.pi4j.io.exception.IOInvalidIDException;
import com.pi4j.io.exception.IONotFoundException;
import com.pi4j.io.exception.IOShutdownException;
import com.pi4j.provider.Provider;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * <p>Registry interface.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public interface Registry extends Describable {
    /**
     * <p>exists.</p>
     *
     * @param id a {@link java.lang.String} object.
     * @return a boolean.
     */
    boolean exists(String id);

    /**
     * <p>exists.</p>
     *
     * @param ioType  a {@link com.pi4j.io.IOType} object.
     * @param address an int.
     *
     * @return a boolean.
     */
    boolean exists(IOType ioType, int address);

    /**
     * <p>all.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    Map<String, ? extends IO> all();

    /**
     * <p>get.</p>
     *
     * @param id a {@link java.lang.String} object.
     * @param <T> a T object.
     * @return a T object.
     * @throws com.pi4j.io.exception.IOInvalidIDException if any.
     * @throws com.pi4j.io.exception.IONotFoundException if any.
     */
    <T extends IO> T get(String id) throws IOInvalidIDException, IONotFoundException;
    /**
     * <p>get.</p>
     *
     * @param id a {@link java.lang.String} object.
     * @param type a {@link java.lang.Class} object.
     * @param <T> a T object.
     * @return a T object.
     * @throws com.pi4j.io.exception.IOInvalidIDException if any.
     * @throws com.pi4j.io.exception.IONotFoundException if any.
     */
    <T extends IO> T get(String id, Class<T> type) throws IOInvalidIDException, IONotFoundException;

    /**
     * <p>allByType.</p>
     *
     * @param ioClass a {@link java.lang.Class} object.
     * @param <T> a T object.
     * @return a {@link java.util.Map} object.
     */
    default <T extends IO> Map<String, T> allByType(Class<T> ioClass){
        // create a map <io-id, io-instance> of I/O instances that extend of the given IO class
        var result = new ConcurrentHashMap<String, T>();
        this.all().values().stream().filter(ioClass::isInstance).forEach(p -> {
            result.put(p.id(), ioClass.cast(p));
        });
        return Collections.unmodifiableMap(result);
    }

    /**
     * <p>allByIoType.</p>
     *
     * @param ioType a {@link com.pi4j.io.IOType} object.
     * @param <P> a P object.
     * @return a {@link java.util.Map} object.
     */
    default <P extends Provider> Map<String, ? extends IO> allByIoType(IOType ioType){
        return allByType(ioType.getIOClass());
    }

    /**
     * <p>allByProvider.</p>
     *
     * @param providerClass a {@link java.lang.Class} object.
     * @param <P> a P object.
     * @return a {@link java.util.Map} object.
     */
    default <P extends Provider> Map<String, ? extends IO> allByProvider(Class<P> providerClass){
        return allByIoType(IOType.getByProviderClass(providerClass));
    }

    /**
     * <p>allByProvider.</p>
     *
     * @param providerId a {@link java.lang.String} object.
     * @param <P> a P object.
     * @return a {@link java.util.Map} object.
     */
    default <P extends Provider> Map<String, ? extends IO> allByProvider(String providerId){

        // create a map <io-id, io-instance> of providers that extend of the given io class
        var result = this.all().values().stream()
                .filter(instance -> providerId.equalsIgnoreCase(((IO) instance).provider().id()))
                .collect(Collectors.toMap(IO::id, c->c));

        return Collections.unmodifiableMap(result);
    }

    /**
     * <p>allByProvider.</p>
     *
     * @param providerId a {@link java.lang.String} object.
     * @param ioClass a {@link java.lang.Class} object.
     * @param <P> a P object.
     * @param <T> a T object.
     * @return a {@link java.util.Map} object.
     */
    default <P extends Provider, T extends IO> Map<String, T> allByProvider(String providerId, Class<T> ioClass){
        // create a map <io-id, io-instance> of providers that extend of the given io class
        var result = new ConcurrentHashMap<String, T>();
        this.all().values().stream()
                .filter(instance -> providerId.equalsIgnoreCase(((IO) instance).provider().id()))
                .filter(ioClass::isInstance).forEach(p -> {
            result.put(p.id(), ioClass.cast(p));
        });
        return Collections.unmodifiableMap(result);
    }

    /**
     * <p>add.</p>
     *
     * @param instance a {@link com.pi4j.io.IO} object.
     * @return this
     * @throws com.pi4j.io.exception.IOAlreadyExistsException if any.
     * @throws com.pi4j.io.exception.IOInvalidIDException if any.
     */
    Registry add(IO instance) throws IOAlreadyExistsException, IOInvalidIDException;
    /**
     * <p>remove.</p>
     *
     * @param id a {@link java.lang.String} object.
     * @param <T> a T object.
     * @return a T object.
     * @throws com.pi4j.io.exception.IONotFoundException if any.
     * @throws com.pi4j.io.exception.IOInvalidIDException if any.
     * @throws com.pi4j.io.exception.IOShutdownException if any.
     */
    <T extends IO> T remove(String id) throws IONotFoundException, IOInvalidIDException, IOShutdownException;

    /**
     * <p>describe.</p>
     *
     * @return a {@link com.pi4j.common.Descriptor} object.
     */
    default Descriptor describe() {

        Map<String, ? extends IO> instances = all();
        Descriptor descriptor = Descriptor.create()
                .category("REGISTRY")
                .name("I/O Registered Instances")
                .quantity((instances == null) ? 0 : instances.size())
                .type(this.getClass());

        if(instances != null && !instances.isEmpty()) {
            instances.forEach((id, instance) -> {
                descriptor.add(instance.describe());
            });
        }

        return descriptor;
    }
}

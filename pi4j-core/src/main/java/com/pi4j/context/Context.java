package com.pi4j.context;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  Context.java
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

import com.pi4j.boardinfo.definition.BoardModel;
import com.pi4j.boardinfo.model.BoardInfo;
import com.pi4j.boardinfo.model.JavaInfo;
import com.pi4j.boardinfo.model.OperatingSystem;
import com.pi4j.common.Describable;
import com.pi4j.common.Descriptor;
import com.pi4j.event.InitializedEventProducer;
import com.pi4j.event.ShutdownEventProducer;
import com.pi4j.exception.ShutdownException;
import com.pi4j.internal.IOCreator;
import com.pi4j.internal.ProviderProvider;
import com.pi4j.io.IO;
import com.pi4j.io.IOConfig;
import com.pi4j.io.IOType;
import com.pi4j.io.exception.IOException;
import com.pi4j.io.exception.IOInvalidIDException;
import com.pi4j.io.exception.IONotFoundException;
import com.pi4j.io.exception.IOShutdownException;
import com.pi4j.provider.Provider;
import com.pi4j.provider.Providers;
import com.pi4j.provider.exception.ProviderInterfaceException;
import com.pi4j.provider.exception.ProviderNotFoundException;
import com.pi4j.registry.Registry;
import com.pi4j.util.StringUtil;

import java.util.concurrent.Future;

/**
 * <p>Context interface.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public interface Context extends Describable, IOCreator, ProviderProvider, InitializedEventProducer<Context>,
    ShutdownEventProducer<Context> {

    /**
     * <p>config.</p>
     *
     * @return a {@link com.pi4j.context.ContextConfig} object.
     */
    ContextConfig config();

    /**
     * <p>providers.</p>
     *
     * @return a {@link com.pi4j.provider.Providers} object.
     */
    Providers providers();

    /**
     * <p>registry.</p>
     *
     * @return a {@link com.pi4j.registry.Registry} object.
     */
    Registry registry();

    /**
     * Submits the given task for async execution
     *
     * @param task the task to execute asynchronously
     * @return the task to cancel later
     */
    Future<?> submitTask(Runnable task);

    /**
     * <p>shutdown.</p>
     *
     * @return a {@link com.pi4j.context.Context} object.
     * @throws com.pi4j.exception.ShutdownException if an error occurs during shutdown.
     */
    Context shutdown() throws ShutdownException;

    /**
     * @return {@link Future} of {@link Context}
     */
    Future<Context> asyncShutdown();

    /**
     * @return Flag indicating if the context has been shutdown
     */
    boolean isShutdown();

    // ------------------------------------------------------------------------
    // PROVIDER ACCESSOR METHODS
    // ------------------------------------------------------------------------

    /**
     * <p>provider.</p>
     *
     * @param providerId a {@link java.lang.String} object.
     * @param <T>
     * @return
     * @throws ProviderNotFoundException
     */
    default <T extends Provider> T provider(String providerId) throws ProviderNotFoundException {
        return (T) providers().get(providerId);
    }

    /**
     * <p>provider.</p>
     *
     * @param providerId    a {@link java.lang.String} object.
     * @param providerClass a T object.
     * @param <T>
     * @return
     * @throws ProviderNotFoundException
     */
    default <T extends Provider> T provider(String providerId, Class<T> providerClass)
        throws ProviderNotFoundException {
        return (T) providers().get(providerId);
    }


    /**
     * <p>Has providers.</p>
     *
     * @param providerId a {@link java.lang.String} object.
     * @return
     */
    default boolean hasProvider(String providerId) {
        try {
            return providers().exists(providerId);
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * <p>Has providers.</p>
     *
     * @param ioType a {@link com.pi4j.io.IOType} object.
     * @param <T>
     * @return
     */
    default <T extends Provider> boolean hasProvider(IOType ioType) {
        return providers().exists(ioType);
    }

    /**
     * <p>Has providers.</p>
     *
     * @param providerClass a {@link java.lang.Class} object.
     * @param <T>
     * @return
     */
    default <T extends Provider> boolean hasProvider(Class<T> providerClass) {
        return providers().exists(providerClass);
    }


    /**
     * <p>provider.</p>
     *
     * @param providerClass a {@link java.lang.Class} object.
     * @param <T>
     * @return
     * @throws ProviderNotFoundException
     * @throws ProviderInterfaceException
     */
    default <T extends Provider> T provider(Class<T> providerClass)
        throws ProviderNotFoundException, ProviderInterfaceException {

        // return the default provider for this type (outside of default platform)
        if (providers().exists(providerClass))
            return providers().get(providerClass);

        // provider not found
        throw new ProviderNotFoundException(providerClass);
    }


    /**
     * <p>provider.</p>
     *
     * @param ioType a {@link com.pi4j.io.IOType} object.
     * @param <T>
     * @return
     * @throws ProviderNotFoundException
     */
    default <T extends Provider> T provider(IOType ioType) throws ProviderNotFoundException {
        // return the default provider for this type (outside of default platform)
        if (providers().exists(ioType))
            return providers().get(ioType);

        // provider not found
        throw new ProviderNotFoundException(ioType);
    }

    // ------------------------------------------------------------------------
    // BOARD INFO ACCESSOR METHODS
    // ------------------------------------------------------------------------

    /**
     * Return the BoardInfo containing more info about the
     * {@link BoardModel}, {@link OperatingSystem}, and {@link JavaInfo}.
     *
     * @return {@link BoardInfo}
     */
    BoardInfo boardInfo();

    // ------------------------------------------------------------------------
    // I/O INSTANCE ACCESSOR/CREATOR METHODS
    // ------------------------------------------------------------------------

    default <I extends IO> I create(IOConfig config, IOType ioType) {
        // create by explicitly configured IO <PROVIDER> from IO config
        String providerId = config.provider();
        if (StringUtil.isNotNullOrEmpty(providerId)) {
            // resolve the provider and use it to create the IO instance
            Provider provider = this.providers().get(providerId, ioType);
            return (I) provider.create(config);
        }

        // get implicitly defined provider (defined by IO type)
        // (this is the default or platform defined provider for this particular IO type)
        if (ioType != null) {
            // resolve the provider and use it to create the IO instance
            Provider provider = this.provider(ioType);
            return (I) provider.create(config);
        }

        // unable to resolve the IO type and thus unable to create I/O instance
        throw new IOException("This IO instance [" + config.id() + "] could not be created because it does not define one of the following: 'PLATFORM', 'PROVIDER', or 'I/O TYPE'.");
    }

    /**
     * shutdown and unregister a created IO.
     *
     * @param <T> the IO Type
     * @param id  the IO id
     * @return the IO which was shutdown or null if it wasn't created/registered
     * @throws IONotFoundException  if the IO was not registered
     * @throws IOInvalidIDException if the ID is invalid
     * @throws IOShutdownException  if an error occured while shuting down the IO
     */
    <T extends IO> T shutdown(String id) throws IOInvalidIDException, IONotFoundException, IOShutdownException;

    /**
     * shutdown and unregister a created IO.
     *
     * @param <T>      the IO Type
     * @param instance the IO to shutdown and unregister
     * @throws IONotFoundException  if the IO was not registered
     * @throws IOInvalidIDException if the ID is invalid
     * @throws IOShutdownException  if an error occured while shuting down the IO
     */
    <T extends IO> void shutdown(T instance) throws IOInvalidIDException, IONotFoundException, IOShutdownException;

    // ------------------------------------------------------------------------
    // I/O INSTANCE ACCESSORS
    // ------------------------------------------------------------------------

    default boolean hasIO(String id) throws IOInvalidIDException, IONotFoundException {
        return registry().exists(id);
    }

    default <T extends IO> T io(String id) throws IOInvalidIDException, IONotFoundException {
        return registry().get(id);
    }

    default <T extends IO> T io(String id, Class<T> ioClass) throws IOInvalidIDException, IONotFoundException {
        return registry().get(id, ioClass);
    }

    default <T extends IO> T getIO(String id) throws IOInvalidIDException, IONotFoundException {
        return io(id);
    }

    default <T extends IO> T getIO(String id, Class<T> ioClass) throws IOInvalidIDException, IONotFoundException {
        return io(id, ioClass);
    }

    // ------------------------------------------------------------------------
    // DESCRIPTOR
    // ------------------------------------------------------------------------

    /**
     * <p>describe.</p>
     *
     * @return a {@link com.pi4j.common.Descriptor} object.
     */
    default Descriptor describe() {
        Descriptor descriptor = Descriptor.create().category("CONTEXT").name("Runtime Context").type(this.getClass());

        descriptor.add(registry().describe());
        descriptor.add(providers().describe());
        return descriptor;
    }

    /** Registers an IO instance, bypassing create method. Used in testing */
    void register(IO instance);
}

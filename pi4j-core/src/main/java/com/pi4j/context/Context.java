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
 * Central runtime state of a Pi4J application. The {@code Context} is created once (typically via
 * {@code Pi4J.newContext()} or a {@link ContextBuilder}) and owns the {@link Registry} of created I/O
 * instances, the set of available {@link Providers}, and the immutable {@link ContextConfig} that was
 * used to build it. Callers use it to look up providers, create and access I/O instances, query board
 * information, and to shut everything down cleanly.
 *
 * @see ContextBuilder
 * @see ContextConfig
 */
public interface Context extends Describable, IOCreator, ProviderProvider, InitializedEventProducer<Context>,
    ShutdownEventProducer<Context> {

    /**
     * Returns the immutable configuration that this context was created from, capturing auto-detection
     * settings, the default platform, registered providers and user properties.
     *
     * @return the {@link ContextConfig} backing this context
     */
    ContextConfig config();

    /**
     * Returns the collection of providers available in this context, used to resolve a {@link Provider}
     * for a given I/O type or provider id when creating I/O instances.
     *
     * @return the {@link Providers} repository for this context
     */
    Providers providers();

    /**
     * Returns the registry tracking every I/O instance that has been created and registered within this
     * context, keyed by its unique id.
     *
     * @return the {@link Registry} for this context
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
     * Shuts down this context synchronously, shutting down and unregistering all I/O instances and
     * releasing the runtime resources held by its providers and registry.
     *
     * @return this context instance, now in the shutdown state
     * @throws com.pi4j.exception.ShutdownException if an error occurs during shutdown.
     */
    Context shutdown() throws ShutdownException;

    /**
     * Initiates an asynchronous shutdown of this context, performing the same work as {@link #shutdown()}
     * on a background task.
     *
     * @return a {@link Future} that completes with this context once shutdown has finished
     */
    Future<Context> asyncShutdown();

    /**
     * Indicates whether this context has already been shut down and can no longer be used to create or
     * access I/O instances.
     *
     * @return {@code true} if the context has been shut down, {@code false} otherwise
     */
    boolean isShutdown();

    // ------------------------------------------------------------------------
    // PROVIDER ACCESSOR METHODS
    // ------------------------------------------------------------------------

    /**
     * Returns the provider registered under the given id.
     *
     * @param <T>        the expected {@link Provider} subtype
     * @param providerId the unique id of the provider to look up
     * @return the matching provider instance
     * @throws ProviderNotFoundException if no provider is registered under the given id
     */
    default <T extends Provider> T provider(String providerId) throws ProviderNotFoundException {
        return (T) providers().get(providerId);
    }

    /**
     * Returns the provider registered under the given id, with the expected provider class supplied to
     * drive the generic return type.
     *
     * @param <T>           the expected {@link Provider} subtype
     * @param providerId    the unique id of the provider to look up
     * @param providerClass the expected provider class
     * @return the matching provider instance
     * @throws ProviderNotFoundException if no provider is registered under the given id
     */
    default <T extends Provider> T provider(String providerId, Class<T> providerClass)
        throws ProviderNotFoundException {
        return (T) providers().get(providerId);
    }


    /**
     * Indicates whether a provider is registered under the given id.
     *
     * @param providerId the provider id to test for
     * @return {@code true} if a provider with this id exists, {@code false} otherwise
     */
    default boolean hasProvider(String providerId) {
        try {
            return providers().exists(providerId);
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * Indicates whether at least one provider is registered for the given I/O type.
     *
     * @param <T>    the {@link Provider} subtype
     * @param ioType the {@link IOType} to test for
     * @return {@code true} if a provider for this I/O type exists, {@code false} otherwise
     */
    default <T extends Provider> boolean hasProvider(IOType ioType) {
        return providers().exists(ioType);
    }

    /**
     * Indicates whether a provider assignable to the given provider class is registered.
     *
     * @param <T>           the {@link Provider} subtype
     * @param providerClass the provider class to test for
     * @return {@code true} if a matching provider exists, {@code false} otherwise
     */
    default <T extends Provider> boolean hasProvider(Class<T> providerClass) {
        return providers().exists(providerClass);
    }


    /**
     * Returns the default provider matching the given provider class.
     *
     * @param <T>           the expected {@link Provider} subtype
     * @param providerClass the provider class to resolve
     * @return the matching provider instance
     * @throws ProviderNotFoundException  if no provider matching the class is registered
     * @throws ProviderInterfaceException if the resolved provider does not implement the expected interface
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
     * Returns the default provider for the given I/O type, used to create I/O instances when no explicit
     * provider id is configured.
     *
     * @param <T>    the expected {@link Provider} subtype
     * @param ioType the {@link IOType} to resolve a provider for
     * @return the default provider for this I/O type
     * @throws ProviderNotFoundException if no provider for this I/O type is registered
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
     * Returns information about the board and runtime environment Pi4J is executing on, including the
     * detected {@link BoardModel}, {@link OperatingSystem}, and {@link JavaInfo}.
     *
     * @return the {@link BoardInfo} describing the current board and environment
     */
    BoardInfo boardInfo();

    // ------------------------------------------------------------------------
    // I/O INSTANCE ACCESSOR/CREATOR METHODS
    // ------------------------------------------------------------------------

    @Override
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

    /**
     * Indicates whether an I/O instance with the given id has been created and registered in this context.
     *
     * @param id the unique id of the I/O instance to test for
     * @return {@code true} if an I/O instance with this id is registered, {@code false} otherwise
     * @throws IOInvalidIDException if the id is invalid
     * @throws IONotFoundException  if the id cannot be resolved
     */
    default boolean hasIO(String id) throws IOInvalidIDException, IONotFoundException {
        return registry().exists(id);
    }

    /**
     * Returns the previously created I/O instance registered under the given id.
     *
     * @param <T> the expected {@link IO} subtype
     * @param id  the unique id of the I/O instance to retrieve
     * @return the registered I/O instance
     * @throws IOInvalidIDException if the id is invalid
     * @throws IONotFoundException  if no I/O instance with this id is registered
     */
    default <T extends IO> T io(String id) throws IOInvalidIDException, IONotFoundException {
        return registry().get(id);
    }

    /**
     * Returns the previously created I/O instance registered under the given id, cast to the supplied
     * I/O class.
     *
     * @param <T>     the expected {@link IO} subtype
     * @param id      the unique id of the I/O instance to retrieve
     * @param ioClass the expected I/O class
     * @return the registered I/O instance
     * @throws IOInvalidIDException if the id is invalid
     * @throws IONotFoundException  if no I/O instance with this id is registered
     */
    default <T extends IO> T io(String id, Class<T> ioClass) throws IOInvalidIDException, IONotFoundException {
        return registry().get(id, ioClass);
    }

    /**
     * Returns the previously created I/O instance registered under the given id. Alias for {@link #io(String)}.
     *
     * @param <T> the expected {@link IO} subtype
     * @param id  the unique id of the I/O instance to retrieve
     * @return the registered I/O instance
     * @throws IOInvalidIDException if the id is invalid
     * @throws IONotFoundException  if no I/O instance with this id is registered
     */
    default <T extends IO> T getIO(String id) throws IOInvalidIDException, IONotFoundException {
        return io(id);
    }

    /**
     * Returns the previously created I/O instance registered under the given id, cast to the supplied
     * I/O class. Alias for {@link #io(String, Class)}.
     *
     * @param <T>     the expected {@link IO} subtype
     * @param id      the unique id of the I/O instance to retrieve
     * @param ioClass the expected I/O class
     * @return the registered I/O instance
     * @throws IOInvalidIDException if the id is invalid
     * @throws IONotFoundException  if no I/O instance with this id is registered
     */
    default <T extends IO> T getIO(String id, Class<T> ioClass) throws IOInvalidIDException, IONotFoundException {
        return io(id, ioClass);
    }

    // ------------------------------------------------------------------------
    // DESCRIPTOR
    // ------------------------------------------------------------------------

    default Descriptor describe() {
        Descriptor descriptor = Descriptor.create().category("CONTEXT").name("Runtime Context").type(this.getClass());

        descriptor.add(registry().describe());
        descriptor.add(providers().describe());
        return descriptor;
    }

    /**
     * Registers an already-constructed I/O instance directly in the {@link Registry}, bypassing the normal
     * provider-based {@link #create(IOConfig, IOType)} flow. Intended primarily for testing.
     *
     * @param instance the I/O instance to register
     */
    void register(IO instance);
}

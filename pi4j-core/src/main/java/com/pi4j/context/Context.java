package com.pi4j.context;

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
import com.pi4j.io.exception.IOInvalidIDException;
import com.pi4j.io.exception.IONotFoundException;
import com.pi4j.io.exception.IOShutdownException;
import com.pi4j.registry.Registry;


import java.util.concurrent.Future;

/**
 * Central runtime state of a Pi4J application. The {@code Context} is created once (typically via
 * {@code Pi4J.newContext()} or a {@link ContextBuilder}) and owns the {@link Registry} of created I/O
 * instances and the immutable {@link ContextConfig} that was
 * used to build it. Callers use it create and access I/O instances, query board
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

package com.pi4j.context.impl;

import com.pi4j.context.Context;
import com.pi4j.exception.InitializeException;
import com.pi4j.exception.LifecycleException;
import com.pi4j.io.IO;
import com.pi4j.io.IOType;
import com.pi4j.io.exception.*;
import com.pi4j.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A mutable implementation of the Registry interface, managed by the Runtime.
 */
public class MutableRegistry implements Registry {

    private static final Logger logger = LoggerFactory.getLogger(MutableRegistry.class);
    private final Map<String, IO> instances;
    private final Map<IOType, Set<Integer>> usedAddressesByIoType;
    private final Context context;

    MutableRegistry(Context context) {
        this.context = context;
        this.instances = new HashMap<>();
        this.usedAddressesByIoType = new HashMap<>();
    }

    synchronized void register(IO instance) throws IOInvalidIDException, IOAlreadyExistsException {

        // Validate target I/O instance id
        String id = validateId(instance.id());

        // First test to make sure this id does not already exist in the registry
        if (instances.containsKey(id)) {
            throw new IOAlreadyExistsException(id);
        }

        // Second check by IO Type and the unique identifier
        if (exists(instance.type(), instance.config().getUniqueIdentifier())) {
            throw new IOAlreadyExistsException(instance.config().getUniqueIdentifier());
        }
        Set<Integer> usedAddresses = this.usedAddressesByIoType.computeIfAbsent(instance.type(), _ -> new HashSet<>());
        usedAddresses.add(instance.config().getUniqueIdentifier());

        // Add the instance to the collection
        try {
            instance.initialize(context);
            instances.put(id, instance);
        } catch (InitializeException e) {
            removeFromMap(instance);
            throw new IllegalStateException("Failed to initialize IO " + instance.getId(), e);
        }
    }

    @Override
    public synchronized <T extends IO> T get(String id, Class<T> type)
        throws IOInvalidIDException, IONotFoundException {
        String _id = validateId(id);

        // first test to make sure this id is included in the registry
        if (!instances.containsKey(_id))
            throw new IONotFoundException(_id);
        return (T) instances.get(_id);
    }

    @Override
    public synchronized <T extends IO> T get(String id) throws IOInvalidIDException, IONotFoundException {
        String _id = validateId(id);

        // first test to make sure this id is included in the registry
        if (!instances.containsKey(_id))
            throw new IONotFoundException(_id);

        return (T) instances.get(_id);
    }

    synchronized <T extends IO> T shutdown(String id)
        throws IONotFoundException, IOInvalidIDException, IOShutdownException {
        String _id = validateId(id);

        // first test to make sure this id is included in the registry
        if (!exists(_id))
            throw new IONotFoundException(_id);

        IO shutdownInstance = instances.get(_id);
        shutdown(shutdownInstance);

        // return the shutdown I/O provider instances
        return (T) shutdownInstance;
    }

    <T extends IO> T shutdown(T instance)
        throws IONotFoundException, IOInvalidIDException, IOShutdownException {
        if (instance == null)
            throw new IllegalArgumentException("An IO instance cannot be NULL.");

        // shutdown instance
        try {
            long start = System.currentTimeMillis();

            instance.shutdownInternal(context);
            long took = System.currentTimeMillis() - start;
            if (took > 10) {
                logger.info("Shutting down of IO {} took {}ms", instance.getId(), took);
            }
        } catch (LifecycleException e) {
            logger.error(e.getMessage(), e);
            throw new IOShutdownException(instance, e);
        }

        // remove the shutdown instance from the registry
        removeFromMap(instance);

        this.instances.remove(instance.id());
        return instance;
    }

    private <T extends IO> void removeFromMap(T instance) {
        Set<Integer> usedAddresses = this.usedAddressesByIoType.get(instance.type());
        if (usedAddresses == null)
            return;
        usedAddresses.remove(instance.config().getUniqueIdentifier());
        if (usedAddresses.isEmpty())
            this.usedAddressesByIoType.remove(instance.type());
    }

    @Override
    public synchronized boolean exists(String id) {
        String _id = null;
        try {
            _id = validateId(id);
            // return 'false' if the requested ID is not found
            // return 'true' if the requested ID is found
            return instances.containsKey(_id);
        } catch (IOInvalidIDException e) {
            return false;
        }
    }

    @Override
    public synchronized boolean exists(IOType ioType, int identifier) {
        Set<Integer> usedAddresses = this.usedAddressesByIoType.get(ioType);
        return usedAddresses != null && usedAddresses.contains(identifier);
    }

    @Override
    public synchronized Map<String, ? extends IO> all() {
        return new HashMap<>(this.instances);
    }

    private String validateId(String id) throws IOInvalidIDException {
        if (id == null)
            throw new IOInvalidIDException();
        String validatedId = id.trim();
        if (validatedId.isEmpty())
            throw new IOInvalidIDException();
        return validatedId;
    }

    synchronized void shutdown() {
        all().values().forEach(instance -> {
            try {
                shutdown(instance.id());
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        });
    }
}

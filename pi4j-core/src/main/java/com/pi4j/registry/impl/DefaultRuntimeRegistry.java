package com.pi4j.registry.impl;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  DefaultRuntimeRegistry.java
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

import com.pi4j.config.BcmConfig;
import com.pi4j.exception.InitializeException;
import com.pi4j.exception.LifecycleException;
import com.pi4j.io.IO;
import com.pi4j.io.IOType;
import com.pi4j.io.exception.*;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.pwm.PwmConfig;
import com.pi4j.io.spi.SpiConfig;
import com.pi4j.runtime.Runtime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * <p>DefaultRuntimeRegistry class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public class DefaultRuntimeRegistry implements RuntimeRegistry {

    private static final Logger logger = LoggerFactory.getLogger(DefaultRuntimeRegistry.class);
    private Runtime runtime;
    private final Map<String, IO> instances;
    private final Map<IOType, Set<Integer>> usedAddressesByIoType;

    // static singleton instance

    /**
     * <p>newInstance.</p>
     *
     * @param runtime a {@link com.pi4j.runtime.Runtime} object.
     * @return a {@link com.pi4j.registry.impl.RuntimeRegistry} object.
     */
    public static RuntimeRegistry newInstance(Runtime runtime) {
        return new DefaultRuntimeRegistry(runtime);
    }

    // private constructor
    private DefaultRuntimeRegistry(Runtime runtime) {
        // set local runtime reference
        this.instances = new HashMap<>();
        this.usedAddressesByIoType = new HashMap<>();
        this.runtime = runtime;
    }

    @Override
    public synchronized RuntimeRegistry add(IO instance) throws IOInvalidIDException, IOAlreadyExistsException {

        // validate target I/O instance id
        String _id = validateId(instance.id());

        // first test to make sure this id does not already exist in the registry
        if (instances.containsKey(_id)) {
            throw new IOAlreadyExistsException(_id);
        }

        switch (instance.config()) {
            case BcmConfig<?> addressConfig: {
                if (exists(instance.type(), addressConfig.bcm())) {
                    throw new IOAlreadyExistsException(addressConfig.bcm());
                }
                Set<Integer> usedAddresses = this.usedAddressesByIoType.computeIfAbsent(instance.type(),
                    k -> new HashSet<>());
                usedAddresses.add(addressConfig.bcm());
                break;
            }
            case PwmConfig pwmConfig: {
                if (exists(instance.type(), pwmConfig.channel())) {
                    throw new IOAlreadyExistsException(pwmConfig.channel());
                }
                Set<Integer> usedAddresses = this.usedAddressesByIoType.computeIfAbsent(instance.type(),
                    k -> new HashSet<>());
                usedAddresses.add(pwmConfig.channel());
                break;
            }
            case I2CConfig i2cConfig: {
                if (exists(instance.type(), i2cConfig.getIdentifier())) {
                    throw new IOAlreadyExistsException("Bus " + i2cConfig.bus() + ", Device " + i2cConfig.device());
                }
                Set<Integer> usedAddresses = this.usedAddressesByIoType.computeIfAbsent(instance.type(),
                    k -> new HashSet<>());
                usedAddresses.add(i2cConfig.getIdentifier());
                break;
            }
            case SpiConfig spiConfig: {
                if (exists(instance.type(), spiConfig.getIdentifier())) {
                    throw new IOAlreadyExistsException("Bus " + spiConfig.bus() + ", Channel " + spiConfig.channel());
                }
                Set<Integer> usedAddresses = this.usedAddressesByIoType.computeIfAbsent(instance.type(),
                    k -> new HashSet<>());
                usedAddresses.add(spiConfig.getIdentifier());
                break;
            }
            default: {
            }
        }

        // add the instance to the collection
        try {
            instance.initialize(this.runtime.context());
            instances.put(_id, instance);
        } catch (InitializeException e) {
            removeFromMap(instance);
            throw new IllegalStateException("Failed to initialize IO " + instance.getId(), e);
        }

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized <T extends IO> T get(String id, Class<T> type)
        throws IOInvalidIDException, IONotFoundException {
        String _id = validateId(id);

        // first test to make sure this id is included in the registry
        if (!instances.containsKey(_id))
            throw new IONotFoundException(_id);
        return (T) instances.get(_id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized <T extends IO> T get(String id) throws IOInvalidIDException, IONotFoundException {
        String _id = validateId(id);

        // first test to make sure this id is included in the registry
        if (!instances.containsKey(_id))
            throw new IONotFoundException(_id);

        return (T) instances.get(_id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized <T extends IO> T remove(String id)
        throws IONotFoundException, IOInvalidIDException, IOShutdownException {
        String _id = validateId(id);

        // first test to make sure this id is included in the registry
        if (!exists(_id))
            throw new IONotFoundException(_id);

        IO shutdownInstance = instances.get(_id);
        remove(shutdownInstance);

        // return the shutdown I/O provider instances
        return (T) shutdownInstance;
    }

    @Override
    public <T extends IO> void remove(T instance)
        throws IONotFoundException, IOInvalidIDException, IOShutdownException {
        if (instance == null)
            throw new IllegalArgumentException("An IO instance cannot be NULL.");

        // shutdown instance
        try {
            long start = System.currentTimeMillis();

            instance.shutdownInternal(runtime.context());
            long took = System.currentTimeMillis() - start;
            if (took > 10)
                logger.info("Shutting down of IO {} took {}ms", instance.getId(), took);
        } catch (LifecycleException e) {
            logger.error(e.getMessage(), e);
            throw new IOShutdownException(instance, e);
        }

        // remove the shutdown instance from the registry
        removeFromMap(instance);

        this.instances.remove(instance.id());
    }

    private <T extends IO> void removeFromMap(T instance) {
        if (!(instance.config() instanceof BcmConfig<?> bcmConfig))
            return;
        Set<Integer> usedAddresses = this.usedAddressesByIoType.get(instance.type());
        if (usedAddresses == null)
            return;
        usedAddresses.remove(bcmConfig.bcm());
        if (usedAddresses.isEmpty())
            this.usedAddressesByIoType.remove(instance.type());
    }

    /**
     * {@inheritDoc}
     */
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
    public synchronized boolean exists(IOType ioType, int address) {
        Set<Integer> usedAddresses = this.usedAddressesByIoType.get(ioType);
        return usedAddresses != null && usedAddresses.contains(address);
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized RuntimeRegistry shutdown() {
        all().values().forEach(instance -> {
            try {
                remove(instance.id());
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        });
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RuntimeRegistry initialize() throws InitializeException {
        // NOTHING TO INITIALIZE
        return this;
    }
}

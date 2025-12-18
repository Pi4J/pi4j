package com.pi4j.registry.impl;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  DefaultRegistry.java
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

import com.pi4j.io.IO;
import com.pi4j.io.IOType;
import com.pi4j.io.exception.IOAlreadyExistsException;
import com.pi4j.io.exception.IOInvalidIDException;
import com.pi4j.io.exception.IONotFoundException;
import com.pi4j.io.exception.IOShutdownException;
import com.pi4j.registry.Registry;

import java.util.Map;

/**
 * <p>DefaultRegistry class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public class DefaultRegistry implements Registry {

    private RuntimeRegistry registry = null;

    /**
     * <p>newInstance.</p>
     *
     * @param registry a {@link com.pi4j.registry.impl.RuntimeRegistry} object.
     * @return a {@link com.pi4j.registry.Registry} object.
     */
    public static Registry newInstance(RuntimeRegistry registry) {
        return new DefaultRegistry(registry);
    }

    // private constructor
    private DefaultRegistry(RuntimeRegistry registry) {
        // set local registry reference
        this.registry = registry;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean exists(String id) {
        return registry.exists(id);
    }

    @Override
    public boolean exists(IOType ioType, int address) {
        return registry.exists(ioType, address);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, ? extends IO> all() {
        return registry.all();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends IO> T get(String id) throws IOInvalidIDException, IONotFoundException {
        return registry.get(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends IO> T get(String id, Class<T> type) throws IOInvalidIDException, IONotFoundException {
        return registry.get(id, type);
    }

    @Override
    public Registry add(IO instance) throws IOAlreadyExistsException, IOInvalidIDException {
        return this.registry.add(instance);
    }

    @Override
    public <T extends IO> T remove(String id) throws IONotFoundException, IOInvalidIDException, IOShutdownException {
        return this.registry.remove(id);
    }
}

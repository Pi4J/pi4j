package com.pi4j.registry.impl;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  RuntimeRegistry.java
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

import com.pi4j.exception.InitializeException;
import com.pi4j.exception.ShutdownException;
import com.pi4j.io.IO;
import com.pi4j.io.exception.IOAlreadyExistsException;
import com.pi4j.io.exception.IOInvalidIDException;
import com.pi4j.io.exception.IONotFoundException;
import com.pi4j.io.exception.IOShutdownException;
import com.pi4j.registry.Registry;

/**
 * <p>RuntimeRegistry interface.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public interface RuntimeRegistry extends Registry {
    /**
     * <p>add.</p>
     *
     * @param instance a {@link com.pi4j.io.IO} object.
     * @return a {@link com.pi4j.registry.impl.RuntimeRegistry} object.
     * @throws com.pi4j.io.exception.IOAlreadyExistsException if any.
     * @throws com.pi4j.io.exception.IOInvalidIDException if any.
     */
    RuntimeRegistry add(IO instance) throws IOAlreadyExistsException, IOInvalidIDException;

    /**
     * <p>remove.</p>
     *
     * @param instance the instance to remove
     * @param <T>      a T object.
     *
     * @throws com.pi4j.io.exception.IONotFoundException  if any.
     * @throws com.pi4j.io.exception.IOInvalidIDException if any.
     * @throws com.pi4j.io.exception.IOShutdownException  if any.
     */
    <T extends IO> void remove(T instance) throws IONotFoundException, IOInvalidIDException, IOShutdownException;

    /**
     * <p>remove.</p>
     *
     * @param id  a {@link java.lang.String} object.
     * @param <T> a T object.
     *
     * @return a T object.
     *
     * @throws com.pi4j.io.exception.IONotFoundException  if any.
     * @throws com.pi4j.io.exception.IOInvalidIDException if any.
     * @throws com.pi4j.io.exception.IOShutdownException  if any.
     */
    <T extends IO> T remove(String id) throws IONotFoundException, IOInvalidIDException, IOShutdownException;

    /**
     * <p>shutdown.</p>
     *
     * @return a {@link com.pi4j.registry.impl.RuntimeRegistry} object.
     * @throws com.pi4j.exception.ShutdownException if any.
     */
    RuntimeRegistry shutdown() throws ShutdownException;
    /**
     * <p>initialize.</p>
     *
     * @return a {@link com.pi4j.registry.impl.RuntimeRegistry} object.
     * @throws com.pi4j.exception.InitializeException if any.
     */
    RuntimeRegistry initialize() throws InitializeException;
}

package com.pi4j.runtime;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  Runtime.java
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

import com.pi4j.context.Context;
import com.pi4j.event.InitializedEventProducer;
import com.pi4j.event.ShutdownEventProducer;
import com.pi4j.exception.InitializeException;
import com.pi4j.exception.ShutdownException;
import com.pi4j.provider.impl.RuntimeProviders;
import com.pi4j.registry.impl.RuntimeRegistry;

import java.util.concurrent.Future;

/**
 * <p>Runtime interface.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public interface Runtime extends InitializedEventProducer<Runtime>, ShutdownEventProducer<Runtime> {
    /**
     * <p>registry.</p>
     *
     * @return a {@link com.pi4j.registry.impl.RuntimeRegistry} object.
     */
    RuntimeRegistry registry();

    /**
     * <p>providers.</p>
     *
     * @return a {@link com.pi4j.provider.impl.RuntimeProviders} object.
     */
    RuntimeProviders providers();

    /**
     * <p>context.</p>
     *
     * @return a {@link com.pi4j.context.Context} object.
     */
    Context context();

    Future<?> submitTask(Runnable task);

    /**
     * <p>shutdown.</p>
     *
     * @return a {@link com.pi4j.runtime.Runtime} object.
     *
     * @throws com.pi4j.exception.ShutdownException if any.
     */
    Runtime shutdown() throws ShutdownException;

    Future<Context> asyncShutdown();

    /**
     * @return Flag indicating if the runtime has been shutdown
     */
    boolean isShutdown();

    /**
     * <p>initialize.</p>
     *
     * @return a {@link com.pi4j.runtime.Runtime} object.
     *
     * @throws com.pi4j.exception.InitializeException if any.
     */
    Runtime initialize() throws InitializeException;
}

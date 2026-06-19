package com.pi4j.common;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  Lifecycle.java
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
import com.pi4j.exception.InitializeException;
import com.pi4j.exception.ShutdownException;

/**
 * Defines the managed startup and shutdown phases for Pi4J components such as I/O providers and platforms.
 * Components are brought online with {@link #initialize(Context)} and torn down with
 * {@link #shutdownInternal(Context)}, both bound to the active {@link Context}.
 *
 * @param <T> the self-type returned by the lifecycle methods, enabling fluent chaining on the implementing component
 */
public interface Lifecycle<T> {

    /**
     * Initializes this component against the given Pi4J context, acquiring any resources it needs to operate.
     *
     * @param context the active Pi4J {@link Context} this component is being initialized within
     * @return this component instance, initialized and ready for use
     * @throws com.pi4j.exception.InitializeException if an error occurs during initialization.
     */
    T initialize(Context context) throws InitializeException;

    /**
     * Shuts this component down as part of the registry's teardown, releasing resources acquired during
     * initialization. This method is called by the registry internally when the registry is shutting down and
     * should not be called by users directly. To shut down an IO instance, call {@code close()} instead, which
     * ensures the instance is not just shut down and closed but also properly unregistered.
     *
     * @param context the active Pi4J {@link Context} this component is being shut down within
     * @return this component instance, after shutdown
     * @throws com.pi4j.exception.ShutdownException if an error occurs during shutdown.
     */
    T shutdownInternal(Context context) throws ShutdownException;
}

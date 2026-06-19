package com.pi4j.event;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  InitializedEventProducer.java
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

/**
 * {@link EventProducer} implemented by Pi4J types that emit {@link InitializedEvent}s
 * and let callers register {@link InitializedListener}s to be notified when the Pi4J
 * context has completed initialization. The listener-management methods are fluent,
 * returning the producer itself for chaining.
 *
 * @param <T> the concrete producer type returned for method chaining
 */
public interface InitializedEventProducer<T> extends EventProducer {
    /**
     * Removes all registered initialization listeners from this producer.
     *
     * @return this producer instance for method chaining
     */
    T removeAllInitializedListeners();

    /**
     * Registers one or more listeners to be notified when initialization completes.
     *
     * @param listener the {@link InitializedListener}(s) to register
     * @return this producer instance for method chaining
     */
    T addListener(InitializedListener ... listener);

    /**
     * Unregisters one or more previously registered initialization listeners.
     *
     * @param listener the {@link InitializedListener}(s) to remove
     * @return this producer instance for method chaining
     */
    T removeListener(InitializedListener ... listener);
}

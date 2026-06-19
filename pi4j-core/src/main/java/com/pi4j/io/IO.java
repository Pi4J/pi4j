package com.pi4j.io;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  IO.java
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

import com.pi4j.common.Describable;
import com.pi4j.common.Identity;
import com.pi4j.common.Lifecycle;
import com.pi4j.provider.Provider;

import java.io.Closeable;

/**
 * Base contract for every Pi4J I/O instance (digital input/output, PWM, I2C, SPI, ...).
 * <p>
 * An {@code IO} is created from an {@link IOConfig} by a {@link Provider} and managed through the
 * Pi4J lifecycle ({@link Lifecycle}). It exposes identity ({@link Identity}) and self-description
 * ({@link Describable}) and is closeable so that it can be released when no longer needed.
 *
 * @param <IO_TYPE>       the concrete I/O type, returned by the fluent identity setters for chaining
 * @param <CONFIG_TYPE>   the {@link IOConfig} type that describes and creates this I/O instance
 * @param <PROVIDER_TYPE> the {@link Provider} type that instantiated and backs this I/O instance
 */
public interface IO<IO_TYPE extends IO, CONFIG_TYPE extends IOConfig, PROVIDER_TYPE extends Provider>
        extends Describable, Lifecycle, Identity, Closeable {

    /**
     * Closes this IO instance.
     * <p>
     * Pi4J generally throws unchecked exceptions, so we restrict Closeable.close() here accordingly.
     * For the interaction with shutdown and general overriding and usage recommendations, please
     * refer to the documentation of IOBase.close().
     */
    @Override
    void close();

    /**
     * Returns the configuration this I/O instance was created from.
     *
     * @return the {@link IOConfig} that defines this instance's properties
     */
    CONFIG_TYPE config();

    /**
     * Returns the {@link IOType} category (digital input/output, PWM, I2C, SPI) of this instance,
     * resolved from its concrete class.
     *
     * @return the matching {@link IOType}, or {@code null} if the class maps to no known type
     */
    default IOType type() { return IOType.getByIOClass(this.getClass()); }

    // TODO :: RECONCILE IDENTITY PROPERTIES BETWEEN IO INSTANCE AND UNDERLYING CONFIG; PROBABLY NEED TO REMOVE THESE SETTERS

    /**
     * Sets the human-readable name of this I/O instance.
     *
     * @param name the display name to assign
     * @return this instance for method chaining
     */
    IO_TYPE name(String name);

    /**
     * Sets the human-readable description of this I/O instance.
     *
     * @param description the descriptive text to assign
     * @return this instance for method chaining
     */
    IO_TYPE description(String description);

    /**
     * Returns the provider that created and backs this I/O instance.
     *
     * @return the {@link Provider} responsible for this instance
     */
    PROVIDER_TYPE provider();
}

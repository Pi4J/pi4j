package com.pi4j.io.gpio;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  GpioBase.java
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

import com.pi4j.io.IOBase;
import com.pi4j.provider.Provider;

/**
 * Abstract base implementation for {@link Gpio} I/O instances. It extends {@link IOBase} to provide
 * the shared identity, configuration and lifecycle plumbing common to all GPIO-based I/O, leaving
 * concrete subclasses (e.g. digital input/output and PWM implementations) to add their pin-specific behaviour.
 *
 * @param <IO_TYPE>       the concrete GPIO I/O type, returned by fluent identity setters for chaining
 * @param <CONFIG_TYPE>   the {@link GpioConfig} type describing and creating this I/O instance
 * @param <PROVIDER_TYPE> the {@link Provider} type that instantiated and backs this I/O instance
 */
public abstract class GpioBase<IO_TYPE extends Gpio<IO_TYPE, CONFIG_TYPE, PROVIDER_TYPE>,
    CONFIG_TYPE extends GpioConfig<CONFIG_TYPE>, PROVIDER_TYPE extends Provider>
    extends IOBase<IO_TYPE, CONFIG_TYPE, PROVIDER_TYPE>
    implements Gpio<IO_TYPE, CONFIG_TYPE, PROVIDER_TYPE> {

    /**
     * Creates a new GPIO I/O instance bound to the given provider and configuration.
     *
     * @param provider the {@link Provider} that creates and backs this I/O instance
     * @param config   the {@link GpioConfig} describing this I/O, including its BCM pin number
     */
    public GpioBase(PROVIDER_TYPE provider, CONFIG_TYPE config) {
        super(provider, config);
    }

    /**
     * Returns a short human-readable representation of this GPIO instance in the form
     * {@code @<id>} optionally followed by its name when one is configured.
     *
     * @return a concise description combining this instance's id and, when present, its name
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        // include ID
        result.append("@");
        result.append(this.id());

        // include NAME
        if (this.name() != null && !this.name().isEmpty()) {
            result.append(" \"");
            result.append(this.name());
        }

        return result.toString();
    }
}

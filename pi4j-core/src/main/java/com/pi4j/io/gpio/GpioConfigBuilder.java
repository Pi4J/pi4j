package com.pi4j.io.gpio;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  GpioConfigBuilder.java
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

import com.pi4j.io.IOBcmConfigBuilder;

/**
 * Fluent builder contract for assembling a {@link GpioConfig}. It extends {@link IOBcmConfigBuilder}
 * to inherit the BCM pin and common I/O configuration setters, adding GPIO-specific bus addressing so
 * concrete GPIO config builders share a consistent fluent API.
 *
 * @param <BUILDER_TYPE> the concrete builder sub-type, returned by fluent setters to enable type-safe chaining
 * @param <CONFIG_TYPE>  the {@link GpioConfig} type produced by this builder
 */
public interface GpioConfigBuilder<BUILDER_TYPE extends GpioConfigBuilder, CONFIG_TYPE extends GpioConfig>
    extends IOBcmConfigBuilder<BUILDER_TYPE, CONFIG_TYPE> {

    /**
     * Sets the bus number on which the GPIO device is addressed.
     *
     * @param bus the bus number to associate with the configured GPIO device
     * @return this builder instance for method chaining
     */
    GpioConfigBuilder<BUILDER_TYPE, CONFIG_TYPE> bus(int bus);
}

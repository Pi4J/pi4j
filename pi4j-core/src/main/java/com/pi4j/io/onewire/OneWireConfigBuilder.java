package com.pi4j.io.onewire;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  OneWireConfigBuilder.java
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

import com.pi4j.config.ConfigBuilder;
import com.pi4j.context.Context;
import com.pi4j.io.IOConfigBuilder;
import com.pi4j.io.onewire.impl.DefaultOneWireConfigBuilder;

/**
 * Builder interface for configuring 1-Wire interfaces in the Pi4J library.
 * <p>
 * This interface extends {@link IOConfigBuilder} and {@link ConfigBuilder},
 * providing a fluent API to construct and customize 1-Wire configuration settings.
 * </p>
 */
public interface OneWireConfigBuilder extends
    IOConfigBuilder<OneWireConfigBuilder, OneWireConfig>,
    ConfigBuilder<OneWireConfigBuilder, OneWireConfig> {

    /**
     * Creates a new instance of {@link OneWireConfigBuilder}.
     * <p>
     * This factory method provides a convenient way to initialize
     * a 1-Wire configuration builder within the given {@link Context}.
     * </p>
     *
     * @param context the {@link Context} for the application environment.
     * @return a {@link OneWireConfigBuilder} instance for building configurations.
     */
    static OneWireConfigBuilder newInstance(Context context)  {
        return DefaultOneWireConfigBuilder.newInstance(context);
    }

    /**
     * Specifies the device ID for the 1-Wire interface.
     * <p>
     * The device ID uniquely identifies the target 1-Wire device
     * and is a required configuration parameter.
     * </p>
     *
     * @param device a {@link String} representing the device ID.
     * @return the {@link OneWireConfigBuilder} instance, for chaining.
     */
    OneWireConfigBuilder device(String device);
}

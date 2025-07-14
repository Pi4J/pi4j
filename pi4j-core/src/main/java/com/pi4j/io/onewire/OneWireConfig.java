package com.pi4j.io.onewire;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  OneWireConfig.java
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
import com.pi4j.io.IOConfig;

/**
 * Interface defining the configuration settings for a 1-Wire interface in the Pi4J library.
 * <p>
 * This interface extends {@link IOConfig} and provides additional configuration options
 * specific to 1-Wire devices.
 * </p>
 */
public interface OneWireConfig extends IOConfig<OneWireConfig> {

    /**
     * Key used for accessing the device configuration property.
     */
    String DEVICE_KEY = "device";

    /**
     * Retrieves the device ID associated with this configuration.
     * <p>
     * The device ID is a unique identifier used to specify the target 1-Wire device.
     * </p>
     *
     * @return a {@link String} representing the device ID.
     */
    String device();

    /**
     * Retrieves the device ID associated with this configuration.
     * <p>
     * This method serves as an alias for {@link #device()} for convenience.
     * </p>
     *
     * @return a {@link String} representing the device ID.
     */
    default String getDevice() {
        return device();
    }

    /**
     * Creates a new builder instance for configuring a 1-Wire interface.
     * <p>
     * The builder provides a fluent API for constructing a {@link OneWireConfig} object.
     * </p>
     *
     * @param context the {@link Context} associated with this configuration.
     * @return a {@link OneWireConfigBuilder} instance for building the configuration.
     */
    static OneWireConfigBuilder newBuilder(Context context) {
        return OneWireConfigBuilder.newInstance(context);
    }
}

package com.pi4j.io.onewire;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  OneWire.java
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
import com.pi4j.io.IO;
import com.pi4j.io.IODataReader;
import com.pi4j.io.IODataWriter;

/**
 * Interface representing a 1-Wire I/O communication in the Pi4J library.
 * <p>
 * This interface facilitates 1-Wire bus/device communications by providing
 * methods for configuration, state management, and data interaction.
 * It extends {@link IO}, {@link IODataWriter}, {@link IODataReader},
 * and {@link OneWireFileDataReaderWriter}, and supports auto-closing of resources.
 * </p>
 */
public interface OneWire
    extends IO<OneWire, OneWireConfig, OneWireProvider>, OneWireFileDataReaderWriter {

    /**
     * Creates a new configuration builder for a 1-Wire interface.
     * <p>
     * This static method provides a convenient way to construct
     * a {@link OneWireConfigBuilder} object.
     * </p>
     *
     * @param context the {@link Context} associated with this configuration.
     * @return a {@link OneWireConfigBuilder} instance to build the configuration.
     */
    static OneWireConfigBuilder newConfigBuilder(Context context) {
        return OneWireConfigBuilder.newInstance(context);
    }

    /**
     * Retrieves the 1-Wire device address for this interface instance.
     * <p>
     * The device address is defined in the configuration and is used
     * to identify the target 1-Wire device on the bus.
     * </p>
     *
     * @return a {@code String} representing the 1-Wire device address.
     */
    default String device() {
        return config().device();
    }

    /**
     * Retrieves the 1-Wire device address for this interface instance.
     * <p>
     * This method serves as an alias for {@link #device()}.
     * </p>
     *
     * @return a {@code String} representing the 1-Wire device address.
     */
    default String getDevice() {
        return device();
    }
}

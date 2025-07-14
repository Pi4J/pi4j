package com.pi4j.io.onewire;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  OneWireProvider.java
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

import com.pi4j.provider.Provider;

/**
 * Interface for defining a 1-Wire Provider in the Pi4J library.
 * <p>
 * The {@link OneWireProvider} interface is responsible for creating and managing
 * instances of 1-Wire devices. It serves as a factory and configurator, enabling
 * the creation of 1-Wire devices based on various parameters like device ID, custom ID,
 * name, and description.
 * </p>
 */
public interface OneWireProvider extends Provider<OneWireProvider, OneWire, OneWireConfig> {

    /**
     * Creates a new 1-Wire device using a pre-configured {@link OneWireConfigBuilder}.
     * <p>
     * This method is useful for building and creating 1-Wire devices when a
     * configuration builder instance is already available.
     * </p>
     *
     * @param builder the configuration builder used to define the 1-Wire device.
     * @param <T> the type of the 1-Wire device to create.
     * @return an instance of the created 1-Wire device.
     */
    default <T extends OneWire> T create(OneWireConfigBuilder builder) {
        return (T) create(builder.build());
    }

    /**
     * Creates a new 1-Wire device with a specified device ID and custom ID.
     * <p>
     * This method provides a simpler way to create 1-Wire devices without
     * requiring additional metadata like name or description.
     * </p>
     *
     * @param device the 1-Wire device address as a {@link String}.
     * @param id a unique identifier for the 1-Wire device as a {@link String}.
     * @param <T> the type of the 1-Wire device to create.
     * @return an instance of the created 1-Wire device.
     */
    default <T extends OneWire> T create(String device, String id) {
        var config = OneWire.newConfigBuilder(context())
            .device(device)
            .id(id)
            .build();
        return (T) create(config);
    }

    /**
     * Creates a new 1-Wire device with a specified device ID, custom ID, and name.
     * <p>
     * This method allows for the creation of 1-Wire devices with an associated
     * human-readable name for easier identification.
     * </p>
     *
     * @param device the 1-Wire device address as a {@link String}.
     * @param id a unique identifier for the 1-Wire device as a {@link String}.
     * @param name a human-readable name for the device as a {@link String}.
     * @param <T> the type of the 1-Wire device to create.
     * @return an instance of the created 1-Wire device.
     */
    default <T extends OneWire> T create(String device, String id, String name) {
        var config = OneWire.newConfigBuilder(context())
            .device(device)
            .id(id)
            .name(name)
            .build();
        return (T) create(config);
    }

    /**
     * Creates a new 1-Wire device with a specified device ID, custom ID, name, and description.
     * <p>
     * This method provides the most detailed way to create a 1-Wire device,
     * including an optional description for additional metadata.
     * </p>
     *
     * @param device the 1-Wire device address as a {@link String}.
     * @param id a unique identifier for the 1-Wire device as a {@link String}.
     * @param name a human-readable name for the device as a {@link String}.
     * @param description a descriptive text for the device as a {@link String}.
     * @param <T> the type of the 1-Wire device to create.
     * @return an instance of the created 1-Wire device.
     */
    default <T extends OneWire> T create(String device, String id, String name, String description) {
        var config = OneWire.newConfigBuilder(context())
            .device(device)
            .id(id)
            .name(name)
            .description(description)
            .build();
        return (T) create(config);
    }
}

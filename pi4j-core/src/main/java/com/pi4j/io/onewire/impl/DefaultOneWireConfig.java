package com.pi4j.io.onewire.impl;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  DefaultOneWireConfig.java
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

import com.pi4j.config.exception.ConfigMissingRequiredKeyException;
import com.pi4j.io.onewire.OneWireConfig;
import com.pi4j.io.impl.IOConfigBase;
import com.pi4j.util.StringUtil;

import java.util.Map;

/**
 * Default implementation of the {@link OneWireConfig} interface.
 * <p>
 * This class represents the configuration settings for a 1-Wire device
 * and ensures that the required properties are properly initialized.
 * </p>
 */
public class DefaultOneWireConfig
    extends IOConfigBase<OneWireConfig>
    implements OneWireConfig {

    // Private field to store the device address
    protected String device = null;

    /**
     * Private constructor to prevent instantiation without properties.
     * <p>
     * This ensures that the configuration is always initialized
     * with the required settings from a property map.
     * </p>
     */
    private DefaultOneWireConfig() {
        super();
    }

    /**
     * Constructs a new {@link DefaultOneWireConfig} instance with the provided properties.
     * <p>
     * This constructor validates the presence of required properties, such as
     * the device address, and sets default values for optional properties if they are missing.
     * </p>
     *
     * @param properties a {@link Map} containing configuration keys and values.
     * @throws ConfigMissingRequiredKeyException if the required "device" key is missing.
     */
    protected DefaultOneWireConfig(Map<String, String> properties) {
        super(properties);

        // Load the (required) DEVICE property for 1-Wire
        if (properties.containsKey(DEVICE_KEY)) {
            this.device = String.valueOf(properties.get(DEVICE_KEY));
        } else {
            throw new ConfigMissingRequiredKeyException(DEVICE_KEY);
        }

        // Set default values for optional properties if missing
        this.id = StringUtil.setIfNullOrEmpty(this.id, "1-Wire-" + this.device(), true);
        this.name = StringUtil.setIfNullOrEmpty(this.name, "1-Wire-" + this.device(), true);
        this.description = StringUtil.setIfNullOrEmpty(this.description, "1-Wire-" + this.device(), true);
    }

    /**
     * Retrieves the device address for this 1-Wire configuration.
     * <p>
     * The device address is a unique identifier for the 1-Wire device
     * and is a required configuration property.
     * </p>
     *
     * @return a {@link String} representing the 1-Wire device address.
     */
    @Override
    public String device() {
        return this.device;
    }

    // Additional methods for 1-Wire configuration can be added here as needed.
}

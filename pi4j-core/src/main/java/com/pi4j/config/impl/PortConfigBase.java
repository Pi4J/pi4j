package com.pi4j.config.impl;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  AddressConfigBase.java
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

import com.pi4j.config.Config;
import com.pi4j.config.ConfigBase;
import com.pi4j.config.PortConfig;
import com.pi4j.config.exception.ConfigMissingRequiredKeyException;

import java.util.Map;

/**
 * <p>Abstract AddressConfigBase class.</p>
 *
 * @param <CONFIG_TYPE>
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public abstract class PortConfigBase<CONFIG_TYPE extends Config>
    extends ConfigBase<CONFIG_TYPE>
    implements PortConfig<CONFIG_TYPE> {

    // private configuration properties
    protected String port = null;

    /**
     * PRIVATE CONSTRUCTOR
     */
    protected PortConfigBase() {
        super();
    }

    /**
     * <p>Constructor for PortConfigBase.</p>
     *
     * @param port a {@link String} object.
     */
    protected PortConfigBase(String port) {
        super();
        this.port = port;
    }

    /**
     * PRIVATE CONSTRUCTOR
     *
     * @param properties a {@link Map} object.
     */
    protected PortConfigBase(Map<String, String> properties) {
        super(properties);

        // load address property
        if (properties.containsKey(PORT_KEY)) {
            this.port = properties.get(PORT_KEY);
        } else {
            throw new ConfigMissingRequiredKeyException(PORT_KEY);
        }
    }

    /**
     * <p>port.</p>
     *
     * @return a {@link String} object.
     */
    public String port() {
        return this.port;
    }
}

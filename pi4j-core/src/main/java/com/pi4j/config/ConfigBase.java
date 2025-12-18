package com.pi4j.config;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  ConfigBase.java
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
import com.pi4j.util.StringUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>ConfigBase class.</p>
 *
 * @param <CONFIG_TYPE>
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public class ConfigBase<CONFIG_TYPE extends Config> implements Config<CONFIG_TYPE> {

    // private configuration variables
    protected String id = null;
    protected String name = null;
    protected String description = null;
    protected Map<String, String> properties = new HashMap<>();

    /**
     * PRIVATE CONSTRUCTOR
     */
    protected ConfigBase() {
    }

    /**
     * PRIVATE CONSTRUCTOR
     *
     * @param properties a {@link java.util.Map} object.
     */
    protected ConfigBase(Map<String, String> properties) {
        // add all properties to this config object
        this.properties.putAll(properties);

        // load required 'id' property
        if (properties.containsKey(ID_KEY))
            this.id = properties.get(ID_KEY);

        // load optional 'name' property
        if (properties.containsKey(NAME_KEY))
            this.name = properties.get(NAME_KEY);

        // load optional 'description' property
        if (properties.containsKey(DESCRIPTION_KEY))
            this.description = properties.get(DESCRIPTION_KEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> properties() {
        return Collections.unmodifiableMap(this.properties);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String id() {
        return this.id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return this.name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String description() {
        return this.description;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate() {
        if (StringUtil.isNullOrEmpty(this.id)) {
            throw new ConfigMissingRequiredKeyException(ID_KEY);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getUniqueIdentifier() {
        return 0;
    }
}

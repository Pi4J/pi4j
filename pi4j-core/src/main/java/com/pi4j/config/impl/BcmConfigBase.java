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

import com.pi4j.config.BcmConfig;
import com.pi4j.config.Config;
import com.pi4j.config.ConfigBase;
import com.pi4j.config.exception.ConfigMissingRequiredKeyException;

import java.util.Map;

/**
 * <p>Abstract AddressConfigBase class.</p>
 *
 * @param <CONFIG_TYPE>
 */
public abstract class BcmConfigBase<CONFIG_TYPE extends Config>
    extends ConfigBase<CONFIG_TYPE>
    implements BcmConfig<CONFIG_TYPE> {

    // private configuration properties
    protected Integer bcm = null;

    /**
     * PRIVATE CONSTRUCTOR
     */
    protected BcmConfigBase() {
        super();
    }

    protected BcmConfigBase(Integer bcm) {
        super();
        this.bcm = bcm;
    }

    /**
     * PRIVATE CONSTRUCTOR
     */
    protected BcmConfigBase(Map<String, String> properties) {
        super(properties);

        // load address property
        if (properties.containsKey(BCM_KEY)) {
            this.bcm = Integer.parseInt(properties.get(BCM_KEY));
        } else {
            throw new ConfigMissingRequiredKeyException(BCM_KEY);
        }
    }

    public Integer address() {
        return this.bcm;
    }

    public Integer bcm() {
        return this.bcm;
    }
}

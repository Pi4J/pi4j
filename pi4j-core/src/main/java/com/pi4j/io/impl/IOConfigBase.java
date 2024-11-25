package com.pi4j.io.impl;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  IOConfigBase.java
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
import com.pi4j.io.IOConfig;

import java.util.Map;

/**
 * <p>ConfigBase class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 * @param <CONFIG_TYPE>
 */
public abstract class IOConfigBase<CONFIG_TYPE extends Config> extends ConfigBase<CONFIG_TYPE> implements IOConfig<CONFIG_TYPE> {

    // private configuration variables
    protected String provider = null;
    protected String platform = null;

    /**
     * PRIVATE CONSTRUCTOR
     */
    protected IOConfigBase(){
    }

    /**
     * PRIVATE CONSTRUCTOR
     *
     * @param properties a {@link Map} object.
     */
    protected IOConfigBase(Map<String,String> properties){
        super(properties);

        // load provider property
        if(properties.containsKey(PROVIDER_KEY)){
            this.provider = properties.get(PROVIDER_KEY);
        }
        // load platform property
        if(properties.containsKey(PLATFORM_KEY)){
            this.platform = properties.get(PLATFORM_KEY);
        }
    }

    /** {@inheritDoc} */
    @Override
    public String platform() {
        return this.platform;
    }

    /** {@inheritDoc} */
    @Override
    public String provider() {
        return this.provider;
    }
}

package com.pi4j.io.impl;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  IODeviceConfigBuilderBase.java
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
import com.pi4j.config.ConfigBuilder;
import com.pi4j.config.DeviceConfigBuilder;
import com.pi4j.config.impl.DeviceConfigBuilderBase;
import com.pi4j.context.Context;
import com.pi4j.io.IOConfig;
import com.pi4j.io.IOConfigBuilder;
import com.pi4j.platform.Platform;
import com.pi4j.provider.Provider;

/**
 * <p>Abstract AddressConfigBuilderBase class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 * @param <BUILDER_TYPE>
 * @param <CONFIG_TYPE>
 */
public abstract class IODeviceConfigBuilderBase<BUILDER_TYPE extends ConfigBuilder, CONFIG_TYPE extends Config>
        extends DeviceConfigBuilderBase<BUILDER_TYPE, CONFIG_TYPE>
        implements IOConfigBuilder<BUILDER_TYPE, CONFIG_TYPE>,
        DeviceConfigBuilder<BUILDER_TYPE, CONFIG_TYPE> {

    /**
     * PRIVATE CONSTRUCTOR
     */
    protected IODeviceConfigBuilderBase(Context context){
        super(context);
    }

    /** {@inheritDoc} */
    @Override
    public BUILDER_TYPE provider(String provider){
        this.properties.put(IOConfig.PROVIDER_KEY, provider);
        return (BUILDER_TYPE) this;
    }

    /** {@inheritDoc} */
    @Override
    public BUILDER_TYPE provider(Class<? extends Provider> providerClass){
        this.properties.put(IOConfig.PROVIDER_KEY, providerClass.getName());
        return (BUILDER_TYPE) this;
    }

    /** {@inheritDoc} */
    @Override
    public BUILDER_TYPE platform(String platform){
        this.properties.put(IOConfig.PLATFORM_KEY, platform);
        return (BUILDER_TYPE) this;
    }

    /** {@inheritDoc} */
    @Override
    public BUILDER_TYPE platform(Class<? extends Platform> platformClass){
        this.properties.put(IOConfig.PLATFORM_KEY, platformClass.getName());
        return (BUILDER_TYPE) this;
    }
}

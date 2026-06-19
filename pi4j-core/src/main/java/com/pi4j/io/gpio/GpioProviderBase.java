package com.pi4j.io.gpio;

import com.pi4j.io.IO;
import com.pi4j.provider.ProviderBase;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  GpioProviderBase.java
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
/**
 * Abstract base implementation for {@link GpioProvider}s. It extends {@link ProviderBase} to supply the
 * shared id/name identity and provider lifecycle plumbing, leaving concrete GPIO providers to implement
 * the actual creation of GPIO I/O instances for a given platform or device.
 *
 * @param <PROVIDER_TYPE> the concrete GPIO provider sub-type
 * @param <IO_TYPE>       the {@link IO} type this provider creates
 * @param <CONFIG_TYPE>   the {@link GpioConfig} type accepted when creating I/O instances
 */
public abstract class GpioProviderBase<
            PROVIDER_TYPE extends GpioProvider,
            IO_TYPE extends IO,
            CONFIG_TYPE extends GpioConfig>
        extends ProviderBase<PROVIDER_TYPE, IO_TYPE, CONFIG_TYPE>
        implements GpioProvider<PROVIDER_TYPE, IO_TYPE, CONFIG_TYPE> {

    /**
     * Creates a new GPIO provider without an explicit id or name; subclasses are expected to supply
     * their identity by other means.
     */
    public GpioProviderBase(){
        super();
    }

    /**
     * Creates a new GPIO provider with the given unique identifier.
     *
     * @param id the unique identifier used to register and look up this provider
     */
    public GpioProviderBase(String id){
        super(id);
    }

    /**
     * Creates a new GPIO provider with the given unique identifier and human-readable name.
     *
     * @param id   the unique identifier used to register and look up this provider
     * @param name the human-readable display name of this provider
     */
    public GpioProviderBase(String id, String name){
        super(id, name);
    }
}

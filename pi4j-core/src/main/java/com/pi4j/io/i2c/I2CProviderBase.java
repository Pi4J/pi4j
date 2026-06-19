package com.pi4j.io.i2c;

import com.pi4j.provider.ProviderBase;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  I2CProviderBase.java
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
 * Base class for {@link I2CProvider} implementations, supplying the common {@link com.pi4j.provider.ProviderBase}
 * plumbing so concrete providers only need to implement device creation. Platform plugins extend this to expose
 * their I2C support to Pi4J.
 */
public abstract class I2CProviderBase
        extends ProviderBase<I2CProvider, I2C, I2CConfig>
        implements I2CProvider {

    /**
     * Creates a provider with auto-generated identifier and name.
     */
    public I2CProviderBase(){
        super();
    }

    /**
     * Creates a provider with the given identifier.
     *
     * @param id the unique identifier used to register and look up this provider
     */
    public I2CProviderBase(String id){
        super(id);
    }

    /**
     * Creates a provider with the given identifier and display name.
     *
     * @param id   the unique identifier used to register and look up this provider
     * @param name the human-readable display name of this provider
     */
    public I2CProviderBase(String id, String name){
        super(id, name);
    }
}

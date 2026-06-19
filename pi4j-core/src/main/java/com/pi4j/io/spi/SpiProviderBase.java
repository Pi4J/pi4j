package com.pi4j.io.spi;

import com.pi4j.provider.ProviderBase;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  SpiProviderBase.java
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
 * Base class for {@link SpiProvider} implementations, supplying identity handling on top of
 * {@link ProviderBase}. Concrete plugins extend this class and implement creation of {@link Spi}
 * devices for their target platform.
 */
public abstract class SpiProviderBase
        extends ProviderBase<SpiProvider, Spi, SpiConfig>
        implements SpiProvider {

    /**
     * Creates a provider with no preset identifier; the id is assigned later by the runtime.
     */
    public SpiProviderBase(){
        super();
    }

    /**
     * Creates a provider with the given unique identifier.
     *
     * @param id the unique provider id used to register and look up this provider
     */
    public SpiProviderBase(String id){
        super(id);
    }

    /**
     * Creates a provider with the given identifier and human-readable name.
     *
     * @param id   the unique provider id used to register and look up this provider
     * @param name a human-readable display name for this provider
     */
    public SpiProviderBase(String id, String name){
        super(id, name);
    }
}

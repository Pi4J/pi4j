package com.pi4j.io.onewire;

import com.pi4j.provider.ProviderBase;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  OneWireProviderBase.java
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
 * Abstract base class for implementing OneWire providers.
 * This class serves as a foundation for creating concrete implementations
 * of the OneWireProvider interface, managing shared functionality and
 * configuration for OneWire communication.
 */
public abstract class OneWireProviderBase
    extends ProviderBase<OneWireProvider, OneWire, OneWireConfig>
    implements OneWireProvider {

    /**
     * Default constructor.
     * Initializes the base class without any specific identifier or name.
     */
    public OneWireProviderBase(){
        super();
    }

    /**
     * Constructor with an identifier.
     * Initializes the base class with a unique identifier for the provider.
     *
     * @param id a {@link String} representing the unique identifier of the provider.
     */
    public OneWireProviderBase(String id){
        super(id);
    }

    /**
     * Constructor with an identifier and name.
     * Initializes the base class with a unique identifier and a human-readable name.
     *
     * @param id a {@link String} representing the unique identifier of the provider.
     * @param name a {@link String} representing the human-readable name of the provider.
     */
    public OneWireProviderBase(String id, String name){
        super(id, name);
    }
}

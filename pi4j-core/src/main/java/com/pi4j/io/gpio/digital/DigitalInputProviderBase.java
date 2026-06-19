package com.pi4j.io.gpio.digital;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  DigitalInputProviderBase.java
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
 * Abstract base class for {@link DigitalInputProvider} implementations, supplying the common provider
 * identity handling from {@link DigitalProviderBase}. Concrete platform or expander providers extend this
 * to implement the actual digital input creation.
 */
public abstract class DigitalInputProviderBase
        extends DigitalProviderBase<DigitalInputProvider, DigitalInput, DigitalInputConfig>
        implements DigitalInputProvider {

    /**
     * Creates a provider with no preset identity; the id and name are expected to be supplied later.
     */
    public DigitalInputProviderBase(){
        super();
    }

    /**
     * Creates a provider with the given unique identifier.
     *
     * @param id the provider's unique identifier
     */
    public DigitalInputProviderBase(String id){
        super(id);
    }

    /**
     * Creates a provider with the given unique identifier and display name.
     *
     * @param id the provider's unique identifier
     * @param name the provider's human-readable name
     */
    public DigitalInputProviderBase(String id, String name){
        super(id, name);
    }
}

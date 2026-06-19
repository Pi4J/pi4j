package com.pi4j.extension;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  ExtensionBase.java
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

import com.pi4j.common.IdentityBase;

/**
 * Convenience base class for {@link Extension} implementations. It inherits identity handling
 * (id, name, description, metadata) from {@link IdentityBase} so that concrete extensions only
 * need to add their specific behaviour.
 *
 * @param <T> the self-type returned by fluent lifecycle and identity methods
 */
public abstract class ExtensionBase<T> extends IdentityBase<T> implements Extension<T> {

    /**
     * Creates an extension with no identity values set; id, name, and description may be
     * assigned later through the inherited fluent setters.
     */
    public ExtensionBase(){
        super();
    }

    /**
     * Creates an extension with the given identifier.
     *
     * @param id the unique identifier for this extension
     */
    public ExtensionBase(String id){
        super(id);
    }

    /**
     * Creates an extension with the given identifier and display name.
     *
     * @param id the unique identifier for this extension
     * @param name the human-readable display name for this extension
     */
    public ExtensionBase(String id, String name){
        super(id, name);
    }

    /**
     * Creates an extension with the given identifier, display name, and description.
     *
     * @param id the unique identifier for this extension
     * @param name the human-readable display name for this extension
     * @param description a human-readable description of this extension's purpose
     */
    public ExtensionBase(String id, String name, String description){
        super(id, name, description);
    }

}

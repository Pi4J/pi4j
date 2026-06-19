package com.pi4j.common;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  Identity.java
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
 * Common identity contract for Pi4J components, exposing a unique id, a name, a description, and a
 * {@link Metadata} collection of arbitrary key/value attributes. Extends {@link Describable} so any
 * identifiable component can also produce a {@link Descriptor} of itself.
 */
public interface Identity extends Describable {
    /**
     * Returns the unique identifier of this component.
     *
     * @return the component's identifier
     */
    String id();

    /**
     * Returns the human-readable name of this component.
     *
     * @return the component's name
     */
    String name();

    /**
     * Returns the description of this component.
     *
     * @return the component's description
     */
    String description();

    /**
     * Returns the metadata attached to this component.
     *
     * @return the component's {@link Metadata} collection
     */
    Metadata metadata();

    /**
     * Bean-style alias for {@link #id()}.
     *
     * @return the component's identifier
     */
    default String getId() {
        return id();
    }

    /**
     * Bean-style alias for {@link #name()}.
     *
     * @return the component's name
     */
    default String getName() {
        return name();
    }

    /**
     * Bean-style alias for {@link #description()}.
     *
     * @return the component's description
     */
    default String getDescription() {
        return description();
    }

    /**
     * Bean-style alias for {@link #metadata()}.
     *
     * @return the component's {@link Metadata} collection
     */
    default Metadata getMetadata() {
        return metadata();
    }

    @Override
    default Descriptor describe() {
        return Descriptor.create()
            .id(id())
            .name(name())
            .description(description());
    }
}

package com.pi4j.extension;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  Extension.java
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

import com.pi4j.common.Descriptor;
import com.pi4j.common.Identity;
import com.pi4j.common.Lifecycle;

/**
 * Common base interface for pluggable Pi4J components that are registered with, and managed by,
 * the runtime. An extension carries {@link Identity} metadata (id, name, description) and a
 * {@link Lifecycle} (initialize/shutdown), allowing it to be discovered, described, and managed
 * uniformly. Providers and other registrable components extend this contract.
 *
 * @param <T> the self-type returned by lifecycle methods to support fluent, type-safe chaining
 */
public interface Extension<T> extends Identity, Lifecycle<T> {

    /**
     * Builds a {@link Descriptor} that summarizes this extension's identity, categorized under
     * {@code "EXTENSION"}, for use in logging and runtime inspection.
     *
     * @return a descriptor populated with this extension's id, name, description, and concrete type
     */
    default Descriptor describe() {
        return Descriptor.create()
                .id(this.id())
                .name(this.name())
                .category("EXTENSION")
                .description(this.description()).type(this.getClass());
    }
}

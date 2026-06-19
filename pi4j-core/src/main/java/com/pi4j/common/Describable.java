package com.pi4j.common;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  Describable.java
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
 * Implemented by Pi4J types that can produce a human-readable, tree-structured description of themselves.
 * The returned {@link Descriptor} can be printed or inspected to report identity, configuration, and any
 * nested child descriptors.
 */
public interface Describable {
    /**
     * Builds a {@link Descriptor} that summarizes this object, including any nested child descriptors.
     *
     * @return a descriptor describing this object's identity and state
     */
    Descriptor describe();
}

package com.pi4j.config;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  Builder.java
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
 * Generic builder contract for the Pi4J configuration API. Implementations accumulate state through
 * fluent setters and produce a finished instance via {@link #build()}; it is the common super-interface
 * of {@link ConfigBuilder} and the I/O-specific configuration builders.
 *
 * @param <BUILT_TYPE> the type of object produced by {@link #build()}
 */
public interface Builder<BUILT_TYPE> {
    /**
     * Constructs and returns the configured instance from the state collected on this builder.
     *
     * @return the newly built instance
     */
    BUILT_TYPE build();
}

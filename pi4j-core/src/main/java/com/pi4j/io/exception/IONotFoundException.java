package com.pi4j.io.exception;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  IONotFoundException.java
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
 * Thrown when a lookup for an I/O instance by its identifier fails because no
 * matching instance is registered in the Pi4J registry. A specialization of
 * {@link IOException}.
 *
 * @see <a href="http://www.pi4j.com/">http://www.pi4j.com/</a>
 */
public class IONotFoundException extends IOException {

    /**
     * Creates an exception reporting that no I/O instance with the given id exists in the registry.
     *
     * @param id the identifier that was looked up but not found
     */
    public IONotFoundException(String id){
        super("IO instance [" + id + "] not found in Pi4J Registry.");
    }
}

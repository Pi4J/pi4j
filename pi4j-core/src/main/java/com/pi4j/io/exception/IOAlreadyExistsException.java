package com.pi4j.io.exception;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  IOAlreadyExistsException.java
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
 * Thrown when creating an I/O instance whose identifier or address is already
 * registered in the Pi4J runtime context, since each I/O instance must be
 * uniquely identifiable. A specialization of {@link IOException}.
 *
 * @see <a href="http://www.pi4j.com/">http://www.pi4j.com/</a>
 */
public class IOAlreadyExistsException extends IOException {

    /**
     * Creates an exception reporting that an I/O instance with the given id is already registered.
     *
     * @param id the already-reserved I/O instance identifier that caused the conflict
     */
    public IOAlreadyExistsException(String id){
        super("IO instance [" + id + "] already exists in the Pi4J runtime context; unable to create a new instance using this reserved id.");
    }

    /**
     * Creates an exception reporting that an I/O instance with the given address is already registered.
     *
     * @param address the hardware/bus address already in use by an existing I/O instance
     */
    public IOAlreadyExistsException(int address){
        super("IO instance with address " + address + " already exists in the Pi4J runtime context; unable to create a new instance using this reserved id.");
    }
}

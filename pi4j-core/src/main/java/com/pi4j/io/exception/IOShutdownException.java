package com.pi4j.io.exception;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  IOShutdownException.java
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


import com.pi4j.io.IO;

/**
 * Thrown when an {@link IO} instance fails to shut down cleanly, for example
 * while releasing its underlying hardware resources during Pi4J teardown. A
 * specialization of {@link IOException}.
 *
 * @see <a href="http://www.pi4j.com/">http://www.pi4j.com/</a>
 */
public class IOShutdownException extends IOException {

    /**
     * Creates an exception describing a failed shutdown of a specific I/O instance.
     *
     * @param instance the {@link IO} instance that failed to shut down; its id is included in the message
     * @param e        the underlying error raised during shutdown, retained as the cause
     */
    public IOShutdownException(IO instance, Exception e){
        super("IO instance [" + instance.getId() + "] failed to properly shutdown: " + e.getMessage(), e);
    }
}

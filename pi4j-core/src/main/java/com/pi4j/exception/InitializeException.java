package com.pi4j.exception;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  InitializeException.java
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
 * Thrown when a Pi4J component fails to initialize, for example when the runtime context,
 * a provider, or an I/O instance cannot be brought into a usable state. It is the
 * initialization-phase counterpart of {@link ShutdownException}.
 *
 * @see <a href="http://www.pi4j.com/">http://www.pi4j.com/</a>
 */
public class InitializeException extends Pi4JException {

    /**
     * Creates a new exception with the given detail message.
     *
     * @param message human-readable description of the initialization failure
     */
    public InitializeException(String message){
        super(message);
    }

    /**
     * Creates a new exception wrapping an underlying cause. The cause's message is reused
     * as the detail message.
     *
     * @param cause the underlying throwable that triggered the initialization failure
     */
    public InitializeException(Throwable cause){
        super(cause.getMessage(), cause);
    }

    /**
     * Creates a new exception with the given detail message and underlying cause.
     *
     * @param message human-readable description of the initialization failure
     * @param cause   the underlying throwable that triggered the initialization failure
     */
    public InitializeException(String message, Throwable cause){
        super(message, cause);
    }
}

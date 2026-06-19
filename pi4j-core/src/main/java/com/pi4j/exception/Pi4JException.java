package com.pi4j.exception;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  Pi4JException.java
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
 * Root unchecked exception type for all errors raised by the Pi4J library. All other
 * Pi4J-specific exceptions extend this class, so callers may catch {@code Pi4JException}
 * to handle any failure originating from Pi4J. It is a {@link RuntimeException}, so it is
 * not required to be declared or caught.
 *
 * @see <a href="http://www.pi4j.com/">http://www.pi4j.com/</a>
 */
public class Pi4JException extends RuntimeException {

    /**
     * Creates a new exception with the given detail message.
     *
     * @param message human-readable description of the failure
     */
    public Pi4JException(String message){
        super(message);
    }

    /**
     * Creates a new exception wrapping an underlying cause. The message is inherited from
     * the cause.
     *
     * @param cause the underlying throwable that triggered this failure
     */
    public Pi4JException(Throwable cause){
        super(cause);
    }

    /**
     * Creates a new exception with the given detail message and underlying cause.
     *
     * @param message human-readable description of the failure
     * @param cause   the underlying throwable that triggered this failure
     */
    public Pi4JException(String message, Throwable cause){
        super(message,cause);
    }
}

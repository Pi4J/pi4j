package com.pi4j.io.exception;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  IOException.java
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

import com.pi4j.exception.Pi4JException;

/**
 * Base unchecked exception for failures originating in the Pi4J I/O subsystem
 * (GPIO, I2C, SPI, PWM, serial, and related providers). It extends
 * {@link Pi4JException} and is the common supertype for the more specific I/O
 * exceptions in this package, allowing callers to catch all I/O-related
 * failures uniformly.
 *
 * @see <a href="http://www.pi4j.com/">http://www.pi4j.com/</a>
 */
public class IOException extends Pi4JException {

    /**
     * Creates an exception with a descriptive error message.
     *
     * @param message human-readable description of the I/O failure
     */
    public IOException(String message){
        super(message);
    }

    /**
     * Creates an exception that wraps an underlying cause.
     *
     * @param cause the throwable that triggered this I/O failure
     */
    public IOException(Throwable cause){
        super(cause);
    }

    /**
     * Creates an exception with a descriptive error message and an underlying cause.
     *
     * @param message human-readable description of the I/O failure
     * @param cause   the throwable that triggered this I/O failure
     */
    public IOException(String message, Throwable cause){
        super(message,cause);
    }
}

package com.pi4j.config.exception;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  ConfigEmptyException.java
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
 * Thrown when a configuration is required but is missing or contains no entries.
 * This is a specialized {@link ConfigException}.
 *
 * @see <a href="http://www.pi4j.com/">http://www.pi4j.com/</a>
 */
public class ConfigEmptyException extends ConfigException {

    /** Detail message used for this exception: {@code "Configuration is missing or empty."} */
    public static String MESSAGE =  "Configuration is missing or empty.";

    /**
     * Creates the exception with the default {@link #MESSAGE}.
     */
    public ConfigEmptyException(){
        super(MESSAGE);
    }

    /**
     * Creates the exception with the default {@link #MESSAGE} and an underlying cause.
     *
     * @param cause the underlying throwable that detected the missing or empty configuration
     */
    public ConfigEmptyException(Throwable cause){
        super(MESSAGE, cause);
    }
}

package com.pi4j.config.exception;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  ConfigMissingRequiredKeyException.java
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
 * Thrown when a configuration does not contain a key that is mandatory for the requested operation.
 * The offending key name is appended to the message. This is a specialized {@link ConfigException}.
 *
 * @see <a href="http://www.pi4j.com/">http://www.pi4j.com/</a>
 */
public class ConfigMissingRequiredKeyException extends ConfigException {

    /** Message prefix to which the missing key name is appended: {@code "Configuration is missing a required key: "} */
    public static String MESSAGE =  "Configuration is missing a required key: ";

    /**
     * Creates the exception for a specific missing key.
     *
     * @param key the name of the required configuration key that was not found
     */
    public ConfigMissingRequiredKeyException(String key){
        super(MESSAGE + key);
    }

    /**
     * Creates the exception for a specific missing key, retaining an underlying cause.
     *
     * @param key   the name of the required configuration key that was not found
     * @param cause the underlying throwable that detected the missing key
     */
    public ConfigMissingRequiredKeyException(String key, Throwable cause){
        super(MESSAGE + key, cause);
    }
}

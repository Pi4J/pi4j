package com.pi4j.config.exception;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  ConfigException.java
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
 * Base unchecked exception for all configuration errors raised while building or validating a Pi4J
 * I/O configuration. Subtypes such as {@link ConfigEmptyException}, {@link ConfigMissingPrefixException}
 * and {@link ConfigMissingRequiredKeyException} signal specific configuration faults.
 *
 * @see <a href="http://www.pi4j.com/">http://www.pi4j.com/</a>
 */
public class ConfigException extends RuntimeException {

    /**
     * Creates a configuration exception with a descriptive message.
     *
     * @param message human-readable description of the configuration problem
     */
    public ConfigException(String message){
        super(message);
    }

    /**
     * Creates a configuration exception that wraps an underlying cause.
     *
     * @param cause the underlying throwable that triggered this configuration failure
     */
    public ConfigException(Throwable cause){
        super(cause);
    }

    /**
     * Creates a configuration exception with a descriptive message and an underlying cause.
     *
     * @param message human-readable description of the configuration problem
     * @param cause   the underlying throwable that triggered this configuration failure
     */
    public ConfigException(String message, Throwable cause){
        super(message,cause);
    }
}

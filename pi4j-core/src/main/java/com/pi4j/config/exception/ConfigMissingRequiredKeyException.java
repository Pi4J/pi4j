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
 * <p>
 * This exception is thrown if a platform assignment is attempted when a
 * platform instance has already been assigned.
 * </p>
 *
 * @see <a href="http://www.pi4j.com/">http://www.pi4j.com/</a>
 */
public class ConfigMissingRequiredKeyException extends ConfigException {

    /** Constant <code>MESSAGE="Configuration is missing a required key"{trunked}</code> */
    public static String MESSAGE =  "Configuration is missing a required key: ";

    /**
     * Default Constructor
     */
    public ConfigMissingRequiredKeyException(String key){
        super(MESSAGE + key);
    }

    public ConfigMissingRequiredKeyException(String key, Throwable cause){
        super(MESSAGE + key, cause);
    }
}

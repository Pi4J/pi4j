package com.pi4j.config;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  Config.java
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

import java.util.Map;

/**
 * <p>Config interface.</p>
 *
 * @param <CONFIG_TYPE>
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public interface Config<CONFIG_TYPE> {
    /**
     * Constant <code>ID_KEY="id"</code>
     */
    String ID_KEY = "id";
    /**
     * Constant <code>NAME_KEY="name"</code>
     */
    String NAME_KEY = "name";
    /**
     * Constant <code>DESCRIPTION_KEY="description"</code>
     */
    String DESCRIPTION_KEY = "description";

    /**
     * Underlying raw configuration properties.
     *
     * @return a {@link Map} of {@link String},{@link String} of raw property keys and string values.
     */
    Map<String, String> properties();

    /**
     * <p>id.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String id();

    /**
     * <p>name.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String name();

    /**
     * <p>description.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String description();

    /**
     * <p>validate.</p>
     */
    void validate();

    /**
     * <p>getId.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    default String getId() {
        return this.id();
    }

    /**
     * <p>getName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    default String getName() {
        return this.name();
    }

    /**
     * <p>getDescription.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    default String getDescription() {
        return this.description();
    }

    /**
     * Each IO Type has to provide a unique identifier.
     * Depending on the type of device:
     *
     * <ul>
     *     <li>GPIO: bcm</li>
     *     <li>I2C: bus+address</li>
     *     <li>SPI: bus+channel</li>
     *     <li>PWM: bus+channel</li>
     * </ul>
     *
     * @return Unique identifier.
     */
    int getUniqueIdentifier();
}

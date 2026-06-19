package com.pi4j.config;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  PortConfig.java
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
 * Configuration contract for I/O that is addressed by a named port.
 * Extends the base {@link Config} contract with a single
 * {@code port} property identified by {@link #PORT_KEY}.
 *
 * @param <CONFIG_TYPE> the concrete configuration type, enabling fluent access on subtypes
 */
public interface PortConfig<CONFIG_TYPE extends Config> extends Config<CONFIG_TYPE> {

    /**
     * Property key under which the port value is stored in the configuration.
     */
    String PORT_KEY = "port";

    /**
     * Returns the configured port.
     *
     * @return the port identifier, or {@code null} if no port was configured
     */
    String port();

    /**
     * Returns the configured port; an alias for {@link #port()} following the JavaBeans getter naming convention.
     *
     * @return the port identifier, or {@code null} if no port was configured
     */
    default String getPort() {
        return this.port();
    }
}

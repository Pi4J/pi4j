package com.pi4j.io;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  IOConfig.java
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

import com.pi4j.config.Config;

/**
 * Common configuration contract shared by all Pi4J I/O types.
 * <p>
 * In addition to the generic properties from {@link Config}, it identifies the {@link com.pi4j.provider.Provider}
 * and platform that should service the I/O instance described by this configuration.
 *
 * @param <CONFIG_TYPE> the concrete configuration type, used for self-referential fluent typing
 */
public interface IOConfig<CONFIG_TYPE> extends Config<CONFIG_TYPE> {
    /** Configuration property key identifying the target platform. */
    String PLATFORM_KEY = "platform";
    /** Configuration property key identifying the I/O provider. */
    String PROVIDER_KEY = "provider";

    /**
     * Returns the id of the provider that should create the I/O instance for this configuration.
     *
     * @return the configured provider id, or {@code null} if none was specified
     */
    String provider();

    /**
     * Alias for {@link #provider()} following the JavaBeans getter naming convention.
     *
     * @return the configured provider id, or {@code null} if none was specified
     */
    default String getProvider() {
        return provider();
    }

    /**
     * Returns the id of the platform this configuration targets.
     *
     * @return the configured platform id, or {@code null} if none was specified
     */
    String platform();

    /**
     * Alias for {@link #platform()} following the JavaBeans getter naming convention.
     *
     * @return the configured platform id, or {@code null} if none was specified
     */
    default String getPlatform() {
        return platform();
    }
}

package com.pi4j.context;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  ContextConfig.java
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

import com.pi4j.provider.Provider;

import java.util.Collection;
import java.util.Map;

/**
 * Immutable, read-only view of the settings used to create a Pi4J {@link Context}. Produced by a
 * {@link ContextBuilder} (via {@link ContextBuilder#toConfig()}) and exposed through {@link Context#config()},
 * it captures auto-detection flags, the default platform, the manually added {@link Provider}s and the
 * user-supplied properties.
 *
 * @see ContextBuilder
 * @see Context
 */
public interface ContextConfig {
    /**
     * Indicates whether mock plugins should be auto-detected on the classpath.
     *
     * @return {@code true} if mock plugin auto-detection is enabled, {@code false} otherwise
     */
    boolean autoDetectMockPlugins();

    /**
     * Indicates whether platform implementations should be auto-detected on the classpath.
     *
     * @return {@code true} if platform auto-detection is enabled, {@code false} otherwise
     */
    boolean autoDetectPlatforms();

    /**
     * Bean-style accessor for {@link #autoDetectPlatforms()}.
     *
     * @return {@code true} if platform auto-detection is enabled, {@code false} otherwise
     */
    default boolean getAutoDetectPlatforms() { return autoDetectPlatforms(); };

    /**
     * Bean-style accessor for {@link #autoDetectPlatforms()}.
     *
     * @return {@code true} if platform auto-detection is enabled, {@code false} otherwise
     */
    default boolean isAutoDetectPlatforms() { return autoDetectPlatforms(); };

    /**
     * Indicates whether automatic injection of Pi4J dependencies into annotated members is enabled.
     *
     * @return {@code true} if auto-injection is enabled, {@code false} otherwise
     */
    boolean autoInject();

    /**
     * Indicates whether a JVM shutdown hook is registered to shut the context down automatically when the
     * JVM terminates.
     *
     * @return {@code true} if the shutdown hook is enabled, {@code false} otherwise
     */
    boolean enableShutdownHook();

    /**
     * Bean-style accessor for {@link #autoInject()}.
     *
     * @return {@code true} if auto-injection is enabled, {@code false} otherwise
     */
    default boolean getAutoInject() { return autoInject(); };

    /**
     * Bean-style accessor for {@link #autoInject()}.
     *
     * @return {@code true} if auto-injection is enabled, {@code false} otherwise
     */
    default boolean isAutoInject() { return autoInject(); };

    // **************************************************
    // PROVIDERS
    // **************************************************
    /**
     * Returns the providers that were explicitly added to the configuration (independent of any
     * auto-detected providers).
     *
     * @return the collection of manually configured {@link Provider}s
     */
    Collection<Provider> providers();

    /**
     * Bean-style accessor for {@link #providers()}.
     *
     * @return the collection of manually configured {@link Provider}s
     */
    default Collection<Provider> getProviders(){
        return providers();
    }

    /**
     * Indicates whether provider implementations should be auto-detected on the classpath.
     *
     * @return {@code true} if provider auto-detection is enabled, {@code false} otherwise
     */
    boolean autoDetectProviders();

    /**
     * Bean-style accessor for {@link #autoDetectProviders()}.
     *
     * @return {@code true} if provider auto-detection is enabled, {@code false} otherwise
     */
    default boolean getAutoDetectProviders() { return autoDetectProviders(); };

    /**
     * Bean-style accessor for {@link #autoDetectProviders()}.
     *
     * @return {@code true} if provider auto-detection is enabled, {@code false} otherwise
     */
    default boolean isAutoDetectProviders() { return autoDetectProviders(); };

}

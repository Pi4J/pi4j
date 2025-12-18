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

import com.pi4j.platform.Platform;
import com.pi4j.provider.Provider;

import java.util.Collection;
import java.util.Map;

/**
 * <p>ContextConfig interface.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public interface ContextConfig {

    // **************************************************
    // PLATFORMS
    // **************************************************
    /**
     * <p>platforms.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    Collection<Platform> platforms();
    /**
     * <p>getPlatforms.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    default Collection<Platform> getPlatforms(){
        return platforms();
    }
    /**
     * <p>autoDetectMockPlugins.</p>
     *
     * @return a boolean.
     */
    boolean autoDetectMockPlugins();
    /**
     * <p>autoDetectPlatforms.</p>
     *
     * @return a boolean.
     */
    boolean autoDetectPlatforms();
    /**
     * <p>getAutoDetectPlatforms.</p>
     *
     * @return a boolean.
     */
    default boolean getAutoDetectPlatforms() { return autoDetectPlatforms(); };
    /**
     * <p>isAutoDetectPlatforms.</p>
     *
     * @return a boolean.
     */
    default boolean isAutoDetectPlatforms() { return autoDetectPlatforms(); };
    /**
     * <p>autoInject.</p>
     *
     * @return a boolean.
     */
    boolean autoInject();
    /**
     * <p>enableShutdownHook.</p>
     *
     * @return a boolean.
     */
    boolean enableShutdownHook();
    /**
     * <p>getAutoInject.</p>
     *
     * @return a boolean.
     */
    default boolean getAutoInject() { return autoInject(); };
    /**
     * <p>isAutoInject.</p>
     *
     * @return a boolean.
     */
    default boolean isAutoInject() { return autoInject(); };
    /**
     * <p>defaultPlatform.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String defaultPlatform();
    /**
     * <p>getDefaultPlatform.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    default String getDefaultPlatform(){
        return defaultPlatform();
    }
    /**
     * <p>hasDefaultPlatform.</p>
     *
     * @return a boolean.
     */
    default boolean hasDefaultPlatform(){ return getDefaultPlatform() != null;};

    // **************************************************
    // PROVIDERS
    // **************************************************
    /**
     * <p>providers.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    Collection<Provider> providers();
    /**
     * <p>getProviders.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    default Collection<Provider> getProviders(){
        return providers();
    }
    /**
     * <p>autoDetectProviders.</p>
     *
     * @return a boolean.
     */
    boolean autoDetectProviders();
    /**
     * <p>getAutoDetectProviders.</p>
     *
     * @return a boolean.
     */
    default boolean getAutoDetectProviders() { return autoDetectProviders(); };
    /**
     * <p>isAutoDetectProviders.</p>
     *
     * @return a boolean.
     */
    default boolean isAutoDetectProviders() { return autoDetectProviders(); };

    // **************************************************
    // PROPERTIES
    // **************************************************
    /**
     * <p>properties.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    Map<String,String> properties();
}

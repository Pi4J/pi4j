package com.pi4j.context;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  ContextBuilder.java
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

import com.pi4j.config.Builder;
import com.pi4j.context.impl.DefaultContextBuilder;
import com.pi4j.provider.Provider;

/**
 * Fluent builder used to configure and create a Pi4J {@link Context}. It accumulates settings such as
 * the default platform, auto-detection behaviour for platforms and providers, the shutdown hook, manually
 * added {@link Provider}s and user properties, and finally produces either a {@link ContextConfig} (via
 * {@link #toConfig()}) or a fully initialized {@link Context} (via {@link #build()}). Obtain an instance
 * with {@link #newInstance()}.
 *
 * @see Context
 * @see ContextConfig
 */
public interface ContextBuilder extends Builder<Context> {

    /**
     * Creates a new, empty context builder.
     *
     * @return a fresh {@link ContextBuilder} instance
     */
    static ContextBuilder newInstance(){
        return DefaultContextBuilder.newInstance();
    }

    /**
     * Adds one or more providers to be registered in the resulting context.
     *
     * @param provider the providers to add
     * @return this builder instance for method chaining
     */
    ContextBuilder add(Provider ... provider);

    /**
     * Enables auto-detection of mock plugins on the classpath, primarily useful for testing without real
     * hardware.
     *
     * @return this builder instance for method chaining
     */
    ContextBuilder autoDetectMockPlugins();

    /**
     * Enables auto-detection of platform implementations available on the classpath.
     *
     * @return this builder instance for method chaining
     */
    ContextBuilder autoDetectPlatforms();

    /**
     * Disables auto-detection of platform implementations on the classpath.
     *
     * @return this builder instance for method chaining
     */
    ContextBuilder noAutoDetectPlatforms();

    /**
     * Enables auto-detection of provider implementations available on the classpath.
     *
     * @return this builder instance for method chaining
     */
    ContextBuilder autoDetectProviders();

    /**
     * Disables auto-detection of provider implementations on the classpath.
     *
     * @return this builder instance for method chaining
     */
    ContextBuilder noAutoDetectProviders();

    /**
     * Enables automatic injection of Pi4J dependencies into annotated members of registered objects.
     *
     * @return this builder instance for method chaining
     */
    ContextBuilder autoInject();

    /**
     * Disables automatic injection of Pi4J dependencies.
     *
     * @return this builder instance for method chaining
     */
    ContextBuilder noAutoInject();

    /**
     * Enables or disables automatic dependency injection depending on the given flag.
     *
     * @param autoInject {@code true} to enable auto-injection, {@code false} to disable it
     * @return this builder instance for method chaining
     */
    default ContextBuilder setAutoInject(boolean autoInject){
        if(autoInject)
            return autoInject();
        else
            return noAutoInject();
    }

    /**
     * Enables registration of a JVM shutdown hook that automatically shuts the context down when the JVM
     * terminates.
     *
     * @return this builder instance for method chaining
     */
    ContextBuilder enableShutdownHook();

    /**
     * Disables registration of the JVM shutdown hook, leaving the caller responsible for shutting the
     * context down explicitly.
     *
     * @return this builder instance for method chaining
     */
    ContextBuilder disableShutdownHook();

    /**
     * Enables or disables the JVM shutdown hook depending on the given flag.
     *
     * @param enableShutdownHook {@code true} to register the shutdown hook, {@code false} to skip it
     * @return this builder instance for method chaining
     */
    default ContextBuilder setShutdownHook(boolean enableShutdownHook) {
        if (enableShutdownHook)
            return enableShutdownHook();
        else
            return disableShutdownHook();
    }

    /**
     * Builds an immutable configuration snapshot from the current builder state, without creating a context.
     *
     * @return a {@link ContextConfig} reflecting this builder's settings
     */
    ContextConfig toConfig();

    /**
     * Enables or disables auto-detection of both platforms and providers depending on the given flag.
     *
     * @param autoDetect {@code true} to enable auto-detection, {@code false} to disable it
     * @return this builder instance for method chaining
     */
    default ContextBuilder setAutoDetect(boolean autoDetect){
        if(autoDetect)
            return autoDetect();
        else
            return noAutoDetect();
    }

    /**
     * Enables auto-detection of all extensibility modules (platforms and providers) on the classpath.
     *
     * @return this builder instance for method chaining
     */
    default ContextBuilder autoDetect(){
        // auto detect all extensibility modules in the classpath
        return  autoDetectPlatforms().
                autoDetectProviders();
    }

    /**
     * Disables auto-detection of all extensibility modules (platforms and providers) on the classpath.
     *
     * @return this builder instance for method chaining
     */
    default ContextBuilder noAutoDetect(){
        // do not auto detect all extensibility modules in the classpath
        return  noAutoDetectPlatforms().
                noAutoDetectProviders();
    }

    /**
     * Sets the GPIO chip name to be used by the resulting context.
     *
     * @param chipName the name of the GPIO chip (e.g., "gpiochip0")
     * @return this builder instance for method chaining
     */
    ContextBuilder setGpioChipName(String chipName);
}

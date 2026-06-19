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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Map;
import java.util.Properties;

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
     * Returns the id of the platform currently configured as the default for the resulting context.
     *
     * @return the configured default platform id, or {@code null} if none has been set
     */
    String defaultPlatform();

    /**
     * Sets the platform to use as the default in the resulting context.
     *
     * @param platformId the id of the platform to use as the default
     * @return this builder instance for method chaining
     */
    ContextBuilder defaultPlatform(String platformId);

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
     * Adds a single property key/value pair to the context configuration.
     *
     * @param key   the property key
     * @param value the property value
     * @return this builder instance for method chaining
     */
    ContextBuilder property(String key, String value);

    /**
     * Adds one or more property entries to the context configuration.
     *
     * @param value the property key/value entries to add
     * @return this builder instance for method chaining
     */
    ContextBuilder property(Map.Entry<String,String> ... value);

    /**
     * Adds all entries of the given map as properties of the context configuration.
     *
     * @param values the map of property key/value pairs to add
     * @return this builder instance for method chaining
     */
    ContextBuilder properties(Map<String,String> values);

    /**
     * Adds entries of the given map as properties, keeping only those whose key starts with the given
     * prefix.
     *
     * @param properties   the map of property key/value pairs to add
     * @param prefixFilter the key prefix that an entry must match to be included, or {@code null} for no filtering
     * @return this builder instance for method chaining
     */
    ContextBuilder properties(Map<String,String> properties, String prefixFilter);

    /**
     * Adds entries of the given {@link Properties} as properties, keeping only those whose key starts with
     * the given prefix.
     *
     * @param properties   the properties to add
     * @param prefixFilter the key prefix that an entry must match to be included, or {@code null} for no filtering
     * @return this builder instance for method chaining
     */
    ContextBuilder properties(Properties properties, String prefixFilter);

    /**
     * Loads properties from the given input stream (in {@link Properties} format) and adds those whose key
     * starts with the given prefix.
     *
     * @param stream       the input stream to read properties from
     * @param prefixFilter the key prefix that an entry must match to be included, or {@code null} for no filtering
     * @return this builder instance for method chaining
     * @throws java.io.IOException if an error occurs accessing {@code stream}.
     */
    ContextBuilder properties(InputStream stream, String prefixFilter) throws IOException;

    /**
     * Loads properties from the given reader (in {@link Properties} format) and adds those whose key starts
     * with the given prefix.
     *
     * @param reader       the reader to read properties from
     * @param prefixFilter the key prefix that an entry must match to be included, or {@code null} for no filtering
     * @return this builder instance for method chaining
     * @throws java.io.IOException if an error occurs accessing {@code reader}.
     */
    ContextBuilder properties(Reader reader, String prefixFilter) throws IOException;

    /**
     * Loads properties from the given file (in {@link Properties} format) and adds those whose key starts
     * with the given prefix.
     *
     * @param file         the file to read properties from
     * @param prefixFilter the key prefix that an entry must match to be included, or {@code null} for no filtering
     * @return this builder instance for method chaining
     * @throws java.io.IOException if an error occurs accessing {@code file}.
     */
    ContextBuilder properties(File file, String prefixFilter) throws IOException;

    /**
     * Adds all entries of the given {@link Properties} to the context configuration, without prefix filtering.
     *
     * @param properties the properties to add
     * @return this builder instance for method chaining
     */
    default ContextBuilder properties(Properties properties){
        return properties(properties, null);
    }

    /**
     * Loads and adds all properties from the given input stream, without prefix filtering.
     *
     * @param stream the input stream to read properties from
     * @return this builder instance for method chaining
     * @throws java.io.IOException if an error occurs accessing {@code stream}.
     */
    default ContextBuilder properties(InputStream stream) throws IOException {
        return properties(stream, null);
    }

    /**
     * Loads and adds all properties from the given reader, without prefix filtering.
     *
     * @param reader the reader to read properties from
     * @return this builder instance for method chaining
     * @throws java.io.IOException if an error occurs accessing {@code reader}.
     */
    default ContextBuilder properties(Reader reader) throws IOException {
        return properties(reader, null);
    }

    /**
     * Loads and adds all properties from the given file, without prefix filtering.
     *
     * @param file the file to read properties from
     * @return this builder instance for method chaining
     * @throws java.io.IOException if an error occurs accessing {@code file}.
     */
    default ContextBuilder properties(File file) throws IOException {
        return properties(file, null);
    }

    /**
     * Adds a single property key/value pair. Alias for {@link #property(String, String)}.
     *
     * @param key   the property key
     * @param value the property value
     * @return this builder instance for method chaining
     */
    default ContextBuilder addProperty(String key, String value){
        return property(key, value);
    }

    /**
     * Adds one or more property entries. Alias for {@link #property(Map.Entry[])}.
     *
     * @param value the property key/value entries to add
     * @return this builder instance for method chaining
     */
    default ContextBuilder addProperty(Map.Entry<String,String> ... value){
        return property(value);
    }

    /**
     * Adds properties from the given {@link Properties}, keeping only keys matching the given prefix.
     *
     * @param properties   the properties to add
     * @param prefixFilter the key prefix that an entry must match to be included, or {@code null} for no filtering
     * @return this builder instance for method chaining
     */
    default ContextBuilder addProperties(Properties properties, String prefixFilter){
        return properties(properties, prefixFilter);
    }

    /**
     * Adds all entries of the given {@link Properties}, without prefix filtering.
     *
     * @param properties the properties to add
     * @return this builder instance for method chaining
     */
    default ContextBuilder addProperties(Properties properties){
        return properties(properties, null);
    }

    /**
     * Adds all entries of the given map, without prefix filtering.
     *
     * @param properties the map of property key/value pairs to add
     * @return this builder instance for method chaining
     */
    default ContextBuilder addProperties(Map<String,String> properties){
        return properties(properties, null);
    }

    /**
     * Adds entries of the given map, keeping only keys matching the given prefix.
     *
     * @param properties   the map of property key/value pairs to add
     * @param prefixFilter the key prefix that an entry must match to be included, or {@code null} for no filtering
     * @return this builder instance for method chaining
     */
    default ContextBuilder addProperties(Map<String, String> properties, String prefixFilter){
        return properties(properties, prefixFilter);
    }

    /**
     * Loads and adds all properties from the given input stream, without prefix filtering.
     *
     * @param stream the input stream to read properties from
     * @return this builder instance for method chaining
     * @throws java.io.IOException if an error occurs accessing {@code stream}.
     */
    default ContextBuilder addProperties(InputStream stream) throws IOException{
        return properties(stream, null);
    }

    /**
     * Loads properties from the given input stream, keeping only keys matching the given prefix.
     *
     * @param stream       the input stream to read properties from
     * @param prefixFilter the key prefix that an entry must match to be included, or {@code null} for no filtering
     * @return this builder instance for method chaining
     * @throws java.io.IOException if an error occurs accessing {@code stream}.
     */
    default ContextBuilder addProperties(InputStream stream, String prefixFilter) throws IOException{
        return properties(stream, prefixFilter);
    }

    /**
     * Loads and adds all properties from the given reader, without prefix filtering.
     *
     * @param reader the reader to read properties from
     * @return this builder instance for method chaining
     * @throws java.io.IOException if an error occurs accessing {@code reader}.
     */
    default ContextBuilder addProperties(Reader reader) throws IOException{
        return properties(reader, null);
    }

    /**
     * Loads properties from the given reader, keeping only keys matching the given prefix.
     *
     * @param reader       the reader to read properties from
     * @param prefixFilter the key prefix that an entry must match to be included, or {@code null} for no filtering
     * @return this builder instance for method chaining
     * @throws java.io.IOException if an error occurs accessing {@code reader}.
     */
    default ContextBuilder addProperties(Reader reader, String prefixFilter) throws IOException{
        return properties(reader, prefixFilter);
    }

    /**
     * Loads and adds all properties from the given file, without prefix filtering.
     *
     * @param file the file to read properties from
     * @return this builder instance for method chaining
     * @throws java.io.IOException if an error occurs accessing {@code file}.
     */
    default ContextBuilder addProperties(File file) throws IOException{
        return properties(file, null);
    }

    /**
     * Loads properties from the given file, keeping only keys matching the given prefix.
     *
     * @param file         the file to read properties from
     * @param prefixFilter the key prefix that an entry must match to be included, or {@code null} for no filtering
     * @return this builder instance for method chaining
     * @throws java.io.IOException if an error occurs accessing {@code file}.
     */
    default ContextBuilder addProperties(File file, String prefixFilter) throws IOException{
        return properties(file, prefixFilter);
    }

    /**
     * Adds properties from the given {@link Properties}, keeping only keys matching the given prefix.
     * Alias for {@link #properties(Properties, String)}.
     *
     * @param properties   the properties to add
     * @param prefixFilter the key prefix that an entry must match to be included, or {@code null} for no filtering
     * @return this builder instance for method chaining
     */
    default ContextBuilder add(Properties properties, String prefixFilter){
        return properties(properties, prefixFilter);
    }

    /**
     * Adds all entries of the given {@link Properties}, without prefix filtering. Alias for
     * {@link #properties(Properties)}.
     *
     * @param properties the properties to add
     * @return this builder instance for method chaining
     */
    default ContextBuilder add(Properties properties){
        return properties(properties, null);
    }

    /**
     * Sets the GPIO chip name to be used by the resulting context.
     *
     * @param chipName the name of the GPIO chip (e.g., "gpiochip0")
     * @return this builder instance for method chaining
     */
    ContextBuilder setGpioChipName(String chipName);
}

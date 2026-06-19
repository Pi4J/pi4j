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
 * <p>ContextBuilder interface.</p>
 */
public interface ContextBuilder extends Builder<Context> {

    static ContextBuilder newInstance(){
        return DefaultContextBuilder.newInstance();
    }

    ContextBuilder add(Provider ... provider);

    String defaultPlatform();

    ContextBuilder defaultPlatform(String platformId);

    ContextBuilder autoDetectMockPlugins();

    ContextBuilder autoDetectPlatforms();

    ContextBuilder noAutoDetectPlatforms();

    ContextBuilder autoDetectProviders();

    ContextBuilder noAutoDetectProviders();

    ContextBuilder autoInject();

    ContextBuilder noAutoInject();

    default ContextBuilder setAutoInject(boolean autoInject){
        if(autoInject)
            return autoInject();
        else
            return noAutoInject();
    }

    ContextBuilder enableShutdownHook();

    ContextBuilder disableShutdownHook();

    default ContextBuilder setShutdownHook(boolean enableShutdownHook) {
        if (enableShutdownHook)
            return enableShutdownHook();
        else
            return disableShutdownHook();
    }

    ContextConfig toConfig();

    default ContextBuilder setAutoDetect(boolean autoDetect){
        if(autoDetect)
            return autoDetect();
        else
            return noAutoDetect();
    }

    default ContextBuilder autoDetect(){
        // auto detect all extensibility modules in the classpath
        return  autoDetectPlatforms().
                autoDetectProviders();
    }

    default ContextBuilder noAutoDetect(){
        // do not auto detect all extensibility modules in the classpath
        return  noAutoDetectPlatforms().
                noAutoDetectProviders();
    }

    ContextBuilder property(String key, String value);

    ContextBuilder property(Map.Entry<String,String> ... value);

    ContextBuilder properties(Map<String,String> values);

    ContextBuilder properties(Map<String,String> properties, String prefixFilter);

    ContextBuilder properties(Properties properties, String prefixFilter);

    /**
     * @throws java.io.IOException if an error occurs accessing {@code stream}.
     */
    ContextBuilder properties(InputStream stream, String prefixFilter) throws IOException;

    /**
     * @throws java.io.IOException if an error occurs accessing {@code reader}.
     */
    ContextBuilder properties(Reader reader, String prefixFilter) throws IOException;

    /**
     * @throws java.io.IOException if an error occurs accessing {@code file}.
     */
    ContextBuilder properties(File file, String prefixFilter) throws IOException;

    default ContextBuilder properties(Properties properties){
        return properties(properties, null);
    }

    /**
     * @throws java.io.IOException if an error occurs accessing {@code stream}.
     */
    default ContextBuilder properties(InputStream stream) throws IOException {
        return properties(stream, null);
    }

    /**
     * @throws java.io.IOException if an error occurs accessing {@code reader}.
     */
    default ContextBuilder properties(Reader reader) throws IOException {
        return properties(reader, null);
    }

    /**
     * @throws java.io.IOException if an error occurs accessing {@code file}.
     */
    default ContextBuilder properties(File file) throws IOException {
        return properties(file, null);
    }

    default ContextBuilder addProperty(String key, String value){
        return property(key, value);
    }

    default ContextBuilder addProperty(Map.Entry<String,String> ... value){
        return property(value);
    }

    default ContextBuilder addProperties(Properties properties, String prefixFilter){
        return properties(properties, prefixFilter);
    }

    default ContextBuilder addProperties(Properties properties){
        return properties(properties, null);
    }

    default ContextBuilder addProperties(Map<String,String> properties){
        return properties(properties, null);
    }

    default ContextBuilder addProperties(Map<String, String> properties, String prefixFilter){
        return properties(properties, prefixFilter);
    }

    /**
     * @throws java.io.IOException if an error occurs accessing {@code stream}.
     */
    default ContextBuilder addProperties(InputStream stream) throws IOException{
        return properties(stream, null);
    }

    /**
     * @throws java.io.IOException if an error occurs accessing {@code stream}.
     */
    default ContextBuilder addProperties(InputStream stream, String prefixFilter) throws IOException{
        return properties(stream, prefixFilter);
    }

    /**
     * @throws java.io.IOException if an error occurs accessing {@code reader}.
     */
    default ContextBuilder addProperties(Reader reader) throws IOException{
        return properties(reader, null);
    }

    /**
     * @throws java.io.IOException if an error occurs accessing {@code reader}.
     */
    default ContextBuilder addProperties(Reader reader, String prefixFilter) throws IOException{
        return properties(reader, prefixFilter);
    }

    /**
     * @throws java.io.IOException if an error occurs accessing {@code file}.
     */
    default ContextBuilder addProperties(File file) throws IOException{
        return properties(file, null);
    }

    /**
     * @throws java.io.IOException if an error occurs accessing {@code file}.
     */
    default ContextBuilder addProperties(File file, String prefixFilter) throws IOException{
        return properties(file, prefixFilter);
    }

    default ContextBuilder add(Properties properties, String prefixFilter){
        return properties(properties, prefixFilter);
    }

    default ContextBuilder add(Properties properties){
        return properties(properties, null);
    }

    /**
     * Sets the GPIO chip name to be used by the Context.
     *
     * @param chipName The name of the GPIO chip (e.g., "gpiochip0").
     */
    ContextBuilder setGpioChipName(String chipName);
}

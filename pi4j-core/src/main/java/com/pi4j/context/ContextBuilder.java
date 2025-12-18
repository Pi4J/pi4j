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
import com.pi4j.platform.Platform;
import com.pi4j.provider.Provider;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Map;
import java.util.Properties;

/**
 * <p>ContextBuilder interface.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public interface ContextBuilder extends Builder<Context> {

    /**
     * <p>newInstance.</p>
     *
     * @return a {@link com.pi4j.context.ContextBuilder} object.
     */
    static ContextBuilder newInstance(){
        return DefaultContextBuilder.newInstance();
    }

    /**
     * <p>add.</p>
     *
     * @param platform a {@link com.pi4j.platform.Platform} object.
     * @return a {@link com.pi4j.context.ContextBuilder} object.
     */
    ContextBuilder add(Platform ... platform);

    /**
     * <p>add.</p>
     *
     * @param provider a {@link com.pi4j.provider.Provider} object.
     * @return a {@link com.pi4j.context.ContextBuilder} object.
     */
    ContextBuilder add(Provider ... provider);

    /**
     * <p>defaultPlatform.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String defaultPlatform();

    /**
     * <p>defaultPlatform.</p>
     *
     * @param platformId a {@link java.lang.String} object.
     * @return a {@link com.pi4j.context.ContextBuilder} object.
     */
    ContextBuilder defaultPlatform(String platformId);

    /**
     * <p>autoDetectMockPlugins.</p>
     *
     * @return a {@link com.pi4j.context.ContextBuilder} object.
     */
    ContextBuilder autoDetectMockPlugins();

    /**
     * <p>autoDetectPlatforms.</p>
     *
     * @return a {@link com.pi4j.context.ContextBuilder} object.
     */
    ContextBuilder autoDetectPlatforms();

    /**
     * <p>noAutoDetectPlatforms.</p>
     *
     * @return a {@link com.pi4j.context.ContextBuilder} object.
     */
    ContextBuilder noAutoDetectPlatforms();

    /**
     * <p>autoDetectProviders.</p>
     *
     * @return a {@link com.pi4j.context.ContextBuilder} object.
     */
    ContextBuilder autoDetectProviders();

    /**
     * <p>noAutoDetectProviders.</p>
     *
     * @return a {@link com.pi4j.context.ContextBuilder} object.
     */
    ContextBuilder noAutoDetectProviders();

    /**
     * <p>autoInject.</p>
     *
     * @return a {@link com.pi4j.context.ContextBuilder} object.
     */
    ContextBuilder autoInject();

    /**
     * <p>noAutoInject.</p>
     *
     * @return a {@link com.pi4j.context.ContextBuilder} object.
     */
    ContextBuilder noAutoInject();

    /**
     * <p>setAutoInject.</p>
     *
     * @param autoInject a boolean.
     * @return a {@link com.pi4j.context.ContextBuilder} object.
     */
    default ContextBuilder setAutoInject(boolean autoInject){
        if(autoInject)
            return autoInject();
        else
            return noAutoInject();
    }

    /**
     * <p>enableShutdownHook.</p>
     *
     * @return a {@link com.pi4j.context.ContextBuilder} object.
     */
    ContextBuilder enableShutdownHook();

    /**
     * <p>disableShutdownHook.</p>
     *
     * @return a {@link com.pi4j.context.ContextBuilder} object.
     */
    ContextBuilder disableShutdownHook();

    /**
     * <p>setShutdownHook.</p>
     *
     * @param enableShutdownHook a boolean.
     *
     * @return a {@link com.pi4j.context.ContextBuilder} object.
     */
    default ContextBuilder setShutdownHook(boolean enableShutdownHook) {
        if (enableShutdownHook)
            return enableShutdownHook();
        else
            return disableShutdownHook();
    }

    /**
     * <p>toConfig.</p>
     *
     * @return a {@link com.pi4j.context.ContextConfig} object.
     */
    ContextConfig toConfig();

    /**
     * <p>setAutoDetect.</p>
     *
     * @param autoDetect a boolean.
     * @return a {@link com.pi4j.context.ContextBuilder} object.
     */
    default ContextBuilder setAutoDetect(boolean autoDetect){
        if(autoDetect)
            return autoDetect();
        else
            return noAutoDetect();
    }

    /**
     * <p>autoDetect.</p>
     *
     * @return a {@link com.pi4j.context.ContextBuilder} object.
     */
    default ContextBuilder autoDetect(){
        // auto detect all extensibility modules in the classpath
        return  autoDetectPlatforms().
                autoDetectProviders();
    }

    /**
     * <p>noAutoDetect.</p>
     *
     * @return a {@link com.pi4j.context.ContextBuilder} object.
     */
    default ContextBuilder noAutoDetect(){
        // do not auto detect all extensibility modules in the classpath
        return  noAutoDetectPlatforms().
                noAutoDetectProviders();
    }

    /**
     * <p>addPlatform.</p>
     *
     * @param platform a {@link com.pi4j.platform.Platform} object.
     * @return a {@link com.pi4j.context.ContextBuilder} object.
     */
    default ContextBuilder addPlatform(Platform ... platform){
        return add(platform);
    }

    /**
     * <p>addPlatform.</p>
     *
     * @param provider a {@link com.pi4j.provider.Provider} object.
     * @return a {@link com.pi4j.context.ContextBuilder} object.
     */
    default ContextBuilder addPlatform(Provider ... provider){
        return add(provider);
    }

    /**
     * <p>addDefaultPlatform.</p>
     *
     * @param platform a {@link com.pi4j.platform.Platform} object.
     * @return a {@link com.pi4j.context.ContextBuilder} object.
     */
    default ContextBuilder addDefaultPlatform(Platform platform){
        return this.add(platform).defaultPlatform(platform.id());
    }

    /**
     * <p>defaultPlatform.</p>
     *
     * @param platform a {@link com.pi4j.platform.Platform} object.
     * @return a {@link com.pi4j.context.ContextBuilder} object.
     */
    default ContextBuilder defaultPlatform(Platform platform){
        return defaultPlatform(platform.id());
    }

    /**
     * <p>setDefaultPlatform.</p>
     *
     * @param platformId a {@link java.lang.String} object.
     * @return a {@link com.pi4j.context.ContextBuilder} object.
     */
    default ContextBuilder setDefaultPlatform(String platformId){
        return defaultPlatform(platformId);
    }

    /**
     * <p>setDefaultPlatform.</p>
     *
     * @param platform a {@link com.pi4j.platform.Platform} object.
     * @return a {@link com.pi4j.context.ContextBuilder} object.
     */
    default ContextBuilder setDefaultPlatform(Platform platform){
        return defaultPlatform(platform);
    }

    /**
     * <p>property.</p>
     *
     * @param key a {@link java.lang.String} object.
     * @param value a {@link java.lang.String} object.
     * @return a {@link com.pi4j.context.ContextBuilder} object.
     */
    ContextBuilder property(String key, String value);

    /**
     * <p>property.</p>
     *
     * @param value a {@link java.util.Map.Entry} object.
     * @return a {@link com.pi4j.context.ContextBuilder} object.
     */
    ContextBuilder property(Map.Entry<String,String> ... value);

    /**
     * <p>properties.</p>
     *
     * @param values a {@link java.util.Map} object.
     * @return a {@link com.pi4j.context.ContextBuilder} object.
     */
    ContextBuilder properties(Map<String,String> values);

    /**
     * <p>properties.</p>
     *
     * @param properties a {@link java.util.Map} object.
     * @param prefixFilter a {@link java.lang.String} object.
     * @return a {@link com.pi4j.context.ContextBuilder} object.
     */
    ContextBuilder properties(Map<String,String> properties, String prefixFilter);

    /**
     * <p>properties.</p>
     *
     * @param properties a {@link java.util.Properties} object.
     * @param prefixFilter a {@link java.lang.String} object.
     * @return a {@link com.pi4j.context.ContextBuilder} object.
     */
    ContextBuilder properties(Properties properties, String prefixFilter);

    /**
     * <p>properties.</p>
     *
     * @param stream a {@link java.io.InputStream} object.
     * @param prefixFilter a {@link java.lang.String} object.
     * @return a {@link com.pi4j.context.ContextBuilder} object.
     * @throws java.io.IOException if an error occurs accessing {@code stream}.
     */
    ContextBuilder properties(InputStream stream, String prefixFilter) throws IOException;

    /**
     * <p>properties.</p>
     *
     * @param reader a {@link java.io.Reader} object.
     * @param prefixFilter a {@link java.lang.String} object.
     * @return a {@link com.pi4j.context.ContextBuilder} object.
     * @throws java.io.IOException if an error occurs accessing {@code reader}.
     */
    ContextBuilder properties(Reader reader, String prefixFilter) throws IOException;

    /**
     * <p>properties.</p>
     *
     * @param file a {@link java.io.File} object.
     * @param prefixFilter a {@link java.lang.String} object.
     * @return a {@link com.pi4j.context.ContextBuilder} object.
     * @throws java.io.IOException if an error occurs accessing {@code file}.
     */
    ContextBuilder properties(File file, String prefixFilter) throws IOException;

    /**
     * <p>properties.</p>
     *
     * @param properties a {@link java.util.Properties} object.
     * @return a {@link com.pi4j.context.ContextBuilder} object.
     */
    default ContextBuilder properties(Properties properties){
        return properties(properties, null);
    }

    /**
     * <p>properties.</p>
     *
     * @param stream a {@link java.io.InputStream} object.
     * @return a {@link com.pi4j.context.ContextBuilder} object.
     * @throws java.io.IOException if an error occurs accessing {@code stream}.
     */
    default ContextBuilder properties(InputStream stream) throws IOException {
        return properties(stream, null);
    }

    /**
     * <p>properties.</p>
     *
     * @param reader a {@link java.io.Reader} object.
     * @return a {@link com.pi4j.context.ContextBuilder} object.
     * @throws java.io.IOException if an error occurs accessing {@code reader}.
     */
    default ContextBuilder properties(Reader reader) throws IOException {
        return properties(reader, null);
    }

    /**
     * <p>properties.</p>
     *
     * @param file a {@link java.io.File} object.
     * @return a {@link com.pi4j.context.ContextBuilder} object.
     * @throws java.io.IOException if an error occurs accessing {@code file}.
     */
    default ContextBuilder properties(File file) throws IOException {
        return properties(file, null);
    }

    /**
     * <p>addProperty.</p>
     *
     * @param key a {@link java.lang.String} object.
     * @param value a {@link java.lang.String} object.
     * @return a {@link com.pi4j.context.ContextBuilder} object.
     */
    default ContextBuilder addProperty(String key, String value){
        return property(key, value);
    }

    /**
     * <p>addProperty.</p>
     *
     * @param value a {@link java.util.Map.Entry} object.
     * @return a {@link com.pi4j.context.ContextBuilder} object.
     */
    default ContextBuilder addProperty(Map.Entry<String,String> ... value){
        return property(value);
    }

    /**
     * <p>addProperties.</p>
     *
     * @param properties a {@link java.util.Properties} object.
     * @param prefixFilter a {@link java.lang.String} object.
     * @return a {@link com.pi4j.context.ContextBuilder} object.
     */
    default ContextBuilder addProperties(Properties properties, String prefixFilter){
        return properties(properties, prefixFilter);
    }

    /**
     * <p>addProperties.</p>
     *
     * @param properties a {@link java.util.Properties} object.
     * @return a {@link com.pi4j.context.ContextBuilder} object.
     */
    default ContextBuilder addProperties(Properties properties){
        return properties(properties, null);
    }

    /**
     * <p>addProperties.</p>
     *
     * @param properties a {@link java.util.Map} object.
     * @return a {@link com.pi4j.context.ContextBuilder} object.
     */
    default ContextBuilder addProperties(Map<String,String> properties){
        return properties(properties, null);
    }

    /**
     * <p>addProperties.</p>
     *
     * @param properties a {@link java.util.Map} object.
     * @param prefixFilter a {@link java.lang.String} object.
     * @return a {@link com.pi4j.context.ContextBuilder} object.
     */
    default ContextBuilder addProperties(Map<String, String> properties, String prefixFilter){
        return properties(properties, prefixFilter);
    }

    /**
     * <p>addProperties.</p>
     *
     * @param stream a {@link java.io.InputStream} object.
     * @return a {@link com.pi4j.context.ContextBuilder} object.
     * @throws java.io.IOException if an error occurs accessing {@code stream}.
     */
    default ContextBuilder addProperties(InputStream stream) throws IOException{
        return properties(stream, null);
    }

    /**
     * <p>addProperties.</p>
     *
     * @param stream a {@link java.io.InputStream} object.
     * @param prefixFilter a {@link java.lang.String} object.
     * @return a {@link com.pi4j.context.ContextBuilder} object.
     * @throws java.io.IOException if an error occurs accessing {@code stream}.
     */
    default ContextBuilder addProperties(InputStream stream, String prefixFilter) throws IOException{
        return properties(stream, prefixFilter);
    }

    /**
     * <p>addProperties.</p>
     *
     * @param reader a {@link java.io.Reader} object.
     * @return a {@link com.pi4j.context.ContextBuilder} object.
     * @throws java.io.IOException if an error occurs accessing {@code reader}.
     */
    default ContextBuilder addProperties(Reader reader) throws IOException{
        return properties(reader, null);
    }

    /**
     * <p>addProperties.</p>
     *
     * @param reader a {@link java.io.Reader} object.
     * @param prefixFilter a {@link java.lang.String} object.
     * @return a {@link com.pi4j.context.ContextBuilder} object.
     * @throws java.io.IOException if an error occurs accessing {@code reader}.
     */
    default ContextBuilder addProperties(Reader reader, String prefixFilter) throws IOException{
        return properties(reader, prefixFilter);
    }

    /**
     * <p>addProperties.</p>
     *
     * @param file a {@link java.io.File} object.
     * @return a {@link com.pi4j.context.ContextBuilder} object.
     * @throws java.io.IOException if an error occurs accessing {@code file}.
     */
    default ContextBuilder addProperties(File file) throws IOException{
        return properties(file, null);
    }

    /**
     * <p>addProperties.</p>
     *
     * @param file a {@link java.io.File} object.
     * @param prefixFilter a {@link java.lang.String} object.
     * @return a {@link com.pi4j.context.ContextBuilder} object.
     * @throws java.io.IOException if an error occurs accessing {@code file}.
     */
    default ContextBuilder addProperties(File file, String prefixFilter) throws IOException{
        return properties(file, prefixFilter);
    }

    /**
     * <p>add.</p>
     *
     * @param properties a {@link java.util.Properties} object.
     * @param prefixFilter a {@link java.lang.String} object.
     * @return a {@link com.pi4j.context.ContextBuilder} object.
     */
    default ContextBuilder add(Properties properties, String prefixFilter){
        return properties(properties, prefixFilter);
    }

    /**
     * <p>add.</p>
     *
     * @param properties a {@link java.util.Properties} object.
     * @return a {@link com.pi4j.context.ContextBuilder} object.
     */
    default ContextBuilder add(Properties properties){
        return properties(properties, null);
    }

    /**
     * Sets the GPIO chip name to be used by GpioDContext.
     *
     * @param chipName The name of the GPIO chip (e.g., "gpiochip0").
     * @return a {@link com.pi4j.context.ContextBuilder} object.
     */
    ContextBuilder setGpioChipName(String chipName);
}

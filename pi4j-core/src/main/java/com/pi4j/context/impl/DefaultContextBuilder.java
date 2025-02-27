package com.pi4j.context.impl;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  DefaultContextBuilder.java
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

import com.pi4j.boardinfo.util.BoardInfoHelper;
import com.pi4j.context.Context;
import com.pi4j.context.ContextBuilder;
import com.pi4j.context.ContextConfig;
import com.pi4j.exception.Pi4JException;
import com.pi4j.platform.Platform;
import com.pi4j.provider.Provider;
import com.pi4j.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>DefaultContextBuilder class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public class DefaultContextBuilder implements ContextBuilder {

    protected Logger logger = LoggerFactory.getLogger(DefaultContextBuilder.class);

    // auto detection flags
    protected boolean autoDetectMockPlugins = !BoardInfoHelper.runningOnRaspberryPi();
    protected boolean autoDetectPlatforms = false;
    protected boolean autoDetectProviders = false;
    protected boolean autoInject = false;
    protected boolean enableShutdownHook = false;

    // default platform identifier
    protected String defaultPlatformId = null;

    // extensibility modules
    protected Collection<Platform> platforms = Collections.synchronizedList(new ArrayList<>());
    protected Collection<Provider> providers = Collections.synchronizedList(new ArrayList<>());

    // properties
    protected Map<String,String> properties = Collections.synchronizedMap(new HashMap<>());

    // Add a field to store the GPIO chip name
    protected String gpioChipName;

    /**
     * Private Constructor
     */
    private DefaultContextBuilder(){
        // forbid object construction
    }

    /**
     * <p>newInstance.</p>
     *
     * @return a {@link com.pi4j.context.ContextBuilder} object.
     */
    public static ContextBuilder newInstance(){
        return new DefaultContextBuilder();
    }

    /** {@inheritDoc} */
    @Override
    public ContextBuilder add(Platform... platform) {
        if(platform != null && platform.length > 0)
            this.platforms.addAll(List.of(platform));
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public ContextBuilder add(Provider... provider) {
        if(provider != null && provider.length > 0)
            this.providers.addAll(List.of(provider));
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public String defaultPlatform() {
        return this.defaultPlatformId;
    }

    /** {@inheritDoc} */
    @Override
    public ContextBuilder defaultPlatform(String platformId) {
        this.defaultPlatformId = platformId;
        return this;
    }

    public ContextBuilder autoDetectMockPlugins() {
        this.autoDetectMockPlugins = true;
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public ContextBuilder autoDetectPlatforms() {
        this.autoDetectPlatforms = true;
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public ContextBuilder noAutoDetectPlatforms() {
        this.autoDetectPlatforms = false;
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public ContextBuilder autoDetectProviders() {
        this.autoDetectProviders = true;
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public ContextBuilder noAutoDetectProviders() {
        this.autoDetectProviders = false;
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public ContextBuilder autoInject() {
        this.autoInject = true;
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public ContextBuilder noAutoInject() {
        this.autoInject = false;
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public ContextBuilder enableShutdownHook() {
        this.enableShutdownHook = true;
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public ContextBuilder disableShutdownHook() {
        this.enableShutdownHook = false;
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public ContextBuilder property(String key, String value){
        this.properties.put(key, value);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public ContextBuilder property(Map.Entry<String,String> ... value){
        for(Map.Entry e : value){
            this.properties.put(e.getKey().toString(), e.getValue().toString());
        }
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public ContextBuilder properties(Properties properties, String prefixFilter){
        // convert java.util.Properties to a Map<String,String> object
        Map<String, String> entries = properties.keySet().stream()
                .collect(Collectors.toMap(k->k.toString(), key->properties.get(key).toString()));
        return properties(entries, prefixFilter);
    }

    /** {@inheritDoc} */
    @Override
    public ContextBuilder properties(Map<String,String> properties) {
        this.properties.putAll(properties);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public ContextBuilder properties(Map<String,String> properties, String prefixFilter){

        // if a filter was not provided, then load properties without a filter
        if(StringUtil.isNullOrEmpty(prefixFilter)) return properties(properties);

        // sanitize the prefix filter and make sure it includes a "." character at the end
        var prefix = (prefixFilter.endsWith(".")) ? prefixFilter : prefixFilter+".";

        // iterate the properties object and assign any key with the prefix filter to this config
        properties.keySet().stream().filter(key -> key.startsWith(prefix)).forEach((key)->{
            this.properties.put(key.substring(prefix.length()), properties.get(key));
        });
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public ContextBuilder properties(InputStream stream, String prefixFilter) throws IOException{
        Properties prop = new Properties();
        prop.load(stream);
        return properties(prop, prefixFilter);
    }

    /** {@inheritDoc} */
    @Override
    public ContextBuilder properties(Reader reader, String prefixFilter) throws IOException{
        Properties prop = new Properties();
        prop.load(reader);
        return properties(prop, prefixFilter);
    }

    /** {@inheritDoc} */
    @Override
    public ContextBuilder properties(File file, String prefixFilter) throws IOException{
        Properties prop = new Properties();
        prop.load(new FileInputStream(file));
        return properties(prop, prefixFilter);
    }

    /** {@inheritDoc} */
    @Override
    public ContextBuilder setGpioChipName(String chipName) {
        this.gpioChipName = chipName;
        this.properties.put("gpio.chip.name", chipName);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public ContextConfig toConfig() {
        // set instance reference
        var builder = this;

        // create a new context configuration object
        return new ContextConfig() {
            @Override
            public Collection<Platform> platforms() {
                return Collections.unmodifiableCollection(builder.platforms);
            }

            @Override
            public Collection<Provider> providers() {
                return Collections.unmodifiableCollection(builder.providers);
            }

            @Override
            public String defaultPlatform() {
                return builder.defaultPlatformId;
            }

            @Override
            public boolean autoDetectMockPlugins() {
                return builder.autoDetectMockPlugins;
            }

            @Override
            public boolean autoDetectPlatforms() {
                return builder.autoDetectPlatforms;
            }

            @Override
            public boolean enableShutdownHook() {
                return builder.enableShutdownHook;
            }

            @Override
            public boolean autoInject() { return builder.autoInject; }

            @Override
            public boolean autoDetectProviders() {
                return builder.autoDetectProviders;
            }

            @Override
            public Map<String, String> properties() {
                return Collections.unmodifiableMap(builder.properties);
            }
        };
    }

    /** {@inheritDoc} */
    @Override
    public Context build() throws Pi4JException {
        logger.trace("invoked 'build()'");

        // create new context
        Context context = DefaultContext.newInstance(this.toConfig());

        // return the newly created context
        logger.debug("Pi4J successfully created and initialized a new runtime 'Context'.'");
        return context;
    }
}

package com.pi4j.config.impl;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  ConfigBuilderBase.java
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
import com.pi4j.config.ConfigBuilder;
import com.pi4j.context.Context;
import com.pi4j.util.PropertiesUtil;
import com.pi4j.util.StringUtil;

import java.io.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * <p>Abstract ConfigBuilderBase class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 * @param <BUILDER_TYPE>
 * @param <CONFIG_TYPE>
 */
public abstract class ConfigBuilderBase<BUILDER_TYPE extends ConfigBuilder, CONFIG_TYPE extends Config>
        implements ConfigBuilder<BUILDER_TYPE, CONFIG_TYPE> {

    // private configuration variables
    protected final ConcurrentHashMap<String, String> properties = new ConcurrentHashMap<>();
    protected Boolean inheritProperties = true;
    protected Context context = null;

    /**
     * PRIVATE CONSTRUCTOR
     */
    protected ConfigBuilderBase(Context context){
        this.context = context;
    }

    /** {@inheritDoc} */
    @Override
    public BUILDER_TYPE id(String id){
        this.properties.put(Config.ID_KEY, id);
        return (BUILDER_TYPE) this;
    }

    /** {@inheritDoc} */
    @Override
    public String id(){
        return this.properties.get(Config.ID_KEY);
    }

    /** {@inheritDoc} */
    @Override
    public BUILDER_TYPE name(String name){
        this.properties.put(Config.NAME_KEY, name);
        return (BUILDER_TYPE) this;
    }

    /** {@inheritDoc} */
    @Override
    public BUILDER_TYPE description(String description){
        this.properties.put(Config.DESCRIPTION_KEY, description);
        return (BUILDER_TYPE) this;
    }

    /** {@inheritDoc} */
    public BUILDER_TYPE inheritProperties(Boolean allow){
        this.inheritProperties = allow;
        return (BUILDER_TYPE) this;
    }

    /** {@inheritDoc} */
    @Override
    public BUILDER_TYPE load(Map<String, String> properties) {
        this.properties.putAll(properties);
        return (BUILDER_TYPE) this;
    }

    /** {@inheritDoc} */
    @Override
    public BUILDER_TYPE load(Map<String, String> properties, String prefixFilter) {
        // if a filter was not provided, then load properties without a filter
        if(StringUtil.isNullOrEmpty(prefixFilter)) return load(properties);

        // sanitize the prefix filter and make sure it includes a "." character at the end
        var prefix = (prefixFilter.endsWith(".")) ? prefixFilter : prefixFilter+".";

        // iterate the properties object and assign any key with the prefix filter to this config
        properties.keySet().stream().filter(key -> key.startsWith(prefix)).forEach((key)->{
            this.properties.put(key.substring(prefix.length()), properties.get(key));
        });

        // return this config class
        return (BUILDER_TYPE) this;
    }

    /** {@inheritDoc} */
    @Override
    public BUILDER_TYPE load(Properties properties) {
        return load(properties, null);
    }

    /** {@inheritDoc} */
    @Override
    public BUILDER_TYPE load(Properties properties, String prefixFilter) {
        // convert java.util.Properties to a Map<String,String> object
        Map<String, String> entries = properties.keySet().stream()
                .collect(Collectors.toMap(k->k.toString(), key->properties.get(key).toString()));
        return load(entries, prefixFilter);
    }

    /** {@inheritDoc} */
    @Override
    public BUILDER_TYPE load(InputStream stream) throws IOException {
        return load(stream, null);
    }

    /** {@inheritDoc} */
    @Override
    public BUILDER_TYPE load(InputStream stream, String prefixFilter) throws IOException {
        Properties prop = new Properties();
        prop.load(stream);
        return load(prop, prefixFilter);
    }

    /** {@inheritDoc} */
    @Override
    public BUILDER_TYPE load(Reader reader) throws IOException {
        return load(reader, null);
    }

    /** {@inheritDoc} */
    @Override
    public BUILDER_TYPE load(Reader reader, String prefixFilter) throws IOException {
        Properties prop = new Properties();
        prop.load(reader);
        return load(prop, prefixFilter);
    }

    /** {@inheritDoc} */
    @Override
    public BUILDER_TYPE load(File file) throws IOException {
        return load(file, null);
    }

    /** {@inheritDoc} */
    @Override
    public BUILDER_TYPE load(File file, String prefixFilter) throws IOException {
        Properties prop = new Properties();
        prop.load(new FileInputStream(file));
        return load(prop, prefixFilter);
    }

    protected  Map<String,String> getResolvedProperties(){
        Map<String,String> allProperties = new HashMap<>(this.properties);

        // if the configuration object allows inherited properties ... AND
        // the configuration object 'id' is defined (valid), then=
        // we can augment the config with inherited properties now
        if(this.inheritProperties && StringUtil.isNotNullOrEmpty(id())){

            // get property candidates from context properties that may
            // be applicable/eligible for this IO instance (by 'id')
            Map<String,String> candidateProperties = PropertiesUtil.subProperties(context.properties().all(), id());

            // make sure there are eligible candidate and then iterate over the candidates
            // and check each one to make sure the property is not already defined for this
            // configuration object instance and then add the property to this instance and
            // add it to the applied properties return map collection
            if(!candidateProperties.isEmpty()) {
                candidateProperties.forEach((key,value)->{
                    if(!allProperties.containsKey(key)){
                        allProperties.put(key, value);
                    }
                });
            }
        }

        // return a copy of all the resolved properties
        return Collections.unmodifiableMap(allProperties);
    }
}

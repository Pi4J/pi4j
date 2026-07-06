package com.pi4j.config.impl;

import com.pi4j.config.Config;
import com.pi4j.config.ConfigBuilder;
import com.pi4j.context.Context;
import com.pi4j.util.StringUtil;

import java.io.*;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * <p>Abstract ConfigBuilderBase class.</p>
 *
 * @param <BUILDER_TYPE>
 * @param <CONFIG_TYPE>
 */
public abstract class ConfigBuilderBase<BUILDER_TYPE extends ConfigBuilder, CONFIG_TYPE extends Config>
        implements ConfigBuilder<BUILDER_TYPE, CONFIG_TYPE> {

    // private configuration variables
    protected final ConcurrentHashMap<String, String> properties = new ConcurrentHashMap<>();

    /**
     * PRIVATE CONSTRUCTOR
     */
    @Deprecated
    protected ConfigBuilderBase(Context context){
    }

    protected ConfigBuilderBase() {
    }

    @Override
    public BUILDER_TYPE id(String id){
        this.properties.put(Config.ID_KEY, id);
        return (BUILDER_TYPE) this;
    }

    @Override
    public String id(){
        return this.properties.get(Config.ID_KEY);
    }

    @Override
    public BUILDER_TYPE name(String name){
        this.properties.put(Config.NAME_KEY, name);
        return (BUILDER_TYPE) this;
    }

    @Override
    public BUILDER_TYPE description(String description){
        this.properties.put(Config.DESCRIPTION_KEY, description);
        return (BUILDER_TYPE) this;
    }

    @Override
    public BUILDER_TYPE load(Map<String, String> properties) {
        this.properties.putAll(properties);
        return (BUILDER_TYPE) this;
    }

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

    @Override
    public BUILDER_TYPE load(Properties properties) {
        return load(properties, null);
    }

    @Override
    public BUILDER_TYPE load(Properties properties, String prefixFilter) {
        // convert java.util.Properties to a Map<String,String> object
        Map<String, String> entries = properties.keySet().stream()
                .collect(Collectors.toMap(k->k.toString(), key->properties.get(key).toString()));
        return load(entries, prefixFilter);
    }

    @Override
    public BUILDER_TYPE load(InputStream stream) throws IOException {
        return load(stream, null);
    }

    @Override
    public BUILDER_TYPE load(InputStream stream, String prefixFilter) throws IOException {
        Properties prop = new Properties();
        prop.load(stream);
        return load(prop, prefixFilter);
    }

    @Override
    public BUILDER_TYPE load(Reader reader) throws IOException {
        return load(reader, null);
    }

    @Override
    public BUILDER_TYPE load(Reader reader, String prefixFilter) throws IOException {
        Properties prop = new Properties();
        prop.load(reader);
        return load(prop, prefixFilter);
    }

    @Override
    public BUILDER_TYPE load(File file) throws IOException {
        return load(file, null);
    }

    @Override
    public BUILDER_TYPE load(File file, String prefixFilter) throws IOException {
        Properties prop = new Properties();
        prop.load(new FileInputStream(file));
        return load(prop, prefixFilter);
    }

    protected  Map<String,String> getResolvedProperties(){
        return Collections.unmodifiableMap(this.properties);
    }
}

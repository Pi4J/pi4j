package com.pi4j.context.impl;

import com.pi4j.boardinfo.util.BoardInfoHelper;
import com.pi4j.context.Context;
import com.pi4j.context.ContextBuilder;
import com.pi4j.context.ContextConfig;
import com.pi4j.exception.Pi4JException;
import com.pi4j.provider.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

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

    public static ContextBuilder newInstance(){
        return new DefaultContextBuilder();
    }

    @Override
    public ContextBuilder add(Provider... provider) {
        if(provider != null && provider.length > 0)
            this.providers.addAll(List.of(provider));
        return this;
    }

    public ContextBuilder autoDetectMockPlugins() {
        this.autoDetectMockPlugins = true;
        return this;
    }

    @Override
    public ContextBuilder autoDetectPlatforms() {
        this.autoDetectPlatforms = true;
        return this;
    }

    @Override
    public ContextBuilder noAutoDetectPlatforms() {
        this.autoDetectPlatforms = false;
        return this;
    }

    @Override
    public ContextBuilder autoDetectProviders() {
        this.autoDetectProviders = true;
        return this;
    }

    @Override
    public ContextBuilder noAutoDetectProviders() {
        this.autoDetectProviders = false;
        return this;
    }

    @Override
    public ContextBuilder autoInject() {
        this.autoInject = true;
        return this;
    }

    @Override
    public ContextBuilder noAutoInject() {
        this.autoInject = false;
        return this;
    }

    @Override
    public ContextBuilder enableShutdownHook() {
        this.enableShutdownHook = true;
        return this;
    }

    @Override
    public ContextBuilder disableShutdownHook() {
        this.enableShutdownHook = false;
        return this;
    }

    @Override
    public ContextBuilder setGpioChipName(String chipName) {
        this.gpioChipName = chipName;
        return this;
    }

    @Override
    public ContextConfig toConfig() {
        // set instance reference
        var builder = this;

        // create a new context configuration object
        return new ContextConfig() {
            @Override
            public Collection<Provider> providers() {
                return Collections.unmodifiableCollection(builder.providers);
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
        };
    }

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

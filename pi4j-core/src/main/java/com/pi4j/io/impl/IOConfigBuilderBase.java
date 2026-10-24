package com.pi4j.io.impl;

import com.pi4j.config.Config;
import com.pi4j.config.ConfigBuilder;
import com.pi4j.config.impl.ConfigBuilderBase;
import com.pi4j.io.IOConfig;
import com.pi4j.io.IOConfigBuilder;
import com.pi4j.provider.Provider;

/**
 * <p>Abstract AddressConfigBuilderBase class.</p>
 *
 * @param <BUILDER_TYPE>
 * @param <CONFIG_TYPE>
 */
public abstract class IOConfigBuilderBase<
    BUILDER_TYPE extends ConfigBuilder<BUILDER_TYPE, CONFIG_TYPE>,
    CONFIG_TYPE extends Config
    >
    extends ConfigBuilderBase<BUILDER_TYPE, CONFIG_TYPE>
    implements IOConfigBuilder<BUILDER_TYPE, CONFIG_TYPE>
{
    /**
     * PRIVATE CONSTRUCTOR
     */
    protected IOConfigBuilderBase(){
    }

    @SuppressWarnings("unchecked")
    @Override
    public BUILDER_TYPE provider(String provider){
        this.properties.put(IOConfig.PROVIDER_KEY, provider);
        return (BUILDER_TYPE) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public BUILDER_TYPE provider(Class<? extends Provider> providerClass){
        return provider(providerClass.getName());
    }
}

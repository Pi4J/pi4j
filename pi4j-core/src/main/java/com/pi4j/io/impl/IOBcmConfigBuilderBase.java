package com.pi4j.io.impl;

import com.pi4j.config.BcmConfigBuilder;
import com.pi4j.config.Config;
import com.pi4j.config.ConfigBuilder;
import com.pi4j.config.impl.BcmConfigBuilderBase;
import com.pi4j.io.IOConfig;
import com.pi4j.io.IOConfigBuilder;
import com.pi4j.provider.Provider;

/**
 * <p>Abstract AddressConfigBuilderBase class.</p>
 *
 * @param <BUILDER_TYPE>
 * @param <CONFIG_TYPE>
 */
public abstract class IOBcmConfigBuilderBase<BUILDER_TYPE extends ConfigBuilder, CONFIG_TYPE extends Config>
    extends BcmConfigBuilderBase<BUILDER_TYPE, CONFIG_TYPE>
    implements IOConfigBuilder<BUILDER_TYPE, CONFIG_TYPE>,
    BcmConfigBuilder<BUILDER_TYPE, CONFIG_TYPE> {

    /**
     * PRIVATE CONSTRUCTOR
     */
    protected IOBcmConfigBuilderBase() {
    }

    @Override
    public BUILDER_TYPE provider(String provider) {
        this.properties.put(IOConfig.PROVIDER_KEY, provider);
        return (BUILDER_TYPE) this;
    }

    @Override
    public BUILDER_TYPE provider(Class<? extends Provider> providerClass) {
        this.properties.put(IOConfig.PROVIDER_KEY, providerClass.getName());
        return (BUILDER_TYPE) this;
    }

}

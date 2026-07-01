package com.pi4j.config.impl;

import com.pi4j.config.BcmConfig;
import com.pi4j.config.BcmConfigBuilder;
import com.pi4j.config.Config;
import com.pi4j.config.ConfigBuilder;

/**
 * <p>Abstract AddressConfigBuilderBase class.</p>
 *
 * @param <BUILDER_TYPE>
 * @param <CONFIG_TYPE>
 */
public abstract class BcmConfigBuilderBase<BUILDER_TYPE extends ConfigBuilder, CONFIG_TYPE extends Config>
    extends ConfigBuilderBase<BUILDER_TYPE, CONFIG_TYPE>
    implements BcmConfigBuilder<BUILDER_TYPE, CONFIG_TYPE> {

    /**
     * PRIVATE CONSTRUCTOR
     */
    protected BcmConfigBuilderBase() {
    }

    @Override
    public BUILDER_TYPE bcm(Integer bcm) {
        this.properties.put(BcmConfig.BCM_KEY, bcm.toString());
        return (BUILDER_TYPE) this;
    }
}

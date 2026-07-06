package com.pi4j.config.impl;

import com.pi4j.config.Config;
import com.pi4j.config.ConfigBuilder;
import com.pi4j.config.PortConfig;
import com.pi4j.config.PortConfigBuilder;
import com.pi4j.context.Context;

/**
 * <p>Abstract AddressConfigBuilderBase class.</p>
 *
 * @param <BUILDER_TYPE>
 * @param <CONFIG_TYPE>
 */
public abstract class PortConfigBuilderBase<BUILDER_TYPE extends ConfigBuilder, CONFIG_TYPE extends Config>
    extends ConfigBuilderBase<BUILDER_TYPE, CONFIG_TYPE>
    implements PortConfigBuilder<BUILDER_TYPE, CONFIG_TYPE> {

    /**
     * PRIVATE CONSTRUCTOR
     */
    protected PortConfigBuilderBase(Context context) {
        super();
    }

    @Override
    public BUILDER_TYPE port(String port) {
        this.properties.put(PortConfig.PORT_KEY, port);
        return (BUILDER_TYPE) this;
    }
}

package com.pi4j.config.impl;

import com.pi4j.config.Config;
import com.pi4j.config.ConfigBuilder;
import com.pi4j.config.DeviceConfig;

/**
 * <p>Abstract DeviceConfigBuilderBase class.</p>
 *
 * @param <BUILDER_TYPE>
 * @param <CONFIG_TYPE>
 */
public abstract class DeviceConfigBuilderBase<
    BUILDER_TYPE extends ConfigBuilder<BUILDER_TYPE, CONFIG_TYPE>,
    CONFIG_TYPE extends Config
    >
    extends ConfigBuilderBase<BUILDER_TYPE, CONFIG_TYPE>
    implements ConfigBuilder<BUILDER_TYPE, CONFIG_TYPE> {

    /**
     * PRIVATE CONSTRUCTOR
     */
    protected DeviceConfigBuilderBase() {
    }

    @SuppressWarnings("unchecked")
    public BUILDER_TYPE device(Integer device) {
        this.properties.put(DeviceConfig.DEVICE_KEY, device.toString());
        return (BUILDER_TYPE) this;
    }
}

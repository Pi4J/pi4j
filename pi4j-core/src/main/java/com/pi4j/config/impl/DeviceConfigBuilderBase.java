package com.pi4j.config.impl;

import com.pi4j.config.Config;
import com.pi4j.config.ConfigBuilder;
import com.pi4j.config.DeviceConfig;
import com.pi4j.config.DeviceConfigBuilder;

/**
 * <p>Abstract DeviceConfigBuilderBase class.</p>
 *
 * @param <BUILDER_TYPE>
 * @param <CONFIG_TYPE>
 */
public abstract class DeviceConfigBuilderBase<BUILDER_TYPE extends ConfigBuilder, CONFIG_TYPE extends Config>
    extends ConfigBuilderBase<BUILDER_TYPE, CONFIG_TYPE>
    implements DeviceConfigBuilder<BUILDER_TYPE, CONFIG_TYPE> {

    /**
     * PRIVATE CONSTRUCTOR
     */
    protected DeviceConfigBuilderBase() {
    }

    @Override
    public BUILDER_TYPE device(Integer device) {
        this.properties.put(DeviceConfig.DEVICE_KEY, device.toString());
        return (BUILDER_TYPE) this;
    }
}

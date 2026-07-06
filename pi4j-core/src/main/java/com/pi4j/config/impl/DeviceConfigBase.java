package com.pi4j.config.impl;

import com.pi4j.config.Config;
import com.pi4j.config.ConfigBase;
import com.pi4j.config.DeviceConfig;
import com.pi4j.config.exception.ConfigMissingRequiredKeyException;

import java.util.Map;

/**
 * <p>Abstract DeviceConfigBase class.</p>
 *
 * @param <CONFIG_TYPE>
 */
public abstract class DeviceConfigBase<CONFIG_TYPE extends Config<CONFIG_TYPE>>
    extends ConfigBase<CONFIG_TYPE>
    implements DeviceConfig<CONFIG_TYPE> {

    // private configuration variables
    protected Integer device = null;

    /**
     * PRIVATE CONSTRUCTOR
     */
    protected DeviceConfigBase() {
    }

    /**
     * PRIVATE CONSTRUCTOR
     */
    protected DeviceConfigBase(Map<String, String> properties) {
        super(properties);

        // load address property
        if (properties.containsKey(DEVICE_KEY)) {
            this.device = Integer.parseInt(properties.get(DEVICE_KEY));
        } else {
            throw new ConfigMissingRequiredKeyException(DEVICE_KEY);
        }
    }

    public Integer device() {
        return this.device;
    }
}

package com.pi4j.io.impl;

import com.pi4j.config.Config;
import com.pi4j.config.DeviceConfig;
import com.pi4j.config.impl.DeviceConfigBase;
import com.pi4j.io.IOConfig;

import java.util.Map;

/**
 * @param <CONFIG_TYPE>
 */
public class IODeviceConfigBase<CONFIG_TYPE extends Config<CONFIG_TYPE>>
    extends DeviceConfigBase<CONFIG_TYPE>
    implements IOConfig<CONFIG_TYPE>, DeviceConfig<CONFIG_TYPE> {

    // private configuration variables
    protected String provider = null;
    protected String platform = null;

    /**
     * PRIVATE CONSTRUCTOR
     */
    protected IODeviceConfigBase() {
    }

    /**
     * PRIVATE CONSTRUCTOR
     */
    protected IODeviceConfigBase(Map<String, String> properties) {
        super(properties);

        // load provider property
        if (properties.containsKey(PROVIDER_KEY)) {
            this.provider = properties.get(PROVIDER_KEY);
        }
        // load platform property
        if (properties.containsKey(PLATFORM_KEY)) {
            this.platform = properties.get(PLATFORM_KEY);
        }
    }

    @Override
    public String platform() {
        return this.platform;
    }

    @Override
    public String provider() {
        return this.provider;
    }
}

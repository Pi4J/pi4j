package com.pi4j.io.impl;

import com.pi4j.config.BcmConfig;
import com.pi4j.config.Config;
import com.pi4j.config.impl.BcmConfigBase;
import com.pi4j.io.IOConfig;

import java.util.Map;

/**
 * @param <CONFIG_TYPE>
 */
public class IOBcmConfigBase<CONFIG_TYPE extends Config>
    extends BcmConfigBase<CONFIG_TYPE>
    implements IOConfig<CONFIG_TYPE>, BcmConfig<CONFIG_TYPE> {

    // private configuration variables
    protected String provider = null;
    protected String platform = null;

    /**
     * PRIVATE CONSTRUCTOR
     */
    protected IOBcmConfigBase() {
    }

    /**
     * PRIVATE CONSTRUCTOR
     */
    protected IOBcmConfigBase(Map<String, String> properties) {
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

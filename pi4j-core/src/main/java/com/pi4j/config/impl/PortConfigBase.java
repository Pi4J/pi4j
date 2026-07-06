package com.pi4j.config.impl;

import com.pi4j.config.Config;
import com.pi4j.config.ConfigBase;
import com.pi4j.config.PortConfig;
import com.pi4j.config.exception.ConfigMissingRequiredKeyException;

import java.util.Map;

/**
 * <p>Abstract AddressConfigBase class.</p>
 *
 * @param <CONFIG_TYPE>
 */
public abstract class PortConfigBase<CONFIG_TYPE extends Config>
    extends ConfigBase<CONFIG_TYPE>
    implements PortConfig<CONFIG_TYPE> {

    // private configuration properties
    protected String port = null;

    /**
     * PRIVATE CONSTRUCTOR
     */
    protected PortConfigBase() {
        super();
    }

    protected PortConfigBase(String port) {
        super();
        this.port = port;
    }

    /**
     * PRIVATE CONSTRUCTOR
     */
    protected PortConfigBase(Map<String, String> properties) {
        super(properties);

        // load address property
        if (properties.containsKey(PORT_KEY)) {
            this.port = properties.get(PORT_KEY);
        } else {
            throw new ConfigMissingRequiredKeyException(PORT_KEY);
        }
    }

    public String port() {
        return this.port;
    }
}

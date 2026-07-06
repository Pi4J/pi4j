package com.pi4j.config.impl;

import com.pi4j.config.BcmConfig;
import com.pi4j.config.Config;
import com.pi4j.config.ConfigBase;
import com.pi4j.config.exception.ConfigMissingRequiredKeyException;

import java.util.Map;

/**
 * <p>Abstract AddressConfigBase class.</p>
 *
 * @param <CONFIG_TYPE>
 */
public abstract class BcmConfigBase<CONFIG_TYPE extends Config>
    extends ConfigBase<CONFIG_TYPE>
    implements BcmConfig<CONFIG_TYPE> {

    // private configuration properties
    protected Integer bcm = null;

    /**
     * PRIVATE CONSTRUCTOR
     */
    protected BcmConfigBase() {
        super();
    }

    protected BcmConfigBase(Integer bcm) {
        super();
        this.bcm = bcm;
    }

    /**
     * PRIVATE CONSTRUCTOR
     */
    protected BcmConfigBase(Map<String, String> properties) {
        super(properties);

        // load address property
        if (properties.containsKey(BCM_KEY)) {
            this.bcm = Integer.parseInt(properties.get(BCM_KEY));
        } else {
            throw new ConfigMissingRequiredKeyException(BCM_KEY);
        }
    }

    public Integer address() {
        return this.bcm;
    }

    public Integer bcm() {
        return this.bcm;
    }
}

package com.pi4j.config;

import com.pi4j.config.exception.ConfigMissingRequiredKeyException;
import com.pi4j.util.StringUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Base implementation of {@link Config} that stores the common identity properties shared by all
 * I/O configurations and exposes their raw property map. Concrete I/O configuration classes extend
 * this base to inherit handling of {@code id}, {@code name} and {@code description}, and override
 * {@link #getUniqueIdentifier()} to supply an I/O-type-specific identity.
 *
 * @param <CONFIG_TYPE> the concrete configuration sub-type
 */
public class ConfigBase<CONFIG_TYPE extends Config> implements Config<CONFIG_TYPE> {

    // private configuration variables
    protected String id = null;
    protected String name = null;
    protected String description = null;
    protected Map<String, String> properties = new HashMap<>();

    /**
     * Creates an empty configuration with no properties set.
     */
    protected ConfigBase() {
    }

    /**
     * Creates a configuration seeded from the given property map, copying all entries and reading the
     * common {@code id}, {@code name} and {@code description} values from their respective keys.
     *
     * @param properties the raw configuration properties to populate this instance from
     */
    protected ConfigBase(Map<String, String> properties) {
        // add all properties to this config object
        this.properties.putAll(properties);

        // load required 'id' property
        if (properties.containsKey(ID_KEY))
            this.id = properties.get(ID_KEY);

        // load optional 'name' property
        if (properties.containsKey(NAME_KEY))
            this.name = properties.get(NAME_KEY);

        // load optional 'description' property
        if (properties.containsKey(DESCRIPTION_KEY))
            this.description = properties.get(DESCRIPTION_KEY);
    }

    @Override
    public Map<String, String> properties() {
        return Collections.unmodifiableMap(this.properties);
    }

    @Override
    public String id() {
        return this.id;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public String description() {
        return this.description;
    }

    @Override
    public void validate() {
        if (StringUtil.isNullOrEmpty(this.id)) {
            throw new ConfigMissingRequiredKeyException(ID_KEY);
        }
    }

    @Override
    public int getUniqueIdentifier() {
        return 0;
    }
}

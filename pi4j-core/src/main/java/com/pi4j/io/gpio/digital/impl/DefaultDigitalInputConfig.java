package com.pi4j.io.gpio.digital.impl;

import com.pi4j.config.BcmConfig;
import com.pi4j.config.BusConfig;
import com.pi4j.config.ConfigBase;
import com.pi4j.config.exception.ConfigMissingRequiredKeyException;
import com.pi4j.context.Context;
import com.pi4j.io.IOConfig;
import com.pi4j.io.gpio.digital.*;
import com.pi4j.util.StringUtil;

import java.util.Map;

public class DefaultDigitalInputConfig extends ConfigBase implements DigitalInputConfig, IOConfig, BcmConfig {

    // private configuration variables
    protected String provider = null;
    // private configuration properties
    protected Integer bcm = null;

    /**
     * PRIVATE CONSTRUCTOR
     */
    private DefaultDigitalInputConfig() {
        super();
    }

    // private configuration properties
    protected Integer bus = 0;
    protected PullResistance pullResistance = PullResistance.OFF;
    protected Long debounce = DigitalInput.DEFAULT_DEBOUNCE;
    protected DigitalState onState = DigitalState.HIGH;

    /**
     * PRIVATE CONSTRUCTOR
     */
    protected DefaultDigitalInputConfig(Map<String, String> properties) {
        super(properties);

        // load address property
        if (properties.containsKey(BcmConfig.BCM_KEY)) {
            this.bcm = Integer.parseInt(properties.get(BcmConfig.BCM_KEY));
        } else {
            throw new ConfigMissingRequiredKeyException(BcmConfig.BCM_KEY);
        }

        // load provider property
        if (properties.containsKey(PROVIDER_KEY)) {
            this.provider = properties.get(PROVIDER_KEY);
        }

        // define default property values if any are missing (based on the required address value)
        this.id = StringUtil.setIfNullOrEmpty(this.id, "DIN-" + this.bcm, true);
        this.name = StringUtil.setIfNullOrEmpty(this.name, "DIN-" + this.bcm, true);
        this.description = StringUtil.setIfNullOrEmpty(this.description, "DIN-" + this.bcm, true);

        if (properties.containsKey(BusConfig.BUS_KEY)) {
            this.bus = Integer.parseInt(properties.get(BusConfig.BUS_KEY));
        }

        // load optional pull resistance from properties
        if (properties.containsKey(DigitalInputConfig.PULL_RESISTANCE_KEY)) {
            this.pullResistance = PullResistance.parse(properties.get(DigitalInputConfig.PULL_RESISTANCE_KEY));
        }

        // load optional pull resistance from properties
        if (properties.containsKey(DigitalInputConfig.DEBOUNCE_RESISTANCE_KEY)) {
            this.debounce = Long.parseLong(properties.get(DigitalInputConfig.DEBOUNCE_RESISTANCE_KEY));
        }

        // load on-state value property
        if (properties.containsKey(ON_STATE_KEY)) {
            this.onState = DigitalState.parse(properties.get(ON_STATE_KEY));
        }
    }

    /**
     * Creates a new {@link DigitalInputConfigBuilder}.
     *
     * @param context the Pi4J context
     * @return a new configuration builder instance
     * @deprecated As of version 5, please use DigitalInput.newConfigBuilder() instead.
     */
    @Deprecated(since="5.0")
    public static DigitalInputConfigBuilder newBuilder(Context context)  {
        return DigitalInputConfigBuilder.newInstance();
    }

    /**
     * {@inheritDoc}
     *
     * @deprecated use {@link #bus()} instead.
     */
    @Override
    @Deprecated(forRemoval = true)
    public Integer address() {
        return this.bcm;
    }

    @Override
    public Integer bus() {
        return this.bus;
    }

    @Override
    public Integer bcm() {
        return this.bcm;
    }

    @Override
    public int getUniqueIdentifier() {
        return bcm();
    }

    public PullResistance pull() {
        return this.pullResistance;
    }

    public Long debounce() {
        return this.debounce;
    }

    @Override
    public DigitalState onState() {
        return this.onState;
    }

    /**
     * Bean-style accessor equivalent to {@link #pull()}.
     *
     * @return the configured {@link PullResistance}
     */
    public PullResistance getPull(){
        return pull();
    }

    /**
     * Bean-style accessor equivalent to {@link #debounce()}.
     *
     * @return the configured debounce interval in microseconds, or {@code null} if unset
     */
    public Long getDebounce(){ return debounce(); }

    @Override
    public String provider() {
        return this.provider;
    }
}

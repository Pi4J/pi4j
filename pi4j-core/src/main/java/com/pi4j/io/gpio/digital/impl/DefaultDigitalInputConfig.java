package com.pi4j.io.gpio.digital.impl;

import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalInputConfig;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.gpio.digital.PullResistance;
import com.pi4j.io.impl.IOBcmConfigBase;
import com.pi4j.util.StringUtil;

import java.util.Map;

public class DefaultDigitalInputConfig
    extends IOBcmConfigBase<DigitalInputConfig>
    implements DigitalInputConfig {

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

        // define default property values if any are missing (based on the required address value)
        this.id = StringUtil.setIfNullOrEmpty(this.id, "DIN-" + this.bcm, true);
        this.name = StringUtil.setIfNullOrEmpty(this.name, "DIN-" + this.bcm, true);
        this.description = StringUtil.setIfNullOrEmpty(this.description, "DIN-" + this.bcm, true);

        if (properties.containsKey(BUS_KEY)) {
            this.bus = Integer.parseInt(properties.get(BUS_KEY));
        }

        // load optional pull resistance from properties
        if (properties.containsKey(PULL_RESISTANCE_KEY)) {
            this.pullResistance = PullResistance.parse(properties.get(PULL_RESISTANCE_KEY));
        }

        // load optional pull resistance from properties
        if (properties.containsKey(DEBOUNCE_RESISTANCE_KEY)) {
            this.debounce = Long.parseLong(properties.get(DEBOUNCE_RESISTANCE_KEY));
        }

        // load on-state value property
        if (properties.containsKey(ON_STATE_KEY)) {
            this.onState = DigitalState.parse(properties.get(ON_STATE_KEY));
        }
    }

    /**
     * @deprecated use {@link #bus()} instead.
     * <p>
     * {@inheritDoc}
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

    @Override
    public PullResistance pull() {
        return this.pullResistance;
    }

    @Override
    public Long debounce() {
        return this.debounce;
    }

    @Override
    public DigitalState onState() {
        return this.onState;
    }
}

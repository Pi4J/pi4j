package com.pi4j.io.gpio.digital.impl;

import com.pi4j.io.gpio.digital.DigitalOutputConfig;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.impl.IOBcmConfigBase;
import com.pi4j.util.StringUtil;

import java.util.Map;

public class DefaultDigitalOutputConfig
    extends IOBcmConfigBase<DigitalOutputConfig>
    implements DigitalOutputConfig {

    // private configuration properties
    protected Integer bus = null;
    protected DigitalState shutdownState = null;
    protected DigitalState initialState = null;
    protected DigitalState onState = DigitalState.HIGH;

    /**
     * PRIVATE CONSTRUCTOR
     */
    private DefaultDigitalOutputConfig() {
        super();
    }

    /**
     * PRIVATE CONSTRUCTOR
     */
    protected DefaultDigitalOutputConfig(Map<String, String> properties) {
        super(properties);

        // define default property values if any are missing (based on the required address value)
        this.id = StringUtil.setIfNullOrEmpty(this.id, "DOUT-" + this.bcm, true);
        this.name = StringUtil.setIfNullOrEmpty(this.name, "DOUT-" + this.bcm, true);
        this.description = StringUtil.setIfNullOrEmpty(this.description, "DOUT-" + this.bcm, true);

        if (properties.containsKey(BUS_KEY)) {
            this.bus = Integer.parseInt(properties.get(BUS_KEY));
        } else {
            // this is essential for FFM Plugin if using pi4j autoContext
            this.bus = 0;
        }

        // load initial value property
        if (properties.containsKey(INITIAL_STATE_KEY)) {
            this.initialState = DigitalState.parse(properties.get(INITIAL_STATE_KEY));
        }

        // load shutdown value property
        if (properties.containsKey(SHUTDOWN_STATE_KEY)) {
            this.shutdownState = DigitalState.parse(properties.get(SHUTDOWN_STATE_KEY));
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
    public DigitalState shutdownState() {
        return this.shutdownState;
    }

    @Override
    public DefaultDigitalOutputConfig shutdownState(DigitalState state) {
        this.shutdownState = state;
        return this;
    }

    @Override
    public DigitalState initialState() {
        return this.initialState;
    }

    @Override
    public DigitalState onState() {
        return this.onState;
    }
}

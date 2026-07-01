package com.pi4j.io.gpio.digital.impl;

import com.pi4j.io.gpio.digital.DigitalConfig;
import com.pi4j.io.gpio.digital.DigitalConfigBuilder;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.impl.IOBcmConfigBuilderBase;

/**
 * <p>Abstract DigitalConfigBuilderBase class.</p>
 *
 * @param <BUILDER_TYPE>
 * @param <CONFIG_TYPE>
 */
public abstract class DigitalConfigBuilderBase<BUILDER_TYPE extends DigitalConfigBuilder, CONFIG_TYPE extends DigitalConfig>
    extends IOBcmConfigBuilderBase<BUILDER_TYPE, CONFIG_TYPE>
    implements DigitalConfigBuilder<BUILDER_TYPE, CONFIG_TYPE> {

    /**
     * PRIVATE CONSTRUCTOR
     */
    protected DigitalConfigBuilderBase() {
    }

    @Override
    public BUILDER_TYPE onState(DigitalState state) {
        this.properties.put(DigitalConfig.ON_STATE_KEY, state.toString());
        return (BUILDER_TYPE) this;
    }
}

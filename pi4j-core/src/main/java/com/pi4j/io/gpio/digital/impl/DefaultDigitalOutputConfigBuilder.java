package com.pi4j.io.gpio.digital.impl;

import com.pi4j.context.Context;
import com.pi4j.io.gpio.GpioConfig;
import com.pi4j.io.gpio.digital.DigitalOutputConfig;
import com.pi4j.io.gpio.digital.DigitalOutputConfigBuilder;
import com.pi4j.io.gpio.digital.DigitalState;

public class DefaultDigitalOutputConfigBuilder
    extends DigitalConfigBuilderBase<DigitalOutputConfigBuilder, DigitalOutputConfig>
    implements DigitalOutputConfigBuilder {

    /**
     * PRIVATE CONSTRUCTOR
     */
    protected DefaultDigitalOutputConfigBuilder() {
    }

    /**
     * @param context
     */
    @Deprecated
    public static DigitalOutputConfigBuilder newInstance(Context context) {
        return newInstance();
    }

    public static DigitalOutputConfigBuilder newInstance() {
        return new DefaultDigitalOutputConfigBuilder();
    }


    @Override
    public DigitalOutputConfigBuilder address(Integer address) {
        this.properties.put(GpioConfig.BCM_KEY, String.valueOf(address));
        return this;
    }

    @Override
    public DigitalOutputConfigBuilder bus(int bus) {
        this.properties.put(GpioConfig.BUS_KEY, String.valueOf(bus));
        return this;
    }

    @Override
    public DigitalOutputConfigBuilder shutdown(DigitalState state) {
        this.properties.put(DigitalOutputConfig.SHUTDOWN_STATE_KEY, state.toString());
        return this;
    }

    @Override
    public DigitalOutputConfigBuilder initial(DigitalState state) {
        this.properties.put(DigitalOutputConfig.INITIAL_STATE_KEY, state.toString());
        return this;
    }

    @Override
    public DigitalOutputConfig build() {
        DigitalOutputConfig config = new DefaultDigitalOutputConfig(getResolvedProperties());
        return config;
    }
}

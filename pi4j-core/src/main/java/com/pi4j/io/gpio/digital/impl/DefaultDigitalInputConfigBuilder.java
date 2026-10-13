package com.pi4j.io.gpio.digital.impl;

import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalConfig;
import com.pi4j.io.gpio.digital.DigitalInputConfigBuilder;
import com.pi4j.io.gpio.digital.PullResistance;

import java.util.concurrent.TimeUnit;

public class DefaultDigitalInputConfigBuilder
    extends DigitalConfigBuilderBase<DigitalInputConfigBuilder, DefaultDigitalInputConfig>
    implements DigitalInputConfigBuilder {

    /**
     * PRIVATE CONSTRUCTOR
     */
    protected DefaultDigitalInputConfigBuilder() {

    }

    /**
     * @param context
     */
    public static DigitalInputConfigBuilder newInstance(Context context) {
        return newInstance();
    }

    public static DigitalInputConfigBuilder newInstance() {
        return new DefaultDigitalInputConfigBuilder();
    }

    @Override
    public DefaultDigitalInputConfig build() {
        DefaultDigitalInputConfig config = new DefaultDigitalInputConfig(getResolvedProperties());
        return config;
    }

    /**
     * @deprecated use {@link #bus(int)} instead.
     * <p>
     * {@inheritDoc}
     */
    @Override
    @Deprecated(forRemoval = true)
    public DigitalInputConfigBuilder address(Integer address) {
        this.properties.put(DigitalConfig.BCM_KEY, String.valueOf(address));
        return this;
    }

    @Override
    public DigitalInputConfigBuilder bcm(Integer bcm) {
        this.properties.put(DigitalConfig.BCM_KEY, String.valueOf(bcm));
        return this;
    }

    @Override
    public DigitalInputConfigBuilder bus(int bus) {
        this.properties.put(DigitalConfig.BUS_KEY, String.valueOf(bus));
        return this;
    }

    @Override
    public DigitalInputConfigBuilder pull(PullResistance value) {
        this.properties.put(DefaultDigitalInputConfig.PULL_RESISTANCE_KEY, value.toString());
        return this;
    }

    @Override
    public DigitalInputConfigBuilder debounce(Long microseconds) {
        if (microseconds != null) {
            this.properties.put(DefaultDigitalInputConfig.DEBOUNCE_RESISTANCE_KEY, microseconds.toString());
        }
        return this;
    }

    @Override
    public DigitalInputConfigBuilder debounce(Long interval, TimeUnit units) {
        return debounce(units.toMicros(interval));
    }
}

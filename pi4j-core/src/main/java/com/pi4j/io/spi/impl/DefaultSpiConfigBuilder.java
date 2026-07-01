package com.pi4j.io.spi.impl;

import com.pi4j.context.Context;
import com.pi4j.io.impl.IOBcmConfigBuilderBase;
import com.pi4j.io.spi.*;

public class DefaultSpiConfigBuilder
    extends IOBcmConfigBuilderBase<SpiConfigBuilder, SpiConfig>
    implements SpiConfigBuilder {

    /**
     * PRIVATE CONSTRUCTOR
     */
    protected DefaultSpiConfigBuilder() {
    }

    /**
     * @param context
     */
    @Deprecated
    public static SpiConfigBuilder newInstance(Context context) {
        return newInstance();
    }

    public static SpiConfigBuilder newInstance() {
        return new DefaultSpiConfigBuilder();
    }


    @Override
    public SpiConfigBuilder readLsbFirst(Integer shift) {
        this.properties.put(SpiConfig.READ_LSB_KEY, shift.toString());
        return this;
    }

    @Override
    public SpiConfigBuilder writeLsbFirst(Integer shift) {
        this.properties.put(SpiConfig.WRITE_LSB_KEY, shift.toString());
        return this;
    }

    /**
     * @deprecated use {@link #bus(Integer)} instead.
     * <p>
     * {@inheritDoc}
     */
    @Override
    @Deprecated(forRemoval = true)
    public SpiConfigBuilder address(Integer address) {
        this.properties.put(SpiConfig.ADDRESS_KEY, address.toString());
        return this;
    }

    @Override
    public SpiConfigBuilder bus(SpiBus bus) {
        this.properties.put(SpiConfig.BUS_KEY, Integer.toString(bus.getBus()));
        return this;
    }

    @Override
    public SpiConfigBuilder baud(Integer rate) {
        this.properties.put(SpiConfig.BAUD_KEY, rate.toString());
        return this;
    }

    @Override
    public SpiConfigBuilder mode(SpiMode mode) {
        this.properties.put(SpiConfig.MODE_KEY, Integer.toString(mode.getMode()));
        return this;
    }

    @Override
    public SpiConfigBuilder flags(Long flags) {
        this.properties.put(SpiConfig.FLAGS_KEY, flags.toString());
        return this;
    }

    @Override
    public SpiConfigBuilder channel(Integer channel) {
        this.properties.put(SpiConfig.CHANNEL_KEY, channel.toString());
        return this;
    }

    @Override
    public SpiConfigBuilder chipSelect(SpiChipSelect chipSelect) {
        this.properties.put(SpiConfig.CHANNEL_KEY, Integer.toString(chipSelect.getChipSelect()));
        return this;
    }

    @Override
    public SpiConfig build() {
        SpiConfig config = new DefaultSpiConfig(this.properties);
        return config;
    }
}

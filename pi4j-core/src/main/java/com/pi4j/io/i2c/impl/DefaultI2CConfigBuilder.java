package com.pi4j.io.i2c.impl;

import com.pi4j.context.Context;
import com.pi4j.io.i2c.*;
import com.pi4j.io.impl.IOConfigBuilderBase;

public class DefaultI2CConfigBuilder
        extends IOConfigBuilderBase<I2CConfigBuilder, I2CConfig>
        implements I2CConfigBuilder {

    /**
     * PRIVATE CONSTRUCTOR
     */
    protected DefaultI2CConfigBuilder(){
    }

    /**
     * @param context
     */
    @Deprecated
    public static I2CConfigBuilder newInstance(Context context) {
        return newInstance();
    }


    public static I2CConfigBuilder newInstance() {
        return new DefaultI2CConfigBuilder();
    }

    @Override
    public I2CConfig build() {
        I2CConfig config = new DefaultI2CConfig(getResolvedProperties());
        return config;
    }

    @Override
    public I2CConfigBuilder bus(Integer bus){
        this.properties.put(I2CConfig.BUS_KEY, bus.toString());
        return this;
    }

    @Override
    public I2CConfigBuilder device(Integer device){
        this.properties.put(I2CConfig.DEVICE_KEY, device.toString());
        return this;
    }

    @Override
    public I2CConfigBuilder i2cImplementation(I2CImplementation i2CImplementation) {
        this.properties.put(I2CConfig.I2C_IMPLEMENTATION, i2CImplementation.name());
        return this;
    }
}

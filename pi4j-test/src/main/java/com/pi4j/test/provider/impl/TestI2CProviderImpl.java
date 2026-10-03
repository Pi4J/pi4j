package com.pi4j.test.provider.impl;

import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.i2c.I2CProvider;
import com.pi4j.provider.ProviderBase;
import com.pi4j.test.provider.TestI2CProvider;

public class TestI2CProviderImpl
    extends ProviderBase<I2CProvider, I2C, I2CConfig>
    implements TestI2CProvider {

    public TestI2CProviderImpl(){ super(); }

    public TestI2CProviderImpl(String id){
        super(id);
    }

    public TestI2CProviderImpl(String id, String name){
        super(id, name);
    }

    @Override
    public I2C create(I2CConfig config) {
        return null;
    }
}

package com.pi4j.test.provider.impl;

import com.pi4j.io.spi.Spi;
import com.pi4j.io.spi.SpiConfig;
import com.pi4j.io.spi.SpiProviderBase;
import com.pi4j.test.provider.TestSpiProvider;

public class TestSpiProviderImpl extends SpiProviderBase implements TestSpiProvider {

    public TestSpiProviderImpl(){ super(); }

    public TestSpiProviderImpl(String id){
        super(id);
    }

    public TestSpiProviderImpl(String id, String name){
        super(id, name);
    }

    @Override
    public Spi create(SpiConfig config) {
        return null;
    }
}

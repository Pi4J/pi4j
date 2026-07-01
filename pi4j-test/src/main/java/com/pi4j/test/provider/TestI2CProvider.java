package com.pi4j.test.provider;

import com.pi4j.io.i2c.I2CProvider;
import com.pi4j.test.provider.impl.TestI2CProviderImpl;

/**
 * <p>TestI2CProvider interface.</p>
 */
public interface TestI2CProvider extends I2CProvider {
    static TestI2CProvider newInstance(){
        return new TestI2CProviderImpl();
    }
    static TestI2CProvider newInstance(String id){
        return new TestI2CProviderImpl(id);
    }
    static TestI2CProvider newInstance(String id, String name){
        return new TestI2CProviderImpl(id, name);
    }
}

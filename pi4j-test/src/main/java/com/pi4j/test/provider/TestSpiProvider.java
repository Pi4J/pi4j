package com.pi4j.test.provider;

import com.pi4j.io.spi.SpiProvider;
import com.pi4j.test.provider.impl.TestSpiProviderImpl;

/**
 * <p>TestSpiProvider interface.</p>
 */
public interface TestSpiProvider extends SpiProvider {
    static TestSpiProvider newInstance(){
        return new TestSpiProviderImpl();
    }
    static TestSpiProvider newInstance(String id){
        return new TestSpiProviderImpl(id);
    }
    static TestSpiProvider newInstance(String id, String name){
        return new TestSpiProviderImpl(id, name);
    }
}

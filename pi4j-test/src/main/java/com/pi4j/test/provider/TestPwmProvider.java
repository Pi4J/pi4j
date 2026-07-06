package com.pi4j.test.provider;

import com.pi4j.io.pwm.PwmProvider;
import com.pi4j.test.provider.impl.TestPwmProviderImpl;

/**
 * <p>TestPwmProvider interface.</p>
 */
public interface TestPwmProvider extends PwmProvider {
    static TestPwmProvider newInstance(){
        return new TestPwmProviderImpl();
    }
    static TestPwmProvider newInstance(String id){
        return new TestPwmProviderImpl(id);
    }
    static TestPwmProvider newInstance(String id, String name){
        return new TestPwmProviderImpl(id, name);
    }
}

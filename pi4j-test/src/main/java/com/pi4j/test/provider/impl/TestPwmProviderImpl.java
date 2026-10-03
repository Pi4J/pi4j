package com.pi4j.test.provider.impl;

import com.pi4j.io.pwm.Pwm;
import com.pi4j.io.pwm.PwmConfig;
import com.pi4j.io.pwm.PwmProvider;
import com.pi4j.provider.ProviderBase;
import com.pi4j.test.provider.TestPwmProvider;

public class TestPwmProviderImpl
    extends ProviderBase<PwmProvider, Pwm, PwmConfig>
    implements TestPwmProvider {

    public TestPwmProviderImpl(){ super(); }

    public TestPwmProviderImpl(String id){
        super(id);
    }

    public TestPwmProviderImpl(String id, String name){
        super(id, name);
    }

    @Override
    public Pwm create(PwmConfig config) {
        return null;
    }
}

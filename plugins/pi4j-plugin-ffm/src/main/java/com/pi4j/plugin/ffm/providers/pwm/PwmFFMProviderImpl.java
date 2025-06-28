package com.pi4j.plugin.ffm.providers.pwm;

import com.pi4j.io.pwm.*;

public class PwmFFMProviderImpl extends PwmProviderBase implements PwmProvider {

    public PwmFFMProviderImpl() {
        this.id = "PwmFFMProviderImpl";
        this.name = "PwmFFMProviderImpl";
    }

    @Override
    public int getPriority() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pwm create(PwmConfig config) {
        // create new I/O instance based on I/O config
        var pwm = new PwmFFMHardware(this, config);
        this.context.registry().add(pwm);
        return pwm;
    }
}

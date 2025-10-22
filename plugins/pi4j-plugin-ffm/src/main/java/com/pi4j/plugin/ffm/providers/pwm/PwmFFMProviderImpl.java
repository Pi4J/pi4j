package com.pi4j.plugin.ffm.providers.pwm;

import com.pi4j.io.pwm.Pwm;
import com.pi4j.io.pwm.PwmConfig;
import com.pi4j.io.pwm.PwmProvider;
import com.pi4j.io.pwm.PwmProviderBase;
import com.pi4j.plugin.ffm.common.PermissionHelper;

public class PwmFFMProviderImpl extends PwmProviderBase implements PwmProvider {

    public PwmFFMProviderImpl() {
        this.id = "ffm-pwm";
        this.name = "FFM API Provider PWM";
        PermissionHelper.checkUserPermissions(this);
    }

    @Override
    public int getPriority() {
        return 200;
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

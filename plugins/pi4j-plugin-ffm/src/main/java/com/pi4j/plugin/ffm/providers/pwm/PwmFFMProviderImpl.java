package com.pi4j.plugin.ffm.providers.pwm;

import com.pi4j.io.exception.IOException;
import com.pi4j.io.pwm.*;
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
        // validate PWM type
        if (config.pwmType() != PwmType.HARDWARE) {
            throw new IOException("The FFM PWM provider only supports HARDWARE PWM");
        }

        // validate the config
        if (config.chip() == null || config.channel() == null) {
            throw new IllegalArgumentException("PWM Chip and Channel are needed for hardware PWM with the FFM I/O provider");
        }

        // Warn for unneeded config
        if (config.pwmType() == PwmType.HARDWARE && config.bcm() != null) {
            logger.warn("You specified a BCM value for the PWM, but this is not needed for hardware PWM. Please specify chip and channel instead.");
        }

        // create new I/O instance based on I/O config
        var pwm = new PwmFFMHardware(this, config);
        this.context.registry().add(pwm);
        return pwm;
    }
}

package com.pi4j.plugin.ffm.providers.pwm;

import com.pi4j.io.pwm.Pwm;
import com.pi4j.io.pwm.PwmConfig;
import com.pi4j.io.pwm.PwmProvider;
import com.pi4j.io.pwm.PwmProviderBase;

public class FFMPwmProviderImpl extends PwmProviderBase implements PwmProvider {

    public FFMPwmProviderImpl() {
        this.id = "FFMPwmProviderImpl";
        this.name = "FFMPwmProviderImpl";
    }

    @Override
    public int getPriority() {
        return 15000;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pwm create(PwmConfig config) {
        // create new I/O instance based on I/O config
        Pwm pwm = null;
//            if (config.pwmType() == PwmType.HARDWARE) {
//                pwm = new PiGpioPwmHardware(piGpio, this, config);
//            } else {
//                pwm = new PiGpioPwmSoftware(piGpio, this, config);
//            }
        this.context.registry().add(pwm);
        return pwm;
    }
}

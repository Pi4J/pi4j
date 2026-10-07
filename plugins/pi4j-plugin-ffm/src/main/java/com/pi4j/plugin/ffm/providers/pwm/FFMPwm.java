package com.pi4j.plugin.ffm.providers.pwm;

import com.pi4j.context.Context;
import com.pi4j.io.exception.IOException;
import com.pi4j.io.pwm.Pwm;
import com.pi4j.io.pwm.PwmConfig;
import com.pi4j.io.pwm.PwmType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FFMPwm {
    private final static Logger logger = LoggerFactory.getLogger(FFMPwm.class);

    /**
     * Creates a hardware PWM instance for the chip and channel given in the configuration, and verifies
     * that the corresponding {@code /sys/class/pwm/pwmchipN} path is accessible with the required
     * permissions.
     *
     * @param context  the context of this instance
     * @param config   the PWM configuration supplying the chip number, channel and optional initial
     *                 duty cycle, polarity and frequency
     */
    public static Pwm create(Context context, PwmConfig config) {
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
        return new FFMPwmHardware(context, config);
    }

}

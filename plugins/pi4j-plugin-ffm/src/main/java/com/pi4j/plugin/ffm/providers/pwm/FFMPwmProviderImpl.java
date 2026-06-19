package com.pi4j.plugin.ffm.providers.pwm;

import com.pi4j.io.exception.IOException;
import com.pi4j.io.pwm.*;
import com.pi4j.plugin.ffm.common.FFMPermissionHelper;

/**
 * {@link PwmProvider} for the FFM backend that creates hardware PWM instances driven through the Linux
 * sysfs PWM interface ({@code /sys/class/pwm}). Only {@link PwmType#HARDWARE} is supported; the produced
 * I/O instances are {@link FFMPwmHardware}.
 *
 * @see com.pi4j.io.pwm.PwmProvider
 * @see FFMPwmHardware
 */
public class FFMPwmProviderImpl extends PwmProviderBase implements PwmProvider {

    /**
     * Creates the provider, assigning its id and name and verifying that the current user holds the
     * permissions required to access the sysfs PWM interface.
     */
    public FFMPwmProviderImpl() {
        this.id = "ffm-pwm";
        this.name = "FFM API Provider PWM";
        FFMPermissionHelper.checkUserPermissions(this);
    }

    @Override
    public int getPriority() {
        return 200;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Validates that the configuration requests {@link PwmType#HARDWARE} and supplies both a PWM chip
     * and channel, then creates and registers a {@link FFMPwmHardware} instance. A configured BCM pin
     * is ignored (with a warning) because hardware PWM is addressed by chip and channel.
     *
     * @param config the PWM configuration; must specify {@link PwmType#HARDWARE}, a chip and a channel
     * @return the newly created and registered hardware PWM instance
     * @throws IOException              if the configuration requests a non-hardware PWM type
     * @throws IllegalArgumentException if the chip or channel is missing from the configuration
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
        var pwm = new FFMPwmHardware(this, config);
        this.context.register(pwm);
        return pwm;
    }
}

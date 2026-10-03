package com.pi4j.plugin.ffm.providers.gpio;

import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalOutputConfig;
import com.pi4j.io.gpio.digital.DigitalOutputProvider;
import com.pi4j.plugin.ffm.common.FFMPermissionHelper;
import com.pi4j.provider.ProviderBase;

/**
 * FFM backend {@link DigitalOutputProvider}. Creates {@link FFMDigitalOutput} instances that drive
 * GPIO lines through the Linux GPIO v2 character-device ioctl interface, and verifies that the current
 * user has the permissions required to access the GPIO devices.
 */
public class FFMDigitalOutputProviderImpl
    extends ProviderBase<DigitalOutputProvider, DigitalOutput, DigitalOutputConfig>
    implements DigitalOutputProvider {

    /**
     * Creates the provider, assigning its id and name and checking that the current user is permitted
     * to access the GPIO character devices used by this backend.
     */
    public FFMDigitalOutputProviderImpl() {
        this.id = "ffm-digital-output";
        this.name = "FFM API Provider Digital Output";
        FFMPermissionHelper.checkUserPermissions(this);
    }


    /**
     * {@inheritDoc}
     * <p>
     * Resolves the GPIO chip name from the {@code gpio.chip.name} context property (defaulting to
     * {@code "unknown"}), constructs an {@link FFMDigitalOutput} for the requested line.
     */
    @Override
    public DigitalOutput create(DigitalOutputConfig config) {
        return new FFMDigitalOutput(context,  this, config);
    }

    @Override
    public int getPriority() {
        return 200;
    }
}

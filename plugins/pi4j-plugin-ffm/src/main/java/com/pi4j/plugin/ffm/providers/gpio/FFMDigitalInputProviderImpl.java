package com.pi4j.plugin.ffm.providers.gpio;

import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalInputConfig;
import com.pi4j.io.gpio.digital.DigitalInputProvider;
import com.pi4j.io.gpio.digital.DigitalInputProviderBase;
import com.pi4j.plugin.ffm.common.FFMPermissionHelper;

/**
 * FFM backend {@link DigitalInputProvider}. Creates {@link FFMDigitalInput} instances that drive GPIO
 * lines through the Linux GPIO v2 character-device ioctl interface, and verifies that the current user
 * has the permissions required to access the GPIO devices.
 */
public class FFMDigitalInputProviderImpl extends DigitalInputProviderBase implements DigitalInputProvider {

    /**
     * Creates the provider, assigning its id and name and checking that the current user is permitted
     * to access the GPIO character devices used by this backend.
     */
    public FFMDigitalInputProviderImpl() {
        this.id = "ffm-digital-input";
        this.name = "FFM API Provider Digital Input";
        FFMPermissionHelper.checkUserPermissions(this);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Resolves the GPIO chip name from the {@code gpio.chip.name} context property (defaulting to
     * {@code "unknown"}), constructs an {@link FFMDigitalInput} for the requested line.
     */
    @Override
    public DigitalInput create(DigitalInputConfig config) {
        return new FFMDigitalInput(context, this, config);
    }

    @Override
    public int getPriority() {
        return 200;
    }
}

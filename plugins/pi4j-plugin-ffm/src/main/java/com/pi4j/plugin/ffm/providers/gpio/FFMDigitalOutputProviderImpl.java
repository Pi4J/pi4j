package com.pi4j.plugin.ffm.providers.gpio;

import com.pi4j.context.Context;
import com.pi4j.exception.InitializeException;
import com.pi4j.exception.ShutdownException;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalOutputConfig;
import com.pi4j.io.gpio.digital.DigitalOutputProvider;
import com.pi4j.io.gpio.digital.DigitalOutputProviderBase;
import com.pi4j.plugin.ffm.common.FFMPermissionHelper;

/**
 * FFM backend {@link DigitalOutputProvider}. Creates {@link FFMDigitalOutput} instances that drive
 * GPIO lines through the Linux GPIO v2 character-device ioctl interface, and verifies that the current
 * user has the permissions required to access the GPIO devices.
 */
public class FFMDigitalOutputProviderImpl extends DigitalOutputProviderBase implements DigitalOutputProvider {

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
     * {@code "unknown"}), constructs an {@link FFMDigitalOutput} for the requested line, and registers
     * it with the context.
     */
    @Override
    public DigitalOutput create(DigitalOutputConfig config) {
        var digitalOutput = new FFMDigitalOutput( this, config);
        this.context.register(digitalOutput);
        return digitalOutput;
    }

    @Override
    public int getPriority() {
        return 200;
    }

    @Override
    public DigitalOutputProvider initialize(Context context) throws InitializeException {
        return super.initialize(context);
    }

    @Override
    public DigitalOutputProvider shutdownInternal(Context context) throws ShutdownException {
        return super.shutdownInternal(context);
    }
}

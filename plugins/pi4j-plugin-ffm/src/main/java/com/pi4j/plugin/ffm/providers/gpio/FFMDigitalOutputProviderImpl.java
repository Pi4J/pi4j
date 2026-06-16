package com.pi4j.plugin.ffm.providers.gpio;

import com.pi4j.context.Context;
import com.pi4j.exception.InitializeException;
import com.pi4j.exception.ShutdownException;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalOutputConfig;
import com.pi4j.io.gpio.digital.DigitalOutputProvider;
import com.pi4j.io.gpio.digital.DigitalOutputProviderBase;
import com.pi4j.plugin.ffm.common.FFMPermissionHelper;

public class FFMDigitalOutputProviderImpl extends DigitalOutputProviderBase implements DigitalOutputProvider {

    public FFMDigitalOutputProviderImpl() {
        this.id = "ffm-digital-output";
        this.name = "FFM API Provider Digital Output";
        FFMPermissionHelper.checkUserPermissions(this);
    }


    @Override
    public DigitalOutput create(DigitalOutputConfig config) {
        var chipName = context.config().properties().getOrDefault("gpio.chip.name", "unknown");
        var digitalOutput = new FFMDigitalOutput(chipName, this, config);
        this.context.runtime().add(digitalOutput);
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

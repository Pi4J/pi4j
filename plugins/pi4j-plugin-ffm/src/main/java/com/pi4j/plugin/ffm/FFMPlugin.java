package com.pi4j.plugin.ffm;

import com.pi4j.context.Context;
import com.pi4j.exception.ShutdownException;
import com.pi4j.extension.Plugin;
import com.pi4j.extension.PluginService;
import com.pi4j.plugin.ffm.providers.gpio.DigitalInputFFMProviderImpl;
import com.pi4j.plugin.ffm.providers.gpio.DigitalOutputFFMProviderImpl;
import com.pi4j.plugin.ffm.providers.i2c.I2CFFMProviderImpl;
import com.pi4j.plugin.ffm.providers.pwm.PwmFFMProviderImpl;
import com.pi4j.plugin.ffm.providers.serial.SerialFFMProviderImpl;
import com.pi4j.plugin.ffm.providers.spi.SpiFFMProviderImpl;
import com.pi4j.provider.Provider;

import java.util.Arrays;

public class FFMPlugin implements Plugin {
    private Provider<?, ?, ?>[] providers = new Provider[]{};

    @Override
    public void initialize(PluginService service) {
        this.providers = new Provider[]{
            new DigitalInputFFMProviderImpl(),
            new DigitalOutputFFMProviderImpl(),
            new I2CFFMProviderImpl(),
            new SpiFFMProviderImpl(),
            new PwmFFMProviderImpl(),
            new SerialFFMProviderImpl()
        };
        service.register(providers);
    }

    @Override
    public void shutdown(Context context) throws ShutdownException {
        Arrays.stream(this.providers).forEach(provider -> provider.shutdown(context));
    }
}

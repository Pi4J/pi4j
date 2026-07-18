package com.pi4j.plugin.ffm;

import com.pi4j.context.Context;
import com.pi4j.exception.ShutdownException;
import com.pi4j.extension.Plugin;
import com.pi4j.extension.PluginService;
import com.pi4j.plugin.ffm.detect.HardwareDetector;
import com.pi4j.plugin.ffm.providers.gpio.FFMDigitalInputProviderImpl;
import com.pi4j.plugin.ffm.providers.gpio.FFMDigitalOutputProviderImpl;
import com.pi4j.plugin.ffm.providers.i2c.FFMI2CProviderImpl;
import com.pi4j.plugin.ffm.providers.pwm.FFMPwmProviderImpl;
import com.pi4j.plugin.ffm.providers.spi.FFMSpiProviderImpl;
import com.pi4j.provider.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * Pi4J {@link Plugin} entry point for the FFM (Foreign Function &amp; Memory) native I/O backend.
 * <p>
 * On initialization it instantiates and registers the FFM providers that talk to the Linux kernel
 * directly via the Java Foreign Function &amp; Memory API: {@link FFMDigitalInputProviderImpl} and
 * {@link FFMDigitalOutputProviderImpl} for GPIO, {@link FFMI2CProviderImpl} for I2C,
 * {@link FFMSpiProviderImpl} for SPI, and {@link FFMPwmProviderImpl} for PWM. These providers
 * supply the runtime implementations of the corresponding pi4j-core contracts.
 */
public class FFMPlugin implements Plugin {
    private static final Logger logger = LoggerFactory.getLogger(FFMPlugin.class);

    private Provider<?, ?, ?>[] providers = new Provider[]{};

    @Override
    public void initialize(PluginService service) {
        logDetectedHardware();
        this.providers = new Provider[]{
            new FFMDigitalInputProviderImpl(),
            new FFMDigitalOutputProviderImpl(),
            new FFMI2CProviderImpl(),
            new FFMSpiProviderImpl(),
            new FFMPwmProviderImpl()
        };
        service.register(providers);
    }

    /**
     * Runs hardware I/O detection and logs the report. Best-effort only: detection must never prevent the
     * plugin from registering its providers, so any failure is logged and swallowed.
     */
    private void logDetectedHardware() {
        try {
            var report = new HardwareDetector().detect();
            logger.debug("FFM plugin hardware detection:\n{}", report.renderSummery());
            logger.trace("GPIO details:\n{}", report.renderGpioDetails());
        } catch (Exception e) {
            logger.warn("FFM plugin hardware detection failed: {}", e.getMessage(), e);
        }
    }

    @Override
    public void shutdown(Context context) throws ShutdownException {
        Arrays.stream(this.providers).forEach(provider -> provider.shutdownInternal(context));
    }
}

package com.pi4j.plugin.ffm;

import com.pi4j.context.Context;
import com.pi4j.exception.Pi4JException;
import com.pi4j.exception.ShutdownException;
import com.pi4j.extension.Plugin;
import com.pi4j.extension.PluginService;
import com.pi4j.plugin.ffm.providers.gpio.FFMDigitalInputProviderImpl;
import com.pi4j.plugin.ffm.providers.gpio.FFMDigitalOutputProviderImpl;
import com.pi4j.plugin.ffm.providers.i2c.FFMI2CProviderImpl;
import com.pi4j.plugin.ffm.providers.parallel.FFMParallelPortProvider;
import com.pi4j.plugin.ffm.providers.pwm.FFMPwmProviderImpl;
import com.pi4j.plugin.ffm.providers.spi.FFMSpiProviderImpl;
import com.pi4j.provider.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * Pi4J {@link Plugin} entry point for the FFM (Foreign Function &amp; Memory) native I/O backend.
 * <p>
 * On initialization it instantiates and registers the FFM providers that talk to the Linux kernel
 * directly via the Java Foreign Function &amp; Memory API: {@link FFMDigitalInputProviderImpl} and
 * {@link FFMDigitalOutputProviderImpl} for GPIO, {@link FFMI2CProviderImpl} for I2C,
 * {@link FFMSpiProviderImpl} for SPI, and {@link FFMPwmProviderImpl} for PWM. These providers
 * supply the runtime implementations of the corresponding pi4j-core contracts.
 * <p>
 * Each provider checks in its constructor that the current user belongs to the group its devices
 * need ({@code gpio} or {@code dialout} for GPIO and PWM, {@code i2c} for I2C, {@code spi} for SPI)
 * and throws when not. Those checks are independent, so the providers are created one by one: a
 * user who is in {@code i2c} but not in {@code gpio} still gets {@code ffm-i2c}, and only the
 * providers whose check failed are missing. The plugin fails as a whole only when no provider
 * could be created.
 */
public class FFMPlugin implements Plugin {
    private static final Logger logger = LoggerFactory.getLogger(FFMPlugin.class);

    /** A provider's id and how to create it; creation runs the permission check and may throw. */
    private record Candidate(String id, Supplier<Provider<?, ?, ?>> factory) {
    }

    private static final List<Candidate> CANDIDATES = List.of(
        new Candidate("ffm-digital-input", FFMDigitalInputProviderImpl::new),
        new Candidate("ffm-digital-output", FFMDigitalOutputProviderImpl::new),
        new Candidate("ffm-i2c", FFMI2CProviderImpl::new),
        new Candidate("ffm-spi", FFMSpiProviderImpl::new),
        new Candidate("ffm-pwm", FFMPwmProviderImpl::new),
        new Candidate("ffm-parallel-port", FFMParallelPortProvider::new)
    );

    private Provider<?, ?, ?>[] providers = new Provider[]{};

    @Override
    public void initialize(PluginService service) {
        var available = new ArrayList<Provider<?, ?, ?>>();
        var failures = new ArrayList<String>();
        for (var candidate : CANDIDATES) {
            try {
                available.add(candidate.factory().get());
            } catch (RuntimeException e) {
                // The permission helper has already printed what to do; name the provider that is lost
                logger.error("FFM provider [{}] is not available: {}", candidate.id(), e.getMessage());
                failures.add(candidate.id() + ": " + e.getMessage());
            }
        }
        if (available.isEmpty()) {
            throw new Pi4JException("No FFM provider could be initialized: " + String.join("; ", failures));
        }
        this.providers = available.toArray(Provider[]::new);
        service.register(providers);
    }

    @Override
    public void shutdown(Context context) throws ShutdownException {
        Arrays.stream(this.providers).forEach(provider -> provider.shutdownInternal(context));
    }
}

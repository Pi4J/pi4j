package com.pi4j.io.gpio.digital;

import com.pi4j.context.Context;
import com.pi4j.io.IO;
import com.pi4j.io.IOType;

/**
 * Provider-agnostic {@link PinReconfigurer} implementation. Runtime reconfiguration of a GPIO pin is
 * expressed as two generic steps that work for any provider: shut the current I/O instance down
 * (which releases its GPIO line and unregisters it from the {@link Context} registry), then create a
 * replacement of the requested direction through the provider already registered for that
 * {@link IOType}.
 * <p>
 * The no-argument {@code create()} steps reuse the addressing (BCM pin and bus) of the pin being
 * reconfigured so callers do not have to restate it; direction-specific settings fall back to their
 * defaults.
 *
 * @see DigitalInput#reconfigure()
 * @see DigitalOutput#reconfigure()
 */
public class DefaultPinReconfigurer implements PinReconfigurer {

    private final IO currentPin;
    private final Context context;

    /**
     * Creates a reconfigurer for the given pin.
     *
     * @param currentPin the pin currently holding the GPIO line to be reconfigured
     * @param context    the Pi4J context used to shut the old pin down and resolve the target provider
     */
    public DefaultPinReconfigurer(IO currentPin, Context context) {
        this.currentPin = currentPin;
        this.context = context;
    }

    @Override
    public OutputReconfigurer digitalOutput() {
        return new OutputReconfigurer() {
            @Override
            public DigitalOutput create(DigitalOutputConfig config) {
                context.shutdown(currentPin);
                DigitalOutputProvider provider = context.provider(IOType.DIGITAL_OUTPUT);
                return provider.create(config);
            }

            @Override
            public DigitalOutput create() {
                return create(reuse(DigitalOutputConfigBuilder.newInstance()).build());
            }
        };
    }

    @Override
    public InputReconfigurer digitalInput() {
        return new InputReconfigurer() {
            @Override
            public DigitalInput create(DigitalInputConfig config) {
                context.shutdown(currentPin);
                DigitalInputProvider provider = context.provider(IOType.DIGITAL_INPUT);
                return provider.create(config);
            }

            @Override
            public DigitalInput create() {
                return create(reuse(DigitalInputConfigBuilder.newInstance()).build());
            }
        };
    }

    /**
     * Copies the addressing (BCM pin and bus) of the pin being reconfigured onto the given builder,
     * so the reused configuration targets the same physical line.
     */
    private <B extends DigitalConfigBuilder<B, ?>> B reuse(B builder) {
        DigitalConfig<?> config = (DigitalConfig<?>) currentPin.config();
        builder.bcm(config.bcm());
        if (config.bus() != null) {
            builder.bus(config.bus());
        }
        return builder;
    }
}

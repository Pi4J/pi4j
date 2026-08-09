package com.pi4j.io.gpio.parallel;

import com.pi4j.io.exception.IOException;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalOutputBase;
import com.pi4j.io.gpio.digital.DigitalOutputConfig;
import com.pi4j.io.gpio.digital.DigitalOutputProvider;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.provider.ProviderBase;

import java.util.concurrent.atomic.AtomicReference;

/**
 * A provider for creating digital output instances from this parallel port.
 * <p>
 * The bcm numbering of digital devices created by this provider corresponds to the bits in the parallel port's value.
 * E.g. If the {@link ParallelPort} is mapped to pins 10 and 12, the logical bcm of for the {@link DigitalOutput}s
 * creatable via this instance would be `0` and `1` respectively.
 */
public class VirtualDigitalOutputProvider
    extends ProviderBase<DigitalOutputProvider, DigitalOutput, DigitalOutputConfig>
    implements DigitalOutputProvider {

    /**
     * The parallel port instance that is shared by all {@link DigitalOutput}s created by this provider.
     */
    private final ParallelPort port;

    /**
     * An in-memory representation of the parallel port's value. This approach assumes that all write operations
     * are performed via {@link DigitalOutput} devices created by this provider.
     * <p>
     * Alternative approaches could include:
     * <ul>
     *     <li>Perform a read of the port's value before setting writing via {@link DigitalOutput}.</li>
     *     <li>Shared state with a {@link ParallelPort} implementation.</li>
     * </ul>
     * This could possibly be swapped for an explicit read of the port's value before setting the port's value.
     */
    private final AtomicReference<Integer> portValue;

    /**
     * Factory responsible for creating digital outputs. Actual construction is fulfilled by
     * {@link VirtualDigitalOutput}. The abstraction allows composition of additional or shared
     * responsibilities, avoiding possible inheritance from a base class to benefit from shared code.
     */
    private final VirtualDigitalFactory<DigitalOutput, DigitalOutputConfig> factory;

    /**
     * Construct a new provider which uses the given parallel port to allocate devices.
     * @param port the underlying parallel port
     */
    public VirtualDigitalOutputProvider(ParallelPort port) {
        this.port = port;
        this.portValue = new AtomicReference<>(port.read());
        this.factory = new ValidatingVirtualDigitalFactory<>(port.config(), VirtualDigitalOutput::new);
    }

    @Override
    public DigitalOutput create(DigitalOutputConfig config) {
        return factory.createDigital(config);
    }


    /**
     * A {@link DigitalOutput} implementation backed by {@link VirtualDigitalOutputProvider#port}.
     */
    private class VirtualDigitalOutput extends DigitalOutputBase {

        /**
         * Mask for the BCM pin associated with this digital input.
         */
        private final int bcmMask;

        private VirtualDigitalOutput(DigitalOutputConfig config) {
            super(VirtualDigitalOutputProvider.this, config);
            this.bcmMask = 1 << config.bcm();
        }

        @Override
        public DigitalState state() {
            var intValue = ((port.read() & bcmMask) != 0) ? 1 : 0;
            return DigitalState.state(intValue);
        }

        @Override
        public DigitalOutput state(DigitalState state) throws IOException {
            if (port.getDirection() != ParallelPort.Direction.OUTPUT) {
                throw new IOException("Port direction is not set to OUTPUT");
            }
            var intValue = state.isHigh() ? 1 : 0;
            if (intValue != 0) {
                portValue.getAndUpdate(current -> current | bcmMask);
            } else {
                portValue.getAndUpdate(current -> current & ~bcmMask);
            }
            port.write(portValue.get());
            return super.state(state);
        }

    }
}

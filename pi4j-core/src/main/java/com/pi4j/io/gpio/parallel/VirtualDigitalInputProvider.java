package com.pi4j.io.gpio.parallel;

import com.pi4j.io.gpio.digital.DigitalBase;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalInputConfig;
import com.pi4j.io.gpio.digital.DigitalInputProvider;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.gpio.digital.DigitalStateChangeEvent;
import com.pi4j.io.gpio.digital.DigitalStateChangeListener;
import com.pi4j.provider.ProviderBase;

import java.util.HashMap;
import java.util.Map;

/**
 * A provider for creating digital input instances from this parallel port.
 * <p>
 * The bcm numbering of digital devices created by this provider corresponds to the bits in the parallel port's value.
 * E.g. If the {@link ParallelPort} is mapped to pins 10 and 12, the logical bcm of for the {@link DigitalInput}s
 * creatable via this instance would be `0` and `1` respectively.
 * <p>
 * All {@link DigitalInput}s created by this provider share the same {@link ParallelPort} instance and therefore
 * also the same event source.
 */
public class VirtualDigitalInputProvider
    extends ProviderBase<DigitalInputProvider, DigitalInput, DigitalInputConfig>
    implements DigitalInputProvider {

    /**
     * The parallel port instance that is shared by all {@link DigitalInput}s created by this provider.
     */
    private final ParallelPort port;

    /**
     * Factory responsible for creating digital inputs. Actual construction is fulfilled by
     * {@link VirtualDigitalInput}. The abstraction allows composition of additional or shared
     * responsibilities, avoiding possible inheritance from a base class to benefit from shared code.
     */
    private final VirtualDigitalFactory<DigitalInput, DigitalInputConfig> factory;

    /**
     * Construct a new provider which uses the given parallel port to allocate devices.
     * @param port the underlying parallel port
     */
    public VirtualDigitalInputProvider(ParallelPort port) {
        this.port = port;
        this.factory = new ValidatingVirtualDigitalFactory<>(port.config(), VirtualDigitalInput::new);
    }

    @Override
    public DigitalInput create(DigitalInputConfig config) {
        return factory.createDigital(config);
    }

    /**
     * {@link DigitalInput} implementation backed by {@link VirtualDigitalInputProvider#port}
     */
    private class VirtualDigitalInput
        extends DigitalBase<DigitalInput, DigitalInputConfig, DigitalInputProvider>
        implements DigitalInput {

        /**
         * Mask for the BCM pin associated with this digital input.
         */
        private final int bcmMask;

        private final Map<DigitalStateChangeListener, ListenerPair> listeners = new HashMap<>();

        private VirtualDigitalInput(DigitalInputConfig config) {
            super(VirtualDigitalInputProvider.this, config);
            this.bcmMask = 1 << config.bcm();
        }

        @Override
        public DigitalState state() {
            var intValue = ((port.read() & bcmMask) != 0) ? 1 : 0;
            return DigitalState.state(intValue);
        }

        /**
         * Hypothetical implementation of adding listeners to the digital input.
         * <p>
         * This simply forwards any event on the port to every digital listener regardless of whether value-change
         * on the port actually affects the digital input.
         * <p>
         * If we want to filter events for each digital device, we might need to track state and compare the before
         * and after values of the port.
         * <p>
         * If a port is used to back only a single pin, this is not an issue as the events would have a 1:1 mapping.
         */
        @Override
        public DigitalInput addListener(DigitalStateChangeListener... listener) {
            ParallelPort.Listener parallelPortListener = _ -> {
                for (var l : listener) {
                    l.onDigitalStateChange(new DigitalStateChangeEvent<>(this, state()));
                }
            };

            port.addListener(parallelPortListener);

            for (var l : listener) {
                listeners.put(l, new ListenerPair(l, parallelPortListener));
            }

            return super.addListener(listener);
        }

        /**
         * Hypothetical implementation of removing listeners from the digital input.
         */
        @Override
        public DigitalInput removeListener(DigitalStateChangeListener... listener) {
            for (var l : listener) {
                var listenerReference = listeners.remove(l);
                if (listenerReference != null) {
                    port.removeListener(listenerReference.parallelPortListener());
                }
            }
            return super.removeListener(listener);
        }

        private record ListenerPair(
            DigitalStateChangeListener listener,
            ParallelPort.Listener parallelPortListener
        ) {}
    }
}

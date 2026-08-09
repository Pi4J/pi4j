package com.pi4j.io.gpio.parallel;

import com.pi4j.io.exception.IOAlreadyExistsException;
import com.pi4j.io.exception.IOBoundsException;
import com.pi4j.io.gpio.MaskUtils;
import com.pi4j.io.gpio.digital.Digital;
import com.pi4j.io.gpio.digital.DigitalConfig;

/**
 * Package private factory decorator with responsibility to validate configuration before delegating construction.
 * <p>
 * The functionality provided by this factory is common to both input and output providers.
 *
 * @param <D> type of digital output
 * @param <C> type of digital output config
 */
final class ValidatingVirtualDigitalFactory<D extends Digital<D, C, ?>, C extends DigitalConfig> implements VirtualDigitalFactory<D, C> {

    /**
     * The delegate used to perform actual construction
     */
    private final VirtualDigitalFactory<D, C> delegate;

    /**
     * A mask representing the valid pins which can be allocated by the underlying port
     */
    private final long portMask;

    /**
     * The number of pins available on the underlying port
     */
    private final int portPinCount;

    /**
     * Tracks which pins have already been allocated
     */
    private long inUseOffsets = 0;

    /**
     * Creates a new validating virtual digital factory
     * @param config the port configuration
     * @param delegate the delegate used to perform actual construction
     */
    public ValidatingVirtualDigitalFactory(ParallelPortConfig config, VirtualDigitalFactory<D, C> delegate) {
        this.delegate = delegate;
        this.portMask = MaskUtils.packed(config.mask());
        this.portPinCount = Long.bitCount(portMask);
    }

    /**
     * Validate the device configuration before creating a new virtual digital device
     * @param config the device configuration
     * @return the new virtual digital device
     */
    @Override
    public D createDigital(C config) {
        final int bcmMask = 1 << config.bcm();

        // check that the requested pin is in the range of physical pins available
        if ((bcmMask & portMask) == 0) {
            throw new IOBoundsException(config.bcm(), 0, portPinCount);
        }

        // check that the requested pin has not already been allocated
        if ((inUseOffsets & bcmMask) != 0) {
            throw new IOAlreadyExistsException(config.bcm());
        }

        // mark the pin as allocated
        inUseOffsets = inUseOffsets | bcmMask;
        return delegate.createDigital(config);
    }
}

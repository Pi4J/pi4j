package com.pi4j.plugin.ffm.common.spi;

import com.pi4j.plugin.ffm.common.Pi4JLayout;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;
import java.lang.foreign.ValueLayout;

/**
 * Mutable adapter around a single {@link SpiIocTransfer} that retains the on-heap transmit and
 * receive byte arrays together with the native {@link MemorySegment}s they are copied into.
 * Because {@code struct spi_ioc_transfer} references its data buffers by address, this helper keeps
 * those segments alive and reachable across the {@link #to(MemorySegment, SegmentAllocator)} /
 * {@code SPI_IOC_MESSAGE} ioctl / {@link #from(MemorySegment, SegmentAllocator)} round-trip so the
 * received bytes can be read back afterwards. Implements the {@link Pi4JLayout} marshalling contract.
 */
public final class SpiTransferBuffer implements Pi4JLayout {
    private SpiIocTransfer spiIocTransfer;
    private final byte[] txBuf;
    private final byte[] rxBuf;

    private MemorySegment txMemorySegment = MemorySegment.NULL;
    private MemorySegment rxMemorySegment = MemorySegment.NULL;

    /**
     * Creates a transfer holder configured with the full set of {@code spi_ioc_transfer} options.
     *
     * @param txBuf          bytes to transmit
     * @param rxBuf          buffer that will receive the bytes read back
     * @param length         number of bytes to transfer
     * @param speedHz        bus clock speed for this transfer in Hertz; 0 uses the device default
     * @param delayUsecs     delay in microseconds after the transfer before deselecting the device
     * @param bitsPerWord    word size in bits; 0 uses the device default (typically 8)
     * @param csChange       if non-zero, deselect the device after this transfer
     * @param txNbits        number of bit lanes used for transmit (e.g. 1, 2 or 4 for dual/quad SPI)
     * @param rxNbits        number of bit lanes used for receive
     * @param wordDelayUsecs inter-word delay in microseconds
     * @param pad            structure padding; reserved, normally zero
     */
    public SpiTransferBuffer(byte[] txBuf, byte[] rxBuf, int length, int speedHz, int delayUsecs, byte bitsPerWord,
                             byte csChange, byte txNbits, byte rxNbits, byte wordDelayUsecs,
                             byte pad) {
        this.spiIocTransfer = new SpiIocTransfer(txBuf, rxBuf, length, speedHz, delayUsecs, bitsPerWord, csChange, txNbits, rxNbits, wordDelayUsecs, pad);
        this.txBuf = txBuf;
        this.rxBuf = rxBuf;
    }

    /**
     * Creates a transfer holder with all optional settings left at their defaults (zero).
     *
     * @param txBuf  bytes to transmit
     * @param rxBuf  buffer that will receive the bytes read back
     * @param length number of bytes to transfer
     */
    public SpiTransferBuffer(byte[] txBuf, byte[] rxBuf, int length) {
        this(txBuf, rxBuf, length, 0, 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0);
    }

    /**
     * Creates a transfer holder with a post-transfer delay, leaving the remaining settings at zero.
     *
     * @param txBuf      bytes to transmit
     * @param rxBuf      buffer that will receive the bytes read back
     * @param length     number of bytes to transfer
     * @param delayUsecs delay in microseconds after the transfer before deselecting the device
     */
    public SpiTransferBuffer(byte[] txBuf, byte[] rxBuf, int length, int delayUsecs) {
        this(txBuf, rxBuf, length, 0, delayUsecs, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0);
    }

    /**
     * Creates an empty transfer holder with zero-length transmit and receive buffers, suitable as a
     * placeholder or target to be populated from native memory.
     *
     * @return an empty {@code SpiTransferBuffer}
     */
    public static SpiTransferBuffer createEmpty() {
        return new SpiTransferBuffer(new byte[0], new byte[0], 0);
    }

    @Override
    public MemoryLayout getMemoryLayout() {
        // delegates all to underlying object
        return spiIocTransfer.getMemoryLayout();
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException always; the tracked tx/rx data segments require an
     *                                       allocator, so use {@link #from(MemorySegment, SegmentAllocator)}
     */
    @Override
    public <T extends Pi4JLayout> T from(MemorySegment buffer) throws Throwable {
        throw new UnsupportedOperationException("Converting from MemorySegment without context is not supported");
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException always; the tracked tx/rx data segments require an
     *                                       allocator, so use {@link #to(MemorySegment, SegmentAllocator)}
     */
    @Override
    public void to(MemorySegment buffer) throws Throwable {
        throw new UnsupportedOperationException("Converting to MemorySegment without context is not supported");
    }

    /**
     * Decodes the {@code spi_ioc_transfer} from {@code buffer}, reading the received payload back
     * from the native receive segment retained by the preceding {@link #to(MemorySegment, SegmentAllocator)}.
     *
     * @param buffer    native memory holding the {@code spi_ioc_transfer} scalar fields
     * @param allocator allocator context (unused here; the data segments were allocated during {@code to})
     * @return this instance, with its underlying transfer refreshed from native memory
     * @throws Throwable if reading the fields from native memory fails
     */
    @Override
    @SuppressWarnings("unchecked")
    public SpiTransferBuffer from(MemorySegment buffer, SegmentAllocator allocator) throws Throwable {
        // delegates all to underlying object
        this.spiIocTransfer = spiIocTransfer.from(buffer, txMemorySegment, rxMemorySegment);
        return this;
    }

    /**
     * Allocates native segments for the transmit and receive buffers through {@code allocator},
     * retains them for the later read-back, and encodes the {@code spi_ioc_transfer} into
     * {@code buffer} with the buffers' native addresses.
     *
     * @param buffer    native memory to be filled with the {@code spi_ioc_transfer} fields
     * @param allocator allocator used to back the transmit and receive data segments
     * @throws Throwable if allocating or writing the native memory fails
     */
    @Override
    public void to(MemorySegment buffer, SegmentAllocator allocator) throws Throwable {
        this.txMemorySegment = createMemorySegment(txBuf, allocator);
        this.rxMemorySegment = createMemorySegment(rxBuf, allocator);
        // delegates all to underlying object
        spiIocTransfer.to(buffer, txMemorySegment.address(), rxMemorySegment.address());
    }

    private MemorySegment createMemorySegment(byte[] buffer, SegmentAllocator context) {
        var memorySegment = MemorySegment.NULL;
        if (buffer != null) {
            memorySegment = context.allocateFrom(ValueLayout.JAVA_BYTE, buffer);
        }
        return memorySegment;
    }

    /**
     * Returns the transmit payload of the underlying transfer.
     *
     * @return the bytes that were (or will be) transmitted
     */
    public byte[] getTxBuffer() {
        return spiIocTransfer.txBuf();
    }

    /**
     * Returns the receive payload of the underlying transfer, populated after a transfer has been
     * read back via {@link #from(MemorySegment, SegmentAllocator)}.
     *
     * @return the bytes received during the transfer
     */
    public byte[] getRxBuffer() {
        return spiIocTransfer.rxBuf();
    }

    @Override
    public String toString() {
        return "SpiTransferBuffer{" +
            "spiIocTransfer=" + spiIocTransfer +
            ", txBuf=" + txBuf +
            ", rxBuf=" + rxBuf +
            '}';
    }
}

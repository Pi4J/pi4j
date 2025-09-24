package com.pi4j.plugin.ffm.common.spi;

import com.pi4j.plugin.ffm.common.Pi4JLayout;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;
import java.lang.foreign.ValueLayout;

/**
 * Class-helper that holds tx and rx buffers for ioctl.
 * This is needed, because data is transferred by reference and we need to track buffers between calls.
 * Consider this helper as a simple adapter for the underlying {@link SpiIocTransfer} class.
 */
public final class SpiTransferBuffer implements Pi4JLayout {
    private SpiIocTransfer spiIocTransfer;
    private final byte[] txBuf;
    private final byte[] rxBuf;

    private MemorySegment txMemorySegment;
    private MemorySegment rxMemorySegment;

    /**
     * Creates new transfer holder.
     *
     * @param txBuf          send buffer
     * @param rxBuf          receive buffer
     * @param length         buffer length
     * @param speedHz        spi bus speed in Hertz
     * @param delayUsecs     custom delay in nanoseconds
     * @param bitsPerWord    bits per word setting (default is 8 bits)
     * @param csChange       byte for full duplex setting
     * @param txNbits        ?
     * @param rxNbits        ?
     * @param wordDelayUsecs ?
     * @param pad            ?
     */
    public SpiTransferBuffer(byte[] txBuf, byte[] rxBuf, int length, int speedHz, short delayUsecs, byte bitsPerWord,
                             byte csChange, byte txNbits, byte rxNbits, byte wordDelayUsecs,
                             byte pad) {
        this.spiIocTransfer = new SpiIocTransfer(txBuf, rxBuf, length, speedHz, delayUsecs, bitsPerWord, csChange, txNbits, rxNbits, wordDelayUsecs, pad);
        this.txBuf = txBuf;
        this.rxBuf = rxBuf;
    }

    /**
     * Creates new transfer holder.
     *
     * @param txBuf  send buffer
     * @param rxBuf  receive buffer
     * @param length buffer length
     */
    public SpiTransferBuffer(byte[] txBuf, byte[] rxBuf, int length) {
        this(txBuf, rxBuf, length, 0, (short) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0);
    }


    @Override
    public MemoryLayout getMemoryLayout() {
        // delegates all to underlying object
        return spiIocTransfer.getMemoryLayout();
    }

    @Override
    public <T extends Pi4JLayout> T from(MemorySegment buffer) throws Throwable {
        throw new UnsupportedOperationException("Converting from MemorySegment without context is not supported");
    }

    @Override
    public void to(MemorySegment buffer) throws Throwable {
        throw new UnsupportedOperationException("Converting to MemorySegment without context is not supported");
    }

    @Override
    @SuppressWarnings("unchecked")
    public SpiTransferBuffer from(MemorySegment buffer, SegmentAllocator allocator) throws Throwable {
        // delegates all to underlying object
        this.spiIocTransfer = spiIocTransfer.from(buffer, txMemorySegment, rxMemorySegment);
        return this;
    }

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

    public byte[] getTxBuffer() {
        return spiIocTransfer.txBuf();
    }

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

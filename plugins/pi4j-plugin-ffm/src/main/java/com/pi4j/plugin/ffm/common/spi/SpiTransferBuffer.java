package com.pi4j.plugin.ffm.common.spi;

import com.pi4j.plugin.ffm.common.Pi4JLayout;

import java.lang.foreign.Arena;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

/**
 * Class-helper that holds tx and rx buffers for ioctl.
 * This is needed, because data is transferred by reference and we need to track buffers between calls.
 * Consider this helper as a simple adapter for the underlying {@link SpiIocTransfer} class.
 */
public final class SpiTransferBuffer implements Pi4JLayout {
    private static final Arena ARENA = Arena.ofConfined();
    private SpiIocTransfer spiIocTransfer;
    private MemorySegment txBuf;
    private MemorySegment rxBuf;

    /**
     * Creates new transfer holder.
     * @param txBuf send buffer
     * @param rxBuf receive buffer
     * @param length buffer length
     * @param speedHz spi bus speed in Hertz
     * @param delayUsecs custom delay in nanoseconds
     * @param bitsPerWord bits per word setting (default is 8 bits)
     * @param csChange byte for full duplex setting
     * @param txNbits ?
     * @param rxNbits ?
     * @param wordDelayUsecs ?
     * @param pad ?
     */
    public SpiTransferBuffer(byte[] txBuf, byte[] rxBuf, int length, int speedHz, short delayUsecs, byte bitsPerWord,
                             byte csChange, byte txNbits, byte rxNbits, byte wordDelayUsecs,
                             byte pad) {
        this.spiIocTransfer = new SpiIocTransfer(txBuf, rxBuf, length, speedHz, delayUsecs, bitsPerWord, csChange, txNbits, rxNbits, wordDelayUsecs, pad);
        if (txBuf != null) {
            this.txBuf = ARENA.allocateFrom(ValueLayout.JAVA_BYTE, txBuf);
        }
        if (rxBuf != null) {
            this.rxBuf = ARENA.allocateFrom(ValueLayout.JAVA_BYTE, rxBuf);
        }
    }

    /**
     * Creates new transfer holder.
     * @param txBuf send buffer
     * @param rxBuf receive buffer
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
    @SuppressWarnings("unchecked")
    public SpiTransferBuffer from(MemorySegment buffer) throws Throwable {
        // delegates all to underlying object
        this.spiIocTransfer = spiIocTransfer.from(buffer, txBuf, rxBuf);
        return this;
    }

    @Override
    public void to(MemorySegment buffer) throws Throwable {
        // delegates all to underlying object
        var txAddress = txBuf != null ? txBuf.address() : 0;
        var rxAddress = rxBuf != null ? rxBuf.address() : 0;
        spiIocTransfer.to(buffer, txAddress, rxAddress);
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

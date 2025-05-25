package com.pi4j.plugin.ffm.common.spi;

import com.pi4j.plugin.ffm.common.Pi4JLayout;

import java.lang.foreign.Arena;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

public final class SpiTransferBuffer implements Pi4JLayout {
    private static final Arena ARENA = Arena.ofConfined();
    private SpiIocTransfer spiIocTransfer;
    private MemorySegment txBuf;
    private MemorySegment rxBuf;

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

    public SpiTransferBuffer(byte[] txBuf, byte[] rxBuf, int length) {
        this(txBuf, rxBuf, length, 0, (short) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0);
    }


    @Override
    public MemoryLayout getMemoryLayout() {
        return spiIocTransfer.getMemoryLayout();
    }

    @Override
    @SuppressWarnings("unchecked")
    public SpiTransferBuffer from(MemorySegment buffer) throws Throwable {
        this.spiIocTransfer = spiIocTransfer.from(buffer, txBuf, rxBuf);
        return this;
    }

    @Override
    public void to(MemorySegment buffer) throws Throwable {
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
}

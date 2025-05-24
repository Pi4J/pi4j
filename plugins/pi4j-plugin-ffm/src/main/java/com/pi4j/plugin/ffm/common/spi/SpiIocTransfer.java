package com.pi4j.plugin.ffm.common.spi;

import com.pi4j.plugin.ffm.common.Pi4JLayout;

import java.lang.foreign.Arena;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;
import java.util.Arrays;

import static java.lang.foreign.MemoryLayout.PathElement.groupElement;

/**
 * Source: /usr/src/linux-headers-6.8.0-52-generic/include/linux/spi/spidev.h:70:0
 */
public record SpiIocTransfer(byte[] txBuf, byte[] rxBuf, int length, int speedHz, short delayUsecs, byte bitsPerWord,
                             byte csChange, byte txNbits, byte rxNbits, byte wordDelayUsecs,
                             byte pad) implements Pi4JLayout {

    public SpiIocTransfer(byte[] txBuf, byte[] rxBuf, int length) {
        this(txBuf, rxBuf, length, 0, (short) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0);
    }

    public static final MemoryLayout LAYOUT = MemoryLayout.structLayout(
        ValueLayout.JAVA_LONG.withName("tx_buf"),
        ValueLayout.JAVA_LONG.withName("rx_buf"),
        ValueLayout.JAVA_INT.withName("len"),
        ValueLayout.JAVA_INT.withName("speed_hz"),
        ValueLayout.JAVA_SHORT.withName("delay_usecs"),
        ValueLayout.JAVA_BYTE.withName("bits_per_word"),
        ValueLayout.JAVA_BYTE.withName("cs_change"),
        ValueLayout.JAVA_BYTE.withName("tx_nbits"),
        ValueLayout.JAVA_BYTE.withName("rx_nbits"),
        ValueLayout.JAVA_BYTE.withName("word_delay_usecs"),
        ValueLayout.JAVA_BYTE.withName("pad")
    );

    private static final VarHandle VH_TX_BUF = LAYOUT.varHandle(groupElement("tx_buf"));
    private static final VarHandle VH_RX_BUF = LAYOUT.varHandle(groupElement("rx_buf"));

    private static final VarHandle VH_LEN = LAYOUT.varHandle(groupElement("len"));
    private static final VarHandle VH_SPEED_HZ = LAYOUT.varHandle(groupElement("speed_hz"));
    private static final VarHandle VH_DELAY_USECS = LAYOUT.varHandle(groupElement("delay_usecs"));
    private static final VarHandle VH_BITS_PER_WORD = LAYOUT.varHandle(groupElement("bits_per_word"));
    private static final VarHandle VH_CS_CHANGE = LAYOUT.varHandle(groupElement("cs_change"));
    private static final VarHandle VH_TX_NBITS = LAYOUT.varHandle(groupElement("tx_nbits"));
    private static final VarHandle VH_RX_NBITS = LAYOUT.varHandle(groupElement("rx_nbits"));
    private static final VarHandle VH_WORD_DELAY_USECS = LAYOUT.varHandle(groupElement("word_delay_usecs"));
    private static final VarHandle VH_PAD = LAYOUT.varHandle(groupElement("pad"));


    public static SpiIocTransfer create(MemorySegment memorySegment) throws Throwable {
        var spiIocTransferInstance = SpiIocTransfer.createEmpty();
        if (!memorySegment.equals(MemorySegment.NULL)) {
            spiIocTransferInstance = spiIocTransferInstance.from(memorySegment);
        }
        return spiIocTransferInstance;
    }

    public static SpiIocTransfer createEmpty() {
        return new SpiIocTransfer(new byte[]{}, new byte[]{}, 0, 0, (short) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0);
    }

    @Override
    public MemoryLayout getMemoryLayout() {
        return LAYOUT;
    }

    @Override
    @SuppressWarnings("unchecked")
    public SpiIocTransfer from(MemorySegment buffer) throws Throwable {
        return new SpiIocTransfer(
            txBuf1 != null ? txBuf1.toArray(ValueLayout.JAVA_BYTE) : new byte[]{},
            rxBuf1 != null ? rxBuf1.toArray(ValueLayout.JAVA_BYTE) : new byte[]{},
            (int) VH_LEN.get(buffer, 0L),
            (int) VH_SPEED_HZ.get(buffer, 0L),
            (short) VH_DELAY_USECS.get(buffer, 0L),
            (byte) VH_BITS_PER_WORD.get(buffer, 0L),
            (byte) VH_CS_CHANGE.get(buffer, 0L),
            (byte) VH_TX_NBITS.get(buffer, 0L),
            (byte) VH_RX_NBITS.get(buffer, 0L),
            (byte) VH_WORD_DELAY_USECS.get(buffer, 0L),
            (byte) VH_PAD.get(buffer, 0L));
    }

    public static MemorySegment txBuf1;
    public static MemorySegment rxBuf1;

    @Override
    public void to(MemorySegment buffer) throws Throwable {
        if (txBuf != null) {
            txBuf1 = Arena.global().allocateFrom(ValueLayout.JAVA_BYTE, txBuf);
            VH_TX_BUF.set(buffer, 0L, txBuf1.address());
        }
        if (rxBuf != null) {
            rxBuf1 = Arena.global().allocateFrom(ValueLayout.JAVA_BYTE, rxBuf);
            VH_RX_BUF.set(buffer, 0L, rxBuf1.address());
        }
        VH_LEN.set(buffer, 0L, length);
        VH_SPEED_HZ.set(buffer, 0L, speedHz);
        VH_DELAY_USECS.set(buffer, 0L, delayUsecs);
        VH_BITS_PER_WORD.set(buffer, 0L, bitsPerWord);
        VH_CS_CHANGE.set(buffer, 0L, csChange);
        VH_TX_NBITS.set(buffer, 0L, txNbits);
        VH_RX_NBITS.set(buffer, 0L, rxNbits);
        VH_WORD_DELAY_USECS.set(buffer, 0L, wordDelayUsecs);
        VH_PAD.set(buffer, 0L, pad);
    }


    @Override
    public String toString() {
        return "SpiIocTransfer{" +
            "txBuf=" + Arrays.toString(txBuf) +
            ", rxBuf=" + Arrays.toString(rxBuf) +
            ", length=" + length +
            ", speedHz=" + speedHz +
            ", delayUsecs=" + delayUsecs +
            ", bitsPerWord=" + bitsPerWord +
            ", csChange=" + csChange +
            ", txNbits=" + txNbits +
            ", rxNbits=" + rxNbits +
            ", wordDelayUsecs=" + wordDelayUsecs +
            ", pad=" + pad +
            '}';
    }
}

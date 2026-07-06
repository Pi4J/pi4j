package com.pi4j.plugin.ffm.common.spi;

import com.pi4j.plugin.ffm.common.Pi4JLayout;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;
import java.util.Arrays;

import static java.lang.foreign.MemoryLayout.PathElement.groupElement;

/**
 * {@link MemorySegment}-backed mapping of the Linux {@code struct spi_ioc_transfer}
 * (include/uapi/linux/spi/spidev.h), the descriptor passed to the
 * {@code SPI_IOC_MESSAGE} ioctl to perform a single full-duplex SPI transfer.
 * In native memory the {@code tx_buf}/{@code rx_buf} fields are 64-bit pointers to
 * the data buffers; here the buffer contents are held as byte arrays and the
 * pointer fields are written separately via {@link #to(MemorySegment, long, long)}.
 * Package-private wrapper used through {@link SpiTransferBuffer}; implements the
 * {@link Pi4JLayout} marshalling contract.
 *
 * @param txBuf          bytes to transmit (mapped to the {@code tx_buf} pointer)
 * @param rxBuf          buffer receiving the bytes read back (mapped to the {@code rx_buf} pointer)
 * @param length         number of bytes to transfer ({@code len})
 * @param speedHz        bus clock speed for this transfer in Hertz ({@code speed_hz}); 0 uses the device default
 * @param delayUsecs     delay in microseconds after this transfer before deselecting ({@code delay_usecs})
 * @param bitsPerWord    word size in bits ({@code bits_per_word}); 0 uses the device default (typically 8)
 * @param csChange       if non-zero, deselect the device after this transfer ({@code cs_change})
 * @param txNbits        number of bit lanes used for transmit ({@code tx_nbits}; e.g. 1, 2 or 4 for dual/quad SPI)
 * @param rxNbits        number of bit lanes used for receive ({@code rx_nbits})
 * @param wordDelayUsecs inter-word delay in microseconds ({@code word_delay_usecs})
 * @param pad            structure padding ({@code pad}); reserved, normally zero
 */
record SpiIocTransfer(byte[] txBuf, byte[] rxBuf, int length, int speedHz, int delayUsecs, byte bitsPerWord,
                      byte csChange, byte txNbits, byte rxNbits, byte wordDelayUsecs,
                      byte pad) implements Pi4JLayout {

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


    @Override
    public MemoryLayout getMemoryLayout() {
        return LAYOUT;
    }

    @Override
    @SuppressWarnings("unchecked")
    public SpiIocTransfer from(MemorySegment buffer) throws Throwable {
        return from(buffer, MemorySegment.NULL, MemorySegment.NULL);
    }

    /**
     * Decodes a {@code struct spi_ioc_transfer} into a new {@code SpiIocTransfer}, reading the
     * scalar settings from {@code buffer} and the transmit/receive payloads from the separately
     * tracked data segments. A {@link MemorySegment#NULL} data segment is decoded as an empty array.
     *
     * @param buffer native memory holding the {@code spi_ioc_transfer} scalar fields
     * @param txBuf  native memory backing the transmit buffer, or {@link MemorySegment#NULL}
     * @param rxBuf  native memory backing the receive buffer (holds the bytes read back), or {@link MemorySegment#NULL}
     * @return a new {@code SpiIocTransfer} populated from the supplied segments
     */
    SpiIocTransfer from(MemorySegment buffer, MemorySegment txBuf, MemorySegment rxBuf) {
        return new SpiIocTransfer(
            txBuf != MemorySegment.NULL ? txBuf.toArray(ValueLayout.JAVA_BYTE) : new byte[]{},
            rxBuf != MemorySegment.NULL ? rxBuf.toArray(ValueLayout.JAVA_BYTE) : new byte[]{},
            (int) VH_LEN.get(buffer, 0L),
            (int) VH_SPEED_HZ.get(buffer, 0L),
            (int) VH_DELAY_USECS.get(buffer, 0L),
            (byte) VH_BITS_PER_WORD.get(buffer, 0L),
            (byte) VH_CS_CHANGE.get(buffer, 0L),
            (byte) VH_TX_NBITS.get(buffer, 0L),
            (byte) VH_RX_NBITS.get(buffer, 0L),
            (byte) VH_WORD_DELAY_USECS.get(buffer, 0L),
            (byte) VH_PAD.get(buffer, 0L));
    }

    @Override
    public void to(MemorySegment buffer) throws Throwable {
        VH_LEN.set(buffer, 0L, length);
        VH_SPEED_HZ.set(buffer, 0L, speedHz);
        VH_DELAY_USECS.set(buffer, 0L, (short) delayUsecs);
        VH_BITS_PER_WORD.set(buffer, 0L, bitsPerWord);
        VH_CS_CHANGE.set(buffer, 0L, csChange);
        VH_TX_NBITS.set(buffer, 0L, txNbits);
        VH_RX_NBITS.set(buffer, 0L, rxNbits);
        VH_WORD_DELAY_USECS.set(buffer, 0L, wordDelayUsecs);
        VH_PAD.set(buffer, 0L, pad);
    }

    /**
     * Encodes this transfer into a {@code struct spi_ioc_transfer} in native memory, writing the
     * scalar settings and storing the supplied native addresses into the {@code tx_buf}/{@code rx_buf}
     * pointer fields. The pointer for a side is only written when its corresponding byte array is
     * non-null.
     *
     * @param buffer    native memory to be filled with the {@code spi_ioc_transfer} fields
     * @param txAddress native address of the transmit buffer, written to {@code tx_buf}
     * @param rxAddress native address of the receive buffer, written to {@code rx_buf}
     * @throws Throwable if writing the fields to native memory fails
     */
    public void to(MemorySegment buffer, long txAddress, long rxAddress) throws Throwable {
        if (txBuf != null) {
            VH_TX_BUF.set(buffer, 0L, txAddress);
        }
        if (rxBuf != null) {
            VH_RX_BUF.set(buffer, 0L, rxAddress);
        }
        to(buffer);
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

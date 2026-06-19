package com.pi4j.plugin.ffm.common.spi;

import com.pi4j.plugin.ffm.common.Pi4JLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;

/**
 * Marshals an array of {@link SpiTransferBuffer} as a contiguous sequence of
 * {@code struct spi_ioc_transfer} records, the payload of a multi-message
 * {@code SPI_IOC_MESSAGE} ioctl. Each element is laid out back-to-back so the
 * kernel processes them as a single chained transaction. Implements the
 * {@link Pi4JLayout} marshalling contract, but only the allocator-aware
 * {@link #from(MemorySegment, SegmentAllocator)} and
 * {@link #to(MemorySegment, SegmentAllocator)} overloads are supported, since
 * the tx/rx data buffers must be allocated in native memory.
 *
 * @param transferBuffer the individual SPI transfers, in execution order
 */
public record SpiMultipleTransferBuffer(SpiTransferBuffer... transferBuffer) implements Pi4JLayout {
    private static final Logger logger = LoggerFactory.getLogger(SpiMultipleTransferBuffer.class);

    /**
     * {@inheritDoc}
     *
     * <p>Returns a sequence layout of one {@code spi_ioc_transfer} per element, sized to the number
     * of chained transfers.
     */
    @Override
    public MemoryLayout getMemoryLayout() {
        return MemoryLayout.sequenceLayout(transferBuffer.length, transferBuffer[0].getMemoryLayout());
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException always; decoding the chained transfers requires an
     *                                       allocator, so use {@link #from(MemorySegment, SegmentAllocator)}
     */
    @Override
    public <T extends Pi4JLayout> T from(MemorySegment buffer) throws Throwable {
        throw new UnsupportedOperationException("Converting from MemorySegment without context is not supported");
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException always; encoding the chained transfers requires an
     *                                       allocator, so use {@link #to(MemorySegment, SegmentAllocator)}
     */
    @Override
    public void to(MemorySegment buffer) throws Throwable {
        throw new UnsupportedOperationException("Converting to MemorySegment without context is not supported");
    }

    /**
     * Decodes each chained {@code struct spi_ioc_transfer} from its slice of {@code buffer},
     * updating every {@link SpiTransferBuffer} element in place (notably copying back the bytes
     * received during the transaction).
     *
     * @param buffer    native memory holding the contiguous sequence of {@code spi_ioc_transfer} records
     * @param allocator allocator backing the per-transfer tx/rx data segments
     * @return this instance, with its element transfers refreshed from native memory
     * @throws Throwable if reading the fields from native memory fails
     */
    @Override
    @SuppressWarnings("unchecked")
    public SpiMultipleTransferBuffer from(MemorySegment buffer, SegmentAllocator allocator) throws Throwable {
        // delegates all to underlying object
        for (int i = 0; i < transferBuffer.length; i++) {
            var spiTransferBuffer = transferBuffer[i];
            var slice = buffer.asSlice(i * spiTransferBuffer.getMemoryLayout().byteSize());
            transferBuffer[i] = transferBuffer[i].from(slice, allocator);
            if (logger.isTraceEnabled()) {
                logger.trace("SPI transfer buffer {}: {}", i, transferBuffer[i]);
            }
        }
        return this;
    }

    /**
     * Encodes every {@link SpiTransferBuffer} element into its slice of {@code buffer}, allocating
     * native tx/rx data segments through {@code allocator} and writing their addresses into the
     * corresponding {@code spi_ioc_transfer} records.
     *
     * @param buffer    native memory sized to hold the contiguous sequence of {@code spi_ioc_transfer} records
     * @param allocator allocator used to back the per-transfer tx/rx data segments
     * @throws Throwable if writing the fields to native memory fails
     */
    @Override
    public void to(MemorySegment buffer, SegmentAllocator allocator) throws Throwable {
        var memorySegments = buffer.elements(SpiIocTransfer.LAYOUT).toList();
        logger.debug("Number of SPI transfer buffers: {}", memorySegments.size());
        for (MemorySegment spiTransferBuffer : memorySegments) {
            if (logger.isTraceEnabled()) {
                logger.trace("SPI transfer buffer size: {}, {}", spiTransferBuffer.byteSize(), spiTransferBuffer);
            }
            var index = memorySegments.indexOf(spiTransferBuffer);
            transferBuffer[index].to(spiTransferBuffer, allocator);
        }
    }
}


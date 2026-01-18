package com.pi4j.plugin.ffm.common.spi;

import com.pi4j.plugin.ffm.common.Pi4JLayout;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;

public record SpiMultipleTransferBuffer(SpiTransferBuffer... transferBuffer) implements Pi4JLayout {

    @Override
    public MemoryLayout getMemoryLayout() {
        return MemoryLayout.sequenceLayout(transferBuffer.length, transferBuffer[0].getMemoryLayout());
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
    public SpiMultipleTransferBuffer from(MemorySegment buffer, SegmentAllocator allocator) throws Throwable {
        // delegates all to underlying object
        for (int i = 0; i < transferBuffer.length; i++) {
            var spiTransferBuffer = this.transferBuffer[i];
            var slice = buffer.asSlice(i * spiTransferBuffer.getMemoryLayout().byteSize());
            spiTransferBuffer.from(slice, allocator);
        }
        return this;
    }

    @Override
    public void to(MemorySegment buffer, SegmentAllocator allocator) throws Throwable {
        var memorySegments = buffer.elements(SpiIocTransfer.LAYOUT).toList();
        for (MemorySegment spiTransferBuffer : memorySegments) {
            var index = memorySegments.indexOf(spiTransferBuffer);
            var newBuffer = SpiTransferBuffer.createEmpty().from(spiTransferBuffer, allocator);
            transferBuffer[index] = newBuffer;
        }
    }
}


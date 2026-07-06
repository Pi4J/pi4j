package com.pi4j.plugin.ffm.model;

import com.pi4j.plugin.ffm.common.spi.SpiMultipleTransferBuffer;
import com.pi4j.plugin.ffm.common.spi.SpiTransferBuffer;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;

import java.lang.foreign.Arena;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SPITest {

    @Test
    public void testSpiTransferBuffer() throws Throwable {
        try (var offheap = Arena.ofConfined()) {
            var writeBuffer = "Test1".getBytes();
            var readBuffer = "Test2".getBytes();
            var data = new SpiTransferBuffer(writeBuffer, readBuffer, 1, 2, 3, (byte) 4, (byte) 5, (byte) 6, (byte) 7, (byte) 8, (byte) 9);
            var buffer = offheap.allocate(data.getMemoryLayout());
            data.to(buffer, offheap);
            var data2 = data.from(buffer, offheap);
            assertTrue(new ReflectionEquals(data2).matches(data));

            data = new SpiTransferBuffer(null, readBuffer, 1, 2, 3, (byte) 4, (byte) 5, (byte) 6, (byte) 7, (byte) 8, (byte) 9);
            buffer = offheap.allocate(data.getMemoryLayout());
            data.to(buffer, offheap);
            data2 = data.from(buffer, offheap);
            assertTrue(new ReflectionEquals(data2).matches(data));

            data = new SpiTransferBuffer(writeBuffer, null, 1, 2, 3, (byte) 4, (byte) 5, (byte) 6, (byte) 7, (byte) 8, (byte) 9);
            buffer = offheap.allocate(data.getMemoryLayout());
            data.to(buffer, offheap);
            data2 = data.from(buffer, offheap);
            assertTrue(new ReflectionEquals(data2).matches(data));
        }
    }

    @Test
    public void testSpiMultipleTransferBuffer() throws Throwable {
        try (var offheap = Arena.ofConfined()) {
            var writeBuffer = "Test2".getBytes();
            var readBuffer = "Test3".getBytes();
            var inputTransferBuffer = new SpiTransferBuffer(writeBuffer, new byte[0], 1, 2, 3, (byte) 4, (byte) 5, (byte) 6, (byte) 7, (byte) 8, (byte) 9);
            var outputTransferBuffer = new SpiTransferBuffer(new byte[0], readBuffer, 10, 11, 12, (byte) 13, (byte) 14, (byte) 15, (byte) 16, (byte) 17, (byte) 18);
            var transferBuffer = new SpiMultipleTransferBuffer(inputTransferBuffer, outputTransferBuffer);

            var buffer = offheap.allocate(transferBuffer.getMemoryLayout());
            transferBuffer.to(buffer, offheap);
            var transferBuffer2 = transferBuffer.from(buffer, offheap);

            assertTrue(new ReflectionEquals(transferBuffer2).matches(transferBuffer));
        }
    }
}

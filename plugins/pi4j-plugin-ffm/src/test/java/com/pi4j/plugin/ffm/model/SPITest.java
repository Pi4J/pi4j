package com.pi4j.plugin.ffm.model;

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
            var data = new SpiTransferBuffer(writeBuffer, readBuffer, 1, 2, (short) 3, (byte) 4, (byte) 5, (byte) 6, (byte) 7, (byte) 8, (byte) 9);
            var buffer = offheap.allocate(data.getMemoryLayout());
            data.to(buffer, offheap);
            var data2 = data.from(buffer, offheap);
            assertTrue(new ReflectionEquals(data2).matches(data));

            data = new SpiTransferBuffer(null, readBuffer, 1, 2, (short) 3, (byte) 4, (byte) 5, (byte) 6, (byte) 7, (byte) 8, (byte) 9);
            buffer = offheap.allocate(data.getMemoryLayout());
            data.to(buffer, offheap);
            data2 = data.from(buffer, offheap);
            assertTrue(new ReflectionEquals(data2).matches(data));

            data = new SpiTransferBuffer(writeBuffer, null, 1, 2, (short) 3, (byte) 4, (byte) 5, (byte) 6, (byte) 7, (byte) 8, (byte) 9);
            buffer = offheap.allocate(data.getMemoryLayout());
            data.to(buffer, offheap);
            data2 = data.from(buffer, offheap);
            assertTrue(new ReflectionEquals(data2).matches(data));
        }
    }
}

package com.pi4j.plugin.ffm.model;

import com.pi4j.plugin.ffm.common.serial.Termios2;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;

import java.lang.foreign.Arena;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SerialTest {
    @Test
    public void testSerial() throws Throwable {
        try (var offheap = Arena.ofConfined()) {
            var data = new Termios2(1, 2, 3, 4, (byte) 5, new byte[]{1, 2, 3}, 6, 7);
            var buffer = offheap.allocate(Termios2.LAYOUT);
            data.to(buffer);
            var data2 = data.from(buffer);
            assertTrue(new ReflectionEquals(data2).matches(data));
        }
    }
}

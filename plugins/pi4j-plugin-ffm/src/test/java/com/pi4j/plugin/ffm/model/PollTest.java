package com.pi4j.plugin.ffm.model;

import com.pi4j.plugin.ffm.common.poll.structs.PollingData;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;

import java.lang.foreign.Arena;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PollTest {

    @Test
    public void testPollingData() throws Throwable {
        try (var offheap = Arena.ofConfined()) {
            var data = new PollingData(1, (short) 2, (short) 3);
            var buffer = offheap.allocate(PollingData.LAYOUT);
            data.to(buffer);
            var data2 = data.from(buffer);
            assertTrue(new ReflectionEquals(data2).matches(data));
        }
    }
}

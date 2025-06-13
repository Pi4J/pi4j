package com.pi4j.plugin.ffm;

import com.pi4j.plugin.ffm.common.gpio.structs.*;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;

import java.lang.foreign.Arena;

import static org.junit.jupiter.api.Assertions.*;

public class ModelTest {

    @Test
    public void testChipInfo() throws Throwable {
        try (var offheap = Arena.ofConfined()) {
            var chipInfo = new ChipInfo("Name".getBytes(), "Label".getBytes(), 5);
            var buffer = offheap.allocate(ChipInfo.LAYOUT);
            chipInfo.to(buffer);
            var chipInfo2 = chipInfo.from(buffer);
            assertTrue(new ReflectionEquals(chipInfo2).matches(chipInfo));
        }
    }

    @Test
    public void testLineAttribute() throws Throwable {
        try (var offheap = Arena.ofConfined()) {
            var lineAttribute = new LineAttribute(1,  5L, 0, 0);
            var buffer = offheap.allocate(LineAttribute.LAYOUT);
            lineAttribute.to(buffer);
            var lineAttribute2 = lineAttribute.from(buffer);
            assertTrue(new ReflectionEquals(lineAttribute2).matches(lineAttribute));

            lineAttribute = new LineAttribute(1,  0L, 5L, 0);
            buffer = offheap.allocate(LineAttribute.LAYOUT);
            lineAttribute.to(buffer);
            lineAttribute2 = lineAttribute.from(buffer);
            assertTrue(new ReflectionEquals(lineAttribute2).matches(lineAttribute));

            lineAttribute = new LineAttribute(1,  0L, 0, 5);
            buffer = offheap.allocate(LineAttribute.LAYOUT);
            lineAttribute.to(buffer);
            lineAttribute2 = lineAttribute.from(buffer);
            assertTrue(new ReflectionEquals(lineAttribute2).matches(lineAttribute));
        }
    }

    @Test
    public void testLineConfig() throws Throwable {
        try (var offheap = Arena.ofConfined()) {
            var lineConfig = new LineConfig(1, 2,
                new LineConfigAttribute[] {
                    new LineConfigAttribute(new LineAttribute(1,  5L, 0, 0), 1)
                });
            var buffer = offheap.allocate(LineConfig.LAYOUT);
            lineConfig.to(buffer);
            var lineConfig2 = lineConfig.from(buffer);
            assertTrue(new ReflectionEquals(lineConfig2).matches(lineConfig));
        }
    }

    @Test
    public void testLineConfigAttribute() throws Throwable {
        try (var offheap = Arena.ofConfined()) {
            var lineConfigAttribute = new LineConfigAttribute(new LineAttribute(1,  5L, 0, 0), 1);
            var buffer = offheap.allocate(LineConfigAttribute.LAYOUT);
            lineConfigAttribute.to(buffer);
            var lineConfigAttribute2 = lineConfigAttribute.from(buffer);
            assertTrue(new ReflectionEquals(lineConfigAttribute2).matches(lineConfigAttribute));
        }
    }

    @Test
    public void testLineEvent() throws Throwable {
        try (var offheap = Arena.ofConfined()) {
            var lineEvent = new LineEvent(1,2, 3, 4, 5);
            var buffer = offheap.allocate(LineEvent.LAYOUT);
            lineEvent.to(buffer);
            var lineEvent2 = lineEvent.from(buffer);
            assertTrue(new ReflectionEquals(lineEvent2).matches(lineEvent));
        }
    }

    @Test
    public void testLineInfo() throws Throwable {
        try (var offheap = Arena.ofConfined()) {
            var lineInfo = new LineInfo("Name".getBytes(), "Consumer".getBytes(), 1, 2, 3,
                new LineAttribute[]{
                    new LineAttribute(1, 2, 0, 0)
                });
            var buffer = offheap.allocate(LineInfo.LAYOUT);
            lineInfo.to(buffer);
            var lineInfo2 = lineInfo.from(buffer);
            assertTrue(new ReflectionEquals(lineInfo2).matches(lineInfo));
        }
    }

    @Test
    public void testLineRequest() throws Throwable {
        try (var offheap = Arena.ofConfined()) {
            var lineRequest = new LineRequest(new int[] {1,2}, "Consumer".getBytes(),
                new LineConfig(1, 2, new LineConfigAttribute[]{
                    new LineConfigAttribute(new LineAttribute(1, 2, 0,0), 1)
                }), 3, 4, 5
            );
            var buffer = offheap.allocate(LineRequest.LAYOUT);
            lineRequest.to(buffer);
            var lineRequest2 = lineRequest.from(buffer);
            assertArrayEquals(lineRequest2.offsets(), lineRequest.offsets());
            assertArrayEquals(lineRequest2.consumer(), lineRequest.consumer());
            assertEquals(lineRequest2.config().flags(), lineRequest.config().flags());
            assertEquals(lineRequest2.config().numAttrs(), lineRequest.config().numAttrs());
            assertTrue(new ReflectionEquals(lineRequest2.config().attrs()).matches(lineRequest.config().attrs()));
            assertEquals(lineRequest2.numLines(), lineRequest.numLines());
            assertEquals(lineRequest2.eventBufferSize(), lineRequest.eventBufferSize());
            assertEquals(lineRequest2.fd(), lineRequest.fd());
        }
    }

    @Test
    public void testLineValues() throws Throwable {
        try (var offheap = Arena.ofConfined()) {
            var lineValues = new LineValues(1, 2);
            var buffer = offheap.allocate(LineValues.LAYOUT);
            lineValues.to(buffer);
            var lineValues2 = lineValues.from(buffer);
            assertTrue(new ReflectionEquals(lineValues2).matches(lineValues));
        }
    }
}

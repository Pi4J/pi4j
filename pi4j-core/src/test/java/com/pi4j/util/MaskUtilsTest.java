package com.pi4j.util;

import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class MaskUtilsTest {

    private final long[] masks = IntStream.range(0, 64).mapToLong(i -> 1L << i).toArray();

    @Test
    void sanityCheck() {
        var offsets = new int[] { 1 };
        var mask = MaskUtils.mask(offsets);
        assertEquals(masks[1], mask);
        assertEquals(2, mask);
    }

    @Test
    void handlesSingleOffset() {
        for (long mask : masks) {
            var processedMask = MaskUtils.mask(MaskUtils.offsets(mask));
            assertEquals(mask, processedMask);
        }
    }

    @Test
    void handlesMultipleOffsets() {
        for (int i = 2; i < masks.length; i++) {
            var mask = masks[i] | masks[i - 2];
            var processedMask = MaskUtils.mask(MaskUtils.offsets(mask));
            assertEquals(mask, processedMask);
        }
    }

    @Test
    void canPackValuesAccordingToMask() {
        record Expectation(long mask, long value, long expected) {}

        for (var expectation : new Expectation[] {
            new Expectation(0b101, 0b000, 0b000),
            new Expectation(0b101, 0b001, 0b001),
            new Expectation(0b101, 0b100, 0b010),
            new Expectation(0b101, 0b101, 0b011)
        }) {
            assertEquals(expectation.expected, MaskUtils.packValueByMask(expectation.mask, expectation.value));
        }
    }

    @Test
    void canPackOffsetAccordingToMask() {
        record Expectation(long mask, int offset, long expected) {}

        for (var expectation : new Expectation[] {
            new Expectation(0b101, 0, 0),
            new Expectation(0b101, 1, -1), // invalid offset, should return -1
            new Expectation(0b101, 2, 1),
        }) {
            assertEquals(expectation.expected, MaskUtils.packOffsetByMask(expectation.mask, expectation.offset));
        }
    }
}

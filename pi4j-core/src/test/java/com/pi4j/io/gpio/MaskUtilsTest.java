package com.pi4j.io.gpio;

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
}

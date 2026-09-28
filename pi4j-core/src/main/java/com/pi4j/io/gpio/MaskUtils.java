package com.pi4j.io.gpio;

import java.util.PrimitiveIterator;

/**
 * Utility functions to convert between long value bit masks and offsets representing set bits
 */
public class MaskUtils {

    private MaskUtils() {}

    /**
     * Return the offsets of the set bits in the given mask
     * @param mask the mask value
     * @return an array of offsets
     */
    public static int[] offsets(long mask) {
        var offsets = new int[Long.bitCount(mask)];
        int index = 0;
        for (var offset : MaskIterator.of(mask)) {
            offsets[index++] = offset;
        }
        return offsets;
    }

    /**
     * Return a mask with the bits at the given offsets set
     * @param offsets the offsets of the bits to set
     * @return a mask with the bits at the given offsets set
     */
    public static long mask(int... offsets) {
        long mask = 0;
        for (var offset : offsets) {
            mask |= 1L << offset;
        }
        return mask;
    }

    public static long packed(long mask) {
        long packed = 0;
        for (int i = 0; i < Long.bitCount(mask); i++) {
            packed |= 1L << i;
        }
        return packed;
    }

    /**
     * Iterator over the offsets of a mask
     */
    private static class MaskIterator implements PrimitiveIterator.OfInt {

        private long remaining;

        private MaskIterator(long mask) {
            this.remaining = mask;
        }

        @Override
        public boolean hasNext() {
            return remaining != 0;
        }

        @Override
        public int nextInt() {
            int index = Long.numberOfTrailingZeros(remaining);
            remaining &= ~(1L << index);
            return index;
        }

        private static Iterable<Integer> of(long mask) {
            return () -> new MaskIterator(mask);
        }
    }
}

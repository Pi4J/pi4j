package com.pi4j.io.gpio;

import java.util.Collection;
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

    /**
     * Return a mask with the bits at the given offsets set
     * @param offsets the offsets of the bits to set
     * @return a mask with the bits at the given offsets set
     */
    public static long mask(Collection<Integer> offsets) {
        long mask = 0;
        for (var offset : offsets) {
            mask |= 1L << offset;
        }
        return mask;
    }

    /**
     * "pack" a value, removing any zero bits. This is used to convert hardware offsets to logical, integer
     * representations as is required when writing to GPIOs.
     * @param mask an unpacked mask
     * @return a packed mask with the same number of bits set as the set bits of input mask
     */
    public static long packed(long mask) {
        long packed = 0;
        for (int i = 0; i < Long.bitCount(mask); i++) {
            packed |= 1L << i;
        }
        return packed;
    }

    /**
     * Convert a value that complies with the mask to be packed to a logical value
     * @param mask an unpacked mask
     * @param value a value that complies with the mask
     * @return a packed, logical value
     */
    public static long packValueByMask(long mask, long value) {
        long out = 0;
        int i = 0;          // absolute bit position in value
        int k = 0;          // next bit position in packed output
        long m = mask;
        while (m != 0) {
            int tz = Long.numberOfTrailingZeros(m); // skip mask zero bits
            i += tz;
            m >>>= tz;
            out |= ((value >>> i) & 1L) << k;
            m >>>= 1;       // consumed this mask bit
            i++;            // move to next absolute bit position
            k++;            // advance packed output position
        }
        return out;
    }

    /**
     * Convert a hardware offset to the offset in the packed equivalent of the mask
     * @param mask the unpacked hardware mask
     * @param offset the offset in the hardware mask
     * @return the offset in the packed mask or -1 if the offset is not set in the mask
     */
    public static int packOffsetByMask(long mask, int offset) {
        int i = 0;          // absolute bit position in value
        int k = 0;          // next bit position in packed output
        long m = mask;
        while (m != 0) {
            int tz = Long.numberOfTrailingZeros(m); // skip mask zero bits
            i += tz;
            m >>>= tz;
            if (i == offset) {
                return k;
            }
            m >>>= 1;       // consumed this mask bit
            i++;            // move to next absolute bit position
            k++;            // advance packed output position
        }
        return -1;          // offset not found in mask
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

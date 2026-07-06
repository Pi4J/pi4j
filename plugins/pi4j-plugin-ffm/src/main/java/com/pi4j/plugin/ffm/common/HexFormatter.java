package com.pi4j.plugin.ffm.common;

import java.util.HexFormat;

/**
 * Utility that renders byte values and byte arrays as upper-case, {@code 0x}-prefixed hexadecimal
 * strings (e.g. {@code 0xFF} or {@code [0x01, 0xFF]}). Used throughout the FFM backend to produce
 * readable log and exception messages for native I/O payloads.
 */
public class HexFormatter {
    private static final HexFormat HEX = HexFormat.of().withUpperCase();
    private static final HexFormat HEX_ARRAY = HexFormat.ofDelimiter(", ").withUpperCase().withPrefix("0x");

    /**
     * Formats a single byte value to hex format, e.g. 0xFF
     *
     * @param value byte value to format
     * @return hex formatted byte value
     */
    public static String format(byte value) {
        return "0x" + HEX.toHexDigits(value);
    }

    /**
     * Formats byte array to hex format, e.g. [0x01, 0xFF]
     *
     * @param value byte array to format
     * @return hex formatted byte array
     */
    public static String format(byte[] value) {
        return HEX_ARRAY.formatHex(value);
    }
}

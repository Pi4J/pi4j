package com.pi4j.plugin.ffm.common;

import java.util.HexFormat;

/**
 * Helper to properly format hex values sent as bytes or byte arrays.
 * E.g. 0xFF or [0x01, 0xFF]
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

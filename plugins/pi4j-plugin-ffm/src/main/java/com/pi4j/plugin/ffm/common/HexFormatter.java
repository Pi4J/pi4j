package com.pi4j.plugin.ffm.common;

import java.util.HexFormat;

public class HexFormatter {
    private static final HexFormat HEX = HexFormat.of().withUpperCase();
    private static final HexFormat HEX_ARRAY = HexFormat.ofDelimiter(", ").withUpperCase().withPrefix("0x");

    public static String format(byte value) {
        return "0x" + HEX.toHexDigits(value);
    }

    public static String format(byte[] value) {
        return HEX_ARRAY.formatHex(value);
    }
}

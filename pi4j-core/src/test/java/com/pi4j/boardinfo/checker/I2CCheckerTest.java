package com.pi4j.boardinfo.checker;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class I2CCheckerTest {

    @Test
    void shouldParseI2CDevices() {
        var output = """
                 0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f
            00:                         -- -- -- -- -- -- -- --
            10: -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
            20: -- 21 -- -- -- -- -- -- -- -- -- -- -- -- -- --
            30: -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
            40: -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
            50: -- -- -- -- -- -- -- -- -- -- -- -- 5c -- -- --
            60: -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
            70: 70 -- -- -- -- -- -- --
            """;

        var list = I2CChecker.parseI2CDeviceAddresses(output);

        assertEquals(3, list.size());
        assertEquals((byte) 0x21, list.getFirst());
        assertEquals((byte) 0x5c, list.get(1));
        assertEquals((byte) 0x70, list.getLast());
    }
}

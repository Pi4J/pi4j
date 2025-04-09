package com.pi4j.io.spi;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SpiChipSelectTest {

    @Test
    void shouldReturnDefaultForString() {
        assertEquals(Spi.DEFAULT_CHIP_SELECT, SpiChipSelect.parse(""));
        assertEquals(Spi.DEFAULT_CHIP_SELECT, SpiChipSelect.parse("A"));
        assertEquals(Spi.DEFAULT_CHIP_SELECT, SpiChipSelect.parse("*"));
        assertEquals(Spi.DEFAULT_CHIP_SELECT, SpiChipSelect.parse("none"));
    }

    @Test
    void shouldReturnDefaultForUndefinedNumber() {
        assertEquals(Spi.DEFAULT_CHIP_SELECT, SpiChipSelect.parse("11"));
        assertEquals(Spi.DEFAULT_CHIP_SELECT, SpiChipSelect.parse("99"));
    }

    @Test
    void shouldReturnCorrectSpiChipSelectFromString() {
        assertEquals(SpiChipSelect.CS_0, SpiChipSelect.parse("0"));
        assertEquals(SpiChipSelect.CS_1, SpiChipSelect.parse("1"));
        assertEquals(SpiChipSelect.CS_5, SpiChipSelect.parse("5"));
        assertEquals(SpiChipSelect.CS_10, SpiChipSelect.parse("10"));
    }

    @Test
    void shouldReturnCorrectSpiChipSelectFromShort() {
        assertEquals(SpiChipSelect.CS_0, SpiChipSelect.getByNumber((short) 0));
        assertEquals(SpiChipSelect.CS_1, SpiChipSelect.getByNumber((short) 1));
        assertEquals(SpiChipSelect.CS_5, SpiChipSelect.getByNumber((short) 5));
        assertEquals(SpiChipSelect.CS_10, SpiChipSelect.getByNumber((short) 10));
    }

    @Test
    void shouldReturnCorrectSpiChipSelectFromInt() {
        assertEquals(SpiChipSelect.CS_0, SpiChipSelect.getByNumber(0));
        assertEquals(SpiChipSelect.CS_1, SpiChipSelect.getByNumber(1));
        assertEquals(SpiChipSelect.CS_5, SpiChipSelect.getByNumber(5));
        assertEquals(SpiChipSelect.CS_10, SpiChipSelect.getByNumber(10));
    }
}

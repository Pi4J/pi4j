package com.pi4j.boardinfo.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LineHelperTest {

    @Test
    void shouldCalculatePI15AsDescribedInBlogPost() {
        // Banana Pi physical pin 16 = PI15 = bank I, pin 15 -> 8 * 32 + 15 = 271
        assertEquals(271, LineHelper.getAddress('I', 15));
        assertEquals(271, LineHelper.getAddress(8, 15));
    }

    @Test
    void shouldCalculateFirstPinOfBankA() {
        assertEquals(0, LineHelper.getAddress('A', 0));
        assertEquals(0, LineHelper.getAddress(0, 0));
    }

    @Test
    void shouldCalculateLastPinOfBankA() {
        assertEquals(31, LineHelper.getAddress('A', 31));
    }

    @Test
    void shouldCalculateFirstPinOfBankB() {
        assertEquals(32, LineHelper.getAddress('B', 0));
    }

    @Test
    void shouldBeCaseInsensitive() {
        assertEquals(271, LineHelper.getAddress('i', 15));
    }

    @Test
    void shouldMatchGpiochip1LineCountOfNineBanks() {
        // gpioinfo reported gpiochip1 with 288 lines = 9 banks (A-I) of 32 pins each
        assertEquals(288, LineHelper.getAddress('J', 0));
    }

    @Test
    void shouldRejectNonLetterBank() {
        assertThrows(IllegalArgumentException.class, () -> LineHelper.getAddress('1', 0));
    }

    @Test
    void shouldRejectNegativeBankIndex() {
        assertThrows(IllegalArgumentException.class, () -> LineHelper.getAddress(-1, 0));
    }

    @Test
    void shouldRejectLineOutOfRange() {
        assertThrows(IllegalArgumentException.class, () -> LineHelper.getAddress('A', 32));
        assertThrows(IllegalArgumentException.class, () -> LineHelper.getAddress('A', -1));
    }

    @Test
    void shouldRejectBankIndexThatWouldOverflowInt() {
        assertThrows(IllegalArgumentException.class, () -> LineHelper.getAddress(Integer.MAX_VALUE / 32 + 1, 0));
        assertThrows(IllegalArgumentException.class, () -> LineHelper.getAddress(Integer.MAX_VALUE, 0));
    }

    @Test
    void shouldConvertBankLetterToIndex() {
        assertEquals(0, LineHelper.bankIndex('A'));
        assertEquals(8, LineHelper.bankIndex('I'));
        assertEquals(25, LineHelper.bankIndex('Z'));
    }

    @Test
    void shouldDefaultToAllwinnerFamily() {
        assertEquals(271, LineHelper.getAddress(GpioLineFamily.ALLWINNER, 'I', 15));
        assertEquals(271, LineHelper.getAddress(GpioLineFamily.ALLWINNER, 8, 15));
    }

    @Test
    void shouldCalculateTegraFirstPinOfBankA() {
        assertEquals(0, LineHelper.getAddress(GpioLineFamily.TEGRA, 'A', 0));
        assertEquals(0, LineHelper.getAddress(GpioLineFamily.TEGRA, 0, 0));
    }

    @Test
    void shouldCalculateTegraLastPinOfBankA() {
        assertEquals(7, LineHelper.getAddress(GpioLineFamily.TEGRA, 'A', 7));
    }

    @Test
    void shouldCalculateTegraFirstPinOfBankB() {
        assertEquals(8, LineHelper.getAddress(GpioLineFamily.TEGRA, 'B', 0));
    }

    @Test
    void shouldCalculateTegraPortOffsetAsDescribedInForumPost() {
        // Tegra multiplies by 8 per port instead of 32: port index 8 ('I'), pin 5 -> 8 * 8 + 5 = 69
        assertEquals(69, LineHelper.getAddress(GpioLineFamily.TEGRA, 'I', 5));
    }

    @Test
    void shouldRejectTegraLineOutOfRange() {
        assertThrows(IllegalArgumentException.class, () -> LineHelper.getAddress(GpioLineFamily.TEGRA, 'A', 8));
        assertThrows(IllegalArgumentException.class, () -> LineHelper.getAddress(GpioLineFamily.TEGRA, 'A', -1));
    }
}

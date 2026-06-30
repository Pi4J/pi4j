package com.pi4j.boardinfo.definition;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RevisionCodeTest {

    @Test
    void getInvalidRevisionCode() {
        assertAll(
            () -> assertThrows(IllegalArgumentException.class, () -> RevisionCode.of(null)),
            () -> assertThrows(IllegalArgumentException.class, () -> RevisionCode.of("")),
            () -> assertThrows(IllegalArgumentException.class, () -> RevisionCode.of("0X")),
            () -> assertThrows(IllegalArgumentException.class, () -> RevisionCode.of("0xBadHex")),
            () -> assertThrows(IllegalArgumentException.class, () -> RevisionCode.of("FFFFFFFFF"))
        );
    }

    @Test
    void testHashCodeEqualsToString() {
        final Object obj = new Object();
        final String rc1Str = "e04180";
        final String rc2Str = "0XE04180";
        final int rcInt = 0xe04180;
        final RevisionCode rc1 = RevisionCode.of(rc1Str);
        final RevisionCode rc2 = RevisionCode.of(rc2Str);
        assertAll(
            () -> assertNotEquals(rc1Str, rc2Str),
            () -> assertNotEquals(rc1, obj),
            () -> assertEquals(rc1, rc2),
            () -> assertEquals(rc1.hashCode(), rc2.hashCode()),
            () -> assertEquals(rc1.toString(), rc2.toString()),
            () -> assertNotEquals(rc1, RevisionCode.of(rcInt ^ (0x1 << 21))),      // Flip a memory size bit.
            () -> assertNotEquals(rc1, RevisionCode.of(rcInt ^ (0x1 << 5))),       // Flip a type bit.
            () -> assertNotEquals(rc1, RevisionCode.of(rcInt ^ (0x1 << 14))),      // Flip a processor bit.
            () -> assertNotEquals(rc1, RevisionCode.of(rcInt ^ 0x1))               // Flip a revision bit.
        );
    }

    @Test
    void getNewStyleRevisionCode() {
        int revisionCode = 0x800000;
            revisionCode |= (0x4 << 20);    // Set MemorySize GB_4
            revisionCode |= (0x4 << 12);    // Set Processor: BCM2712
            revisionCode |= (0x17 << 4);    // Set Type: RPI_5
            revisionCode |= 1;              // Set Revision: 1
        RevisionCode actual = RevisionCode.of(revisionCode);
        assertAll(
            () -> assertEquals(RevisionCode.MemorySize.GB_4, actual.memorySize()),
            () -> assertEquals(RevisionCode.Processor.BCM2712, actual.processor()),
            () -> assertEquals(RevisionCode.Type.RPI_5, actual.type()),
            () -> assertEquals(1, actual.revision())
        );
    }

    @Test
    void getOldStyleRevisionCode() {
        String revisionCode = "0x0002";
        RevisionCode actual = RevisionCode.of(revisionCode);
        assertAll(
            () -> assertEquals(RevisionCode.MemorySize.MB_256, actual.memorySize()),
            () -> assertEquals(RevisionCode.Processor.BCM2835, actual.processor()),
            () -> assertEquals(RevisionCode.Type.RPI_B, actual.type()),
            () -> assertEquals(1, actual.revision())
        );
    }

    @Test
    void getUnknownRevisionCode() {
        String revisionCode = "0x0001";
        RevisionCode actual = RevisionCode.of(revisionCode);
        assertAll(
            () -> assertEquals(RevisionCode.MemorySize.UNKNOWN, actual.memorySize()),
            () -> assertEquals(RevisionCode.Processor.UNKNOWN, actual.processor()),
            () -> assertEquals(RevisionCode.Type.UNKNOWN, actual.type()),
            () -> assertEquals(1, actual.revision())
        );
    }

    @Test
    void getMemorySizeFromRevisionCode() {
        assertAll(
            // New-style
            () -> assertEquals(RevisionCode.MemorySize.MB_256, RevisionCode.MemorySize.fromRevisionCode(0x800000)),
            () -> assertEquals(RevisionCode.MemorySize.MB_512, RevisionCode.MemorySize.fromRevisionCode(0x800000 | (0x1 << 20))),
            () -> assertEquals(RevisionCode.MemorySize.GB_1, RevisionCode.MemorySize.fromRevisionCode(0x800000 | (0x2 << 20))),
            () -> assertEquals(RevisionCode.MemorySize.GB_2, RevisionCode.MemorySize.fromRevisionCode(0x800000 | (0x3 << 20))),
            () -> assertEquals(RevisionCode.MemorySize.GB_4, RevisionCode.MemorySize.fromRevisionCode(0x800000 | (0x4 << 20))),
            () -> assertEquals(RevisionCode.MemorySize.GB_8, RevisionCode.MemorySize.fromRevisionCode(0x800000 | (0x5 << 20))),
            () -> assertEquals(RevisionCode.MemorySize.GB_16, RevisionCode.MemorySize.fromRevisionCode(0x800000 | (0x6 << 20))),
            () -> assertEquals(RevisionCode.MemorySize.OTHER, RevisionCode.MemorySize.fromRevisionCode(0x800000 | (0x7 << 20))),
            // Old-style
            () -> assertEquals(RevisionCode.MemorySize.MB_256, RevisionCode.MemorySize.fromRevisionCode(2)),
            () -> assertEquals(RevisionCode.MemorySize.MB_512, RevisionCode.MemorySize.fromRevisionCode(16)),
            () -> assertEquals(RevisionCode.MemorySize.OTHER, RevisionCode.MemorySize.fromRevisionCode(21)),
            () -> assertEquals(RevisionCode.MemorySize.UNKNOWN, RevisionCode.MemorySize.fromRevisionCode(0))
        );
    }

    @Test
    void getTypeFromRevisionCode() {
        assertAll(
            // New-style
            () -> assertEquals(RevisionCode.Type.RPI_A, RevisionCode.Type.fromRevisionCode(0x800000)),
            () -> assertEquals(RevisionCode.Type.RPI_B, RevisionCode.Type.fromRevisionCode(0x800000 | (0x01 << 4))),
            () -> assertEquals(RevisionCode.Type.RPI_A_PLUS, RevisionCode.Type.fromRevisionCode(0x800000 | (0x02 << 4))),
            () -> assertEquals(RevisionCode.Type.RPI_B_PLUS, RevisionCode.Type.fromRevisionCode(0x800000 | (0x03 << 4))),
            () -> assertEquals(RevisionCode.Type.RPI_2B, RevisionCode.Type.fromRevisionCode(0x800000 | (0x04 << 4))),
            () -> assertEquals(RevisionCode.Type.RPI_ALPHA, RevisionCode.Type.fromRevisionCode(0x800000 | (0x05 << 4))),
            () -> assertEquals(RevisionCode.Type.RPI_CM1, RevisionCode.Type.fromRevisionCode(0x800000 | (0x06 << 4))),
            () -> assertEquals(RevisionCode.Type.UNKNOWN, RevisionCode.Type.fromRevisionCode(0x800000 | (0x07 << 4))),
            () -> assertEquals(RevisionCode.Type.RPI_3B, RevisionCode.Type.fromRevisionCode(0x800000 | (0x08 << 4))),
            () -> assertEquals(RevisionCode.Type.RPI_ZERO, RevisionCode.Type.fromRevisionCode(0x800000 | (0x09 << 4))),
            () -> assertEquals(RevisionCode.Type.RPI_CM3, RevisionCode.Type.fromRevisionCode(0x800000 | (0x0a << 4))),
            () -> assertEquals(RevisionCode.Type.UNKNOWN, RevisionCode.Type.fromRevisionCode(0x800000 | (0x0b << 4))),
            () -> assertEquals(RevisionCode.Type.RPI_ZERO_W, RevisionCode.Type.fromRevisionCode(0x800000 | (0x0c << 4))),
            () -> assertEquals(RevisionCode.Type.RPI_3B_PLUS, RevisionCode.Type.fromRevisionCode(0x800000 | (0x0d << 4))),
            () -> assertEquals(RevisionCode.Type.RPI_3A_PLUS, RevisionCode.Type.fromRevisionCode(0x800000 | (0x0e << 4))),
            () -> assertEquals(RevisionCode.Type.UNKNOWN, RevisionCode.Type.fromRevisionCode(0x800000 | (0x0f << 4))),
            () -> assertEquals(RevisionCode.Type.RPI_CM3_PLUS, RevisionCode.Type.fromRevisionCode(0x800000 | (0x10 << 4))),
            () -> assertEquals(RevisionCode.Type.RPI_4B, RevisionCode.Type.fromRevisionCode(0x800000 | (0x11 << 4))),
            () -> assertEquals(RevisionCode.Type.RPI_ZERO_2_W, RevisionCode.Type.fromRevisionCode(0x800000 | (0x12 << 4))),
            () -> assertEquals(RevisionCode.Type.RPI_400, RevisionCode.Type.fromRevisionCode(0x800000 | (0x13 << 4))),
            () -> assertEquals(RevisionCode.Type.RPI_CM4, RevisionCode.Type.fromRevisionCode(0x800000 | (0x14 << 4))),
            () -> assertEquals(RevisionCode.Type.RPI_CM4S, RevisionCode.Type.fromRevisionCode(0x800000 | (0x15 << 4))),
            () -> assertEquals(RevisionCode.Type.UNKNOWN, RevisionCode.Type.fromRevisionCode(0x800000 | (0x16 << 4))),
            () -> assertEquals(RevisionCode.Type.RPI_5, RevisionCode.Type.fromRevisionCode(0x800000 | (0x17 << 4))),
            () -> assertEquals(RevisionCode.Type.RPI_CM5, RevisionCode.Type.fromRevisionCode(0x800000 | (0x18 << 4))),
            () -> assertEquals(RevisionCode.Type.RPI_500_500_PLUS, RevisionCode.Type.fromRevisionCode(0x800000 | (0x19 << 4))),
            () -> assertEquals(RevisionCode.Type.RPI_CM5_LITE, RevisionCode.Type.fromRevisionCode(0x800000 | (0x1a << 4))),
            () -> assertEquals(RevisionCode.Type.RPI_CM0, RevisionCode.Type.fromRevisionCode(0x800000 | (0x1b << 4))),
            // Old-style
            () -> assertEquals(RevisionCode.Type.RPI_A, RevisionCode.Type.fromRevisionCode(0x7)),
            () -> assertEquals(RevisionCode.Type.RPI_B, RevisionCode.Type.fromRevisionCode(0x3)),
            () -> assertEquals(RevisionCode.Type.RPI_A_PLUS, RevisionCode.Type.fromRevisionCode(0x12)),
            () -> assertEquals(RevisionCode.Type.RPI_B_PLUS, RevisionCode.Type.fromRevisionCode(0x13)),
            () -> assertEquals(RevisionCode.Type.RPI_CM1, RevisionCode.Type.fromRevisionCode(0x11)),
            () -> assertEquals(RevisionCode.Type.UNKNOWN, RevisionCode.Type.fromRevisionCode(0x1a))
        );
    }

    @Test
    void getProcessorFromRevisionCode() {
        assertAll(
            // New-style
            () -> assertEquals(RevisionCode.Processor.BCM2835, RevisionCode.Processor.fromRevisionCode(0x800000)),
            () -> assertEquals(RevisionCode.Processor.BCM2836, RevisionCode.Processor.fromRevisionCode(0x800000 | (0x1 << 12))),
            () -> assertEquals(RevisionCode.Processor.BCM2837, RevisionCode.Processor.fromRevisionCode(0x800000 | (0x2 << 12))),
            () -> assertEquals(RevisionCode.Processor.BCM2711, RevisionCode.Processor.fromRevisionCode(0x800000 | (0x3 << 12))),
            () -> assertEquals(RevisionCode.Processor.BCM2712, RevisionCode.Processor.fromRevisionCode(0x800000 | (0x4 << 12))),
            () -> assertEquals(RevisionCode.Processor.UNKNOWN, RevisionCode.Processor.fromRevisionCode(0x800000 | (0x5 << 12))),
            () -> assertEquals(RevisionCode.Processor.UNKNOWN, RevisionCode.Processor.fromRevisionCode(0x800000 | (0x6 << 12))),
            () -> assertEquals(RevisionCode.Processor.UNKNOWN, RevisionCode.Processor.fromRevisionCode(0x800000 | (0x7 << 12))),
            () -> assertEquals(RevisionCode.Processor.UNKNOWN, RevisionCode.Processor.fromRevisionCode(0x800000 | (0x8 << 12))),
            () -> assertEquals(RevisionCode.Processor.UNKNOWN, RevisionCode.Processor.fromRevisionCode(0x800000 | (0x9 << 12))),
            () -> assertEquals(RevisionCode.Processor.UNKNOWN, RevisionCode.Processor.fromRevisionCode(0x800000 | (0xa << 12))),
            () -> assertEquals(RevisionCode.Processor.UNKNOWN, RevisionCode.Processor.fromRevisionCode(0x800000 | (0xb << 12))),
            () -> assertEquals(RevisionCode.Processor.UNKNOWN, RevisionCode.Processor.fromRevisionCode(0x800000 | (0xc << 12))),
            () -> assertEquals(RevisionCode.Processor.UNKNOWN, RevisionCode.Processor.fromRevisionCode(0x800000 | (0xd << 12))),
            () -> assertEquals(RevisionCode.Processor.UNKNOWN, RevisionCode.Processor.fromRevisionCode(0x800000 | (0xe << 12))),
            () -> assertEquals(RevisionCode.Processor.UNKNOWN, RevisionCode.Processor.fromRevisionCode(0x800000 | (0xf << 12))),
            // Old-style
            () -> assertEquals(RevisionCode.Processor.BCM2835, RevisionCode.Processor.fromRevisionCode(0x2)),
            () -> assertEquals(RevisionCode.Processor.BCM2835, RevisionCode.Processor.fromRevisionCode(0x3)),
            () -> assertEquals(RevisionCode.Processor.BCM2835, RevisionCode.Processor.fromRevisionCode(0x4)),
            () -> assertEquals(RevisionCode.Processor.BCM2835, RevisionCode.Processor.fromRevisionCode(0x5)),
            () -> assertEquals(RevisionCode.Processor.BCM2835, RevisionCode.Processor.fromRevisionCode(0x6)),
            () -> assertEquals(RevisionCode.Processor.UNKNOWN, RevisionCode.Processor.fromRevisionCode(0x1a))
        );
    }
}

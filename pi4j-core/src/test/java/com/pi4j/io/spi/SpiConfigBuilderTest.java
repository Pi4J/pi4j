package com.pi4j.io.spi;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SpiConfigBuilderTest {

    @Test
    void testDefaultValues() {
        var config = Spi.newConfigBuilder().build();

        assertNotNull(config.id());
        assertEquals("SPI-0.0", config.id());
        assertEquals("SPI-0.0", config.name());
        assertEquals("SPI-0.0", config.description());
        assertEquals(SpiBus.BUS_0, config.bus());
        assertEquals(SpiBus.BUS_0, config.getBus());
        assertFalse(config.busUserProvided());
        assertFalse(config.getBusUserProvided());
        assertEquals(0, config.channel());
        assertEquals(0, config.getChannel());
        assertEquals(SpiChipSelect.CS_0, config.chipSelect());
        assertEquals(SpiChipSelect.CS_0, config.getChipSelect());
        assertEquals(SpiMode.MODE_0, config.mode());
        assertEquals(SpiMode.MODE_0, config.getMode());
        assertFalse(config.modeUserProvided());
        assertFalse(config.getModeUserProvided());
        assertEquals(Spi.DEFAULT_BAUD, config.baud());
        assertEquals(Spi.DEFAULT_BAUD, config.getBaud());
        assertEquals(0, config.readLsbFirst());
        assertEquals(0, config.getReadLsbFirst());
        assertFalse(config.readLsbFirstUserProvided());
        assertFalse(config.getReadLsbFIrstUserProvided());
        assertEquals(0, config.writeLsbFirst());
        assertEquals(0, config.getWriteLsbFirst());
        assertFalse(config.writeLsbFirstUserProvided());
        assertFalse(config.getWriteLsbFIrstUserProvided());
        assertNull(config.flags());
        assertNull(config.getFlags());
    }

    @Test
    void testCustomIdAndProperties() {
        var config = Spi.newConfigBuilder()
            .id("custom-spi")
            .name("custom-name")
            .description("custom-desc")
            .bus(SpiBus.BUS_1)
            .channel(1)
            .mode(SpiMode.MODE_3)
            .baud(500000)
            .readLsbFirst(1)
            .writeLsbFirst(1)
            .flags(12345L)
            .build();

        assertEquals("custom-spi", config.id());
        assertEquals("custom-name", config.name());
        assertEquals("custom-desc", config.description());
        assertEquals(SpiBus.BUS_1, config.bus());
        assertTrue(config.busUserProvided());
        assertEquals(1, config.channel());
        assertEquals(SpiChipSelect.CS_1, config.chipSelect());
        assertEquals(SpiMode.MODE_3, config.mode());
        assertTrue(config.modeUserProvided());
        assertEquals(500000, config.baud());
        assertEquals(1, config.readLsbFirst());
        assertTrue(config.readLsbFirstUserProvided());
        assertEquals(1, config.writeLsbFirst());
        assertTrue(config.writeLsbFirstUserProvided());
        assertEquals(12345L, config.flags());
    }

    @Test
    void testNumericBusAndMode() {
        var config = Spi.newConfigBuilder()
            .bus(1)
            .mode(2)
            .build();

        assertEquals(SpiBus.BUS_1, config.bus());
        assertEquals(SpiMode.MODE_2, config.mode());
    }

    @Test
    void testUniqueIdentifier() {
        var config = Spi.newConfigBuilder()
            .bus(SpiBus.BUS_1)
            .channel(2)
            .build();

        int expected = (1 << 8) + 2;
        assertEquals(expected, config.getUniqueIdentifier());
    }

    @Test
    void testValidate() {
        var config = Spi.newConfigBuilder().build();
        assertDoesNotThrow(config::validate);

        var nullIdConfig = new SpiConfig(null, "name", "desc", SpiBus.BUS_0, 0,
            SpiMode.MODE_0, 1000000, 0, 0, null, false, false, false, false);
        assertThrows(IllegalStateException.class, nullIdConfig::validate);
    }

    @Test
    void testRecordEquality() {
        var config1 = Spi.newConfigBuilder().bus(SpiBus.BUS_0).channel(1).build();
        var config2 = Spi.newConfigBuilder().bus(SpiBus.BUS_0).channel(1).build();

        assertEquals(config1, config2);
        assertEquals(config1.hashCode(), config2.hashCode());
        assertEquals(config1.toString(), config2.toString());
    }
}

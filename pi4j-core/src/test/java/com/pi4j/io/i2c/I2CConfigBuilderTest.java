package com.pi4j.io.i2c;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class I2CConfigBuilderTest {

    @Test
    void testDefaultValues() {
        var config = I2C.newConfigBuilder().build();

        assertNotNull(config.id());
        assertEquals("I2C", config.id());
        assertEquals("I2C", config.name());
        assertEquals("I2C", config.description());
        assertNull(config.bus());
        assertNull(config.device());
        assertEquals(I2CImplementation.DIRECT, config.i2cImplementation());
        assertEquals(I2CImplementation.DIRECT, config.getI2CImplementation());
    }

    @Test
    void testBusAndDeviceGenerateDefaultId() {
        var config = I2C.newConfigBuilder()
            .bus(1)
            .device(0x10)
            .build();

        assertEquals("I2C-1.16", config.id());
        assertEquals("I2C-1.16", config.name());
        assertEquals("I2C-1.16", config.description());
        assertEquals(1, config.bus());
        assertEquals(1, config.getBus());
        assertEquals(0x10, config.device());
        assertEquals(0x10, config.getDevice());
    }

    @Test
    void testCustomIdAndProperties() {
        var config = I2C.newConfigBuilder()
            .id("custom-id")
            .name("custom-name")
            .description("custom-desc")
            .bus(2)
            .device(0x20)
            .i2cImplementation(I2CImplementation.SMBUS)
            .build();

        assertEquals("custom-id", config.id());
        assertEquals("custom-name", config.name());
        assertEquals("custom-desc", config.description());
        assertEquals(2, config.bus());
        assertEquals(0x20, config.device());
        assertEquals(I2CImplementation.SMBUS, config.i2cImplementation());
    }

    @Test
    void testUniqueIdentifier() {
        var config = I2C.newConfigBuilder()
            .bus(1)
            .device(0x27)
            .build();

        int expected = (1 << 8) + 0x27;
        assertEquals(expected, config.getUniqueIdentifier());
    }

    @Test
    void testValidate() {
        var config = I2C.newConfigBuilder().build();
        assertDoesNotThrow(config::validate);

        var nullIdConfig = new I2CConfig(null, "name", "desc", 1, 0x10, I2CImplementation.DIRECT);
        assertThrows(IllegalStateException.class, nullIdConfig::validate);
    }

    @Test
    void testRecordEquality() {
        var config1 = I2C.newConfigBuilder().bus(1).device(0x10).build();
        var config2 = I2C.newConfigBuilder().bus(1).device(0x10).build();

        assertEquals(config1, config2);
        assertEquals(config1.hashCode(), config2.hashCode());
        assertEquals(config1.toString(), config2.toString());
    }
}

package com.pi4j.io.gpio.digital;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DigitalOutputConfigBuilderTest {

    @Test
    void testDefaultValues() {
        var config = DigitalOutput.newConfigBuilder()
            .bcm(18)
            .build();

        assertEquals("DOUT-18", config.id());
        assertEquals("DOUT-18", config.name());
        assertEquals("DOUT-18", config.description());
        assertEquals(18, config.bcm());
        assertEquals(18, config.getBcm());
        assertEquals(18, config.address());
        assertEquals(18, config.getAddress());
        assertEquals(18, config.getUniqueIdentifier());
        assertEquals(0, config.bus());
        assertEquals(0, config.getBus());
        assertNull(config.initialState());
        assertNull(config.getInitialState());
        assertNull(config.shutdownState());
        assertNull(config.getShutdownState());
        assertEquals(DigitalState.HIGH, config.onState());
        assertEquals(DigitalState.HIGH, config.getOnState());
    }

    @Test
    void testCustomValues() {
        var config = DigitalOutput.newConfigBuilder()
            .id("custom-id")
            .name("custom-name")
            .description("custom-desc")
            .bcm(23)
            .bus(2)
            .initial(DigitalState.HIGH)
            .shutdown(DigitalState.LOW)
            .onState(DigitalState.LOW)
            .build();

        assertEquals("custom-id", config.id());
        assertEquals("custom-name", config.name());
        assertEquals("custom-desc", config.description());
        assertEquals(23, config.bcm());
        assertEquals(2, config.bus());
        assertEquals(DigitalState.HIGH, config.initialState());
        assertEquals(DigitalState.LOW, config.shutdownState());
        assertEquals(DigitalState.LOW, config.onState());
    }

    @Test
    void testLegacyAddressSetter() {
        var config = DigitalOutput.newConfigBuilder()
            .address(24)
            .build();

        assertEquals(24, config.bcm());
        assertEquals("DOUT-24", config.id());
    }

    @Test
    void testShutdownStateWither() {
        var config = DigitalOutput.newConfigBuilder()
            .bcm(18)
            .shutdown(DigitalState.HIGH)
            .build();

        assertEquals(DigitalState.HIGH, config.shutdownState());
        var updated = config.shutdownState(DigitalState.LOW);
        assertEquals(DigitalState.LOW, updated.shutdownState());
        assertEquals(DigitalState.HIGH, config.shutdownState());
    }

    @Test
    void testValidation() {
        var config = DigitalOutput.newConfigBuilder().bcm(18).build();
        assertDoesNotThrow(config::validate);

        var nullIdConfig = new DigitalOutputConfig(null, "name", "desc", 18, 0, null, null, DigitalState.HIGH);
        assertThrows(IllegalStateException.class, nullIdConfig::validate);
    }

    @Test
    void testDeprecatedFactory() {
        var builder = DigitalOutputConfigBuilder.newInstance(null);
        assertNotNull(builder);

        var deprecatedBuilder = DigitalOutputConfig.newBuilder(null);
        assertNotNull(deprecatedBuilder);
    }
}

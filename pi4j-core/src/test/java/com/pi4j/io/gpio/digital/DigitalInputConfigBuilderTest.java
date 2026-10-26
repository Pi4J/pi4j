package com.pi4j.io.gpio.digital;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class DigitalInputConfigBuilderTest {

    @Test
    void testDefaultValues() {
        var config = DigitalInput.newConfigBuilder()
            .bcm(4)
            .build();

        assertEquals("DIN-4", config.id());
        assertEquals("DIN-4", config.name());
        assertEquals("DIN-4", config.description());
        assertEquals(4, config.bcm());
        assertEquals(4, config.getBcm());
        assertEquals(4, config.address());
        assertEquals(4, config.getAddress());
        assertEquals(4, config.getUniqueIdentifier());
        assertEquals(0, config.bus());
        assertEquals(0, config.getBus());
        assertEquals(PullResistance.OFF, config.pull());
        assertEquals(PullResistance.OFF, config.getPull());
        assertEquals(DigitalInput.DEFAULT_DEBOUNCE, config.debounce());
        assertEquals(DigitalInput.DEFAULT_DEBOUNCE, config.getDebounce());
        assertEquals(DigitalState.HIGH, config.onState());
        assertEquals(DigitalState.HIGH, config.getOnState());
    }

    @Test
    void testCustomValues() {
        var config = DigitalInput.newConfigBuilder()
            .id("custom-id")
            .name("custom-name")
            .description("custom-desc")
            .bcm(17)
            .bus(1)
            .pull(PullResistance.PULL_UP)
            .debounce(2500L)
            .onState(DigitalState.LOW)
            .build();

        assertEquals("custom-id", config.id());
        assertEquals("custom-name", config.name());
        assertEquals("custom-desc", config.description());
        assertEquals(17, config.bcm());
        assertEquals(1, config.bus());
        assertEquals(PullResistance.PULL_UP, config.pull());
        assertEquals(2500L, config.debounce());
        assertEquals(DigitalState.LOW, config.onState());
    }

    @Test
    void testDebounceWithTimeUnit() {
        var config = DigitalInput.newConfigBuilder()
            .bcm(5)
            .debounce(5L, TimeUnit.MILLISECONDS)
            .build();

        assertEquals(5000L, config.debounce());
    }

    @Test
    void testLegacyAddressSetter() {
        var config = DigitalInput.newConfigBuilder()
            .address(22)
            .build();

        assertEquals(22, config.bcm());
        assertEquals("DIN-22", config.id());
    }

    @Test
    void testValidation() {
        var config = DigitalInput.newConfigBuilder().bcm(4).build();
        assertDoesNotThrow(config::validate);

        var nullIdConfig = new DigitalInputConfig(null, "name", "desc", 4, 0, PullResistance.OFF, 1000L, DigitalState.HIGH);
        assertThrows(IllegalStateException.class, nullIdConfig::validate);
    }

    @Test
    void testDeprecatedFactory() {
        var builder = DigitalInputConfigBuilder.newInstance(null);
        assertNotNull(builder);

        var deprecatedBuilder = DigitalInputConfig.newBuilder(null);
        assertNotNull(deprecatedBuilder);
    }
}

package com.pi4j.io.pwm;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PwmConfigBuilderTest {

    @Test
    void testDefaultValues() {
        var config = PwmConfigBuilder.newInstance().build();

        assertNotNull(config.id());
        assertEquals("PWM", config.id());
        assertEquals("PWM", config.name());
        assertEquals("PWM", config.description());
        assertNull(config.chip());
        assertNull(config.channel());
        assertNull(config.bcm());
        assertEquals(PwmType.HARDWARE, config.pwmType());
        assertEquals(PwmPolarity.NORMAL, config.polarity());
        assertNull(config.frequency());
        assertNull(config.dutyCycle());
        assertNull(config.initialValue());
        assertNull(config.shutdownValue());
    }

    @Test
    void testChannelGeneratesDefaultId() {
        var config = PwmConfigBuilder.newInstance()
            .channel(2)
            .build();

        assertEquals("PWM-2", config.id());
        assertEquals("PWM-2", config.name());
        assertEquals("PWM-2", config.description());
        assertEquals(2, config.channel());
        assertEquals(2, config.getChannel());
        assertEquals(2, config.address());
        assertEquals(2, config.getAddress());
    }

    @Test
    void testBcmGeneratesDefaultIdWhenChannelNull() {
        var config = PwmConfigBuilder.newInstance()
            .bcm(18)
            .build();

        assertEquals("PWM-18", config.id());
        assertEquals("PWM-18", config.name());
        assertEquals("PWM-18", config.description());
        assertEquals(18, config.bcm());
        assertEquals(18, config.getBcm());
    }

    @Test
    void testProviderProperty() {
        var config = PwmConfigBuilder.newInstance()
            .provider("custom-provider")
            .build();

        assertEquals("custom-provider", config.provider());
        assertEquals("custom-provider", config.getProvider());
    }

    @Test
    void testCustomIdAndProperties() {
        var config = PwmConfigBuilder.newInstance()
            .id("custom-id")
            .name("custom-name")
            .description("custom-desc")
            .chip(1)
            .channel(0)
            .bcm(12)
            .pwmType(PwmType.SOFTWARE)
            .polarity(PwmPolarity.INVERSED)
            .frequency(1000.0)
            .dutyCycle(75.5)
            .initial(50.0)
            .shutdown(0.0)
            .build();

        assertEquals("custom-id", config.id());
        assertEquals("custom-id", config.getId());
        assertEquals("custom-name", config.name());
        assertEquals("custom-name", config.getName());
        assertEquals("custom-desc", config.description());
        assertEquals("custom-desc", config.getDescription());
        assertEquals(1, config.chip());
        assertEquals(1, config.getChip());
        assertEquals(0, config.channel());
        assertEquals(0, config.getChannel());
        assertEquals(12, config.bcm());
        assertEquals(12, config.getBcm());
        assertEquals(PwmType.SOFTWARE, config.pwmType());
        assertEquals(PwmType.SOFTWARE, config.getPwmType());
        assertEquals(PwmPolarity.INVERSED, config.polarity());
        assertEquals(PwmPolarity.INVERSED, config.getPolarity());
        assertEquals(1000.0, config.frequency());
        assertEquals(1000.0, config.getFrequency());
        assertEquals(75.5, config.dutyCycle());
        assertEquals(75.5, config.getDutyCycle());
        assertEquals(50.0, config.initialValue());
        assertEquals(50.0, config.getInitialValue());
        assertEquals(0.0, config.shutdownValue());
        assertEquals(0.0, config.getShutdownValue());
    }

    @Test
    void testDutyCycleClamping() {
        var configNegative = PwmConfigBuilder.newInstance()
            .dutyCycle(-10.0)
            .initial(-5)
            .shutdown(-1)
            .build();

        assertEquals(0.0, configNegative.dutyCycle());
        assertEquals(0.0, configNegative.initialValue());
        assertEquals(0.0, configNegative.shutdownValue());

        var configOverMax = PwmConfigBuilder.newInstance()
            .dutyCycle(150.0)
            .initial(120)
            .shutdown(200)
            .build();

        assertEquals(100.0, configOverMax.dutyCycle());
        assertEquals(100.0, configOverMax.initialValue());
        assertEquals(100.0, configOverMax.shutdownValue());
    }

    @Test
    void testUniqueIdentifier() {
        var config = PwmConfigBuilder.newInstance()
            .chip(2)
            .channel(1)
            .bcm(18)
            .build();

        int expected = (2 << 16) + (1 << 8) + 18;
        assertEquals(expected, config.getUniqueIdentifier());
    }

    @Test
    void testValidate() {
        var config = PwmConfigBuilder.newInstance().build();
        assertDoesNotThrow(config::validate);

        var nullIdConfig = new PwmConfig(null, "name", "desc", null, null, null, null,
            PwmType.HARDWARE, PwmPolarity.NORMAL, null, null, null, null);
        assertThrows(IllegalStateException.class, nullIdConfig::validate);
    }

    @Test
    void testRecordEquality() {
        var config1 = PwmConfigBuilder.newInstance().chip(0).channel(1).frequency(500.0).build();
        var config2 = PwmConfigBuilder.newInstance().chip(0).channel(1).frequency(500.0).build();

        assertEquals(config1, config2);
        assertEquals(config1.hashCode(), config2.hashCode());
        assertEquals(config1.toString(), config2.toString());
    }
}

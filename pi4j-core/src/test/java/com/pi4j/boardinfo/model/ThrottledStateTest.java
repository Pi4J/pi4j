package com.pi4j.boardinfo.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ThrottledStateTest {

    @Test
    void testDecodeThrottledStateUndervoltageDetected() {
        int throttledStateInt = 0x1; // 0000000000000001 in binary
        var activeStates = ThrottledState.decode(throttledStateInt);

        assertAll(
            () -> assertEquals(1, activeStates.size(), "Should only have 1 active state"),
            () -> assertEquals(ThrottledState.UNDERVOLTAGE_DETECTED, activeStates.get(0), "Should be Undervoltage detected")
        );
    }

    @Test
    void testDecodeThrottledStateArmFrequencyCapped() {
        int throttledStateInt = 0x2; // 0000000000000010 in binary
        var activeStates = ThrottledState.decode(throttledStateInt);

        assertAll(
            () -> assertEquals(1, activeStates.size(), "Should only have 1 active state"),
            () -> assertEquals(ThrottledState.ARM_FREQUENCY_CAPPED, activeStates.get(0), "Should be Arm frequency capped")
        );
    }

    @Test
    void testDecodeThrottledStateCurrentlyThrottled() {
        int throttledStateInt = 0x4; // 0000000000000100 in binary
        var activeStates = ThrottledState.decode(throttledStateInt);

        assertAll(
            () -> assertEquals(1, activeStates.size(), "Should only have 1 active state"),
            () -> assertEquals(ThrottledState.CURRENTLY_THROTTLED, activeStates.get(0), "Should be Currently throttled")
        );
    }

    @Test
    void testDecodeCombinedThrottledStates() {
        int throttledStateInt = 0x5; // 0000000000000101 in binary (Undervoltage detected + Currently throttled)
        var activeStates = ThrottledState.decode(throttledStateInt);

        assertAll(
            () -> assertEquals(2, activeStates.size(), "Should have 2 active states"),
            () -> assertEquals(ThrottledState.UNDERVOLTAGE_DETECTED, activeStates.get(0), "Should be Undervoltage detected"),
            () -> assertEquals(ThrottledState.CURRENTLY_THROTTLED, activeStates.get(1), "Should be Currently throttled")
        );
    }

    @Test
    void testDecodeThrottledStateSoftTemperatureLimitActive() {
        int throttledStateInt = 0x8; // 0000000000001000 in binary
        var activeStates = ThrottledState.decode(throttledStateInt);

        assertAll(
            () -> assertEquals(1, activeStates.size(), "Should only have 1 active state"),
            () -> assertEquals(ThrottledState.SOFT_TEMPERATURE_LIMIT_ACTIVE, activeStates.get(0), "Should be Soft temperature limit active")
        );
    }

    @Test
    void testThrottledStateDescription() {
        int rawState = 0x50005; // rawState in hexadecimal: 0x50005 or 327685

        // Breakdown of the bits that are set:
        // 0x1 (bit 0): Undervoltage detected
        // 0x4 (bit 2): Currently throttled
        // 0x10000 (bit 16): Undervoltage has occurred
        // 0x40000 (bit 18): Throttling has occurred

        String description = ThrottledState.getActiveStatesDescription(rawState);

        // Assert that the description contains the correct states based on the bits set
        assertEquals(
            "Undervoltage detected, Currently throttled, Undervoltage has occurred, Throttling has occurred",
            description,
            "Description should include the correct active states"
        );
    }

    @Test
    void testDecodeNoThrottledState() {
        int throttledStateInt = 0x0; // 0000000000000000 (no bits set)
        var activeStates = ThrottledState.decode(throttledStateInt);

        assertAll(
            () -> assertEquals(0, activeStates.size(), "Should have 0 active states")
        );
    }

    @Test
    void testDecodeAllThrottledStates() {
        int throttledStateInt = 0xFFFFF; // All possible bits set
        var activeStates = ThrottledState.decode(throttledStateInt);

        assertEquals(8, activeStates.size(), "Should have 8 active states");
    }
}
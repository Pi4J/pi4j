package com.pi4j.io.gpio.parallel;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParallelPortConfigBuilderTest {

    @Test
    void impliesBitOrderWhenAddingBcm() {
        var config = ParallelPortConfigBuilder.newInstance()
            .bcm(3)
            .bcm(2)
            .bcm(1)
            .build();

        assertEquals(3, config.offsets().getFirst());
        assertEquals(2, config.offsets().get(1));
        assertEquals(1, config.offsets().get(2));
    }

    @Test
    void impliesBitOrderWhenAddingMultipleBcmValues() {
        var config = ParallelPortConfigBuilder.newInstance()
            .bcm(3, 2, 1)
            .build();

        assertEquals(3, config.offsets().getFirst());
        assertEquals(2, config.offsets().get(1));
        assertEquals(1, config.offsets().get(2));
    }

    @Test
    void cannotAddDuplicatesWhenAddingBcm() {
        assertThrows(IllegalArgumentException.class,
            () -> ParallelPortConfigBuilder.newInstance()
                .bcm(1)
                .bcm(2)
                .bcm(1)
                .build()
        );
    }

    @Test
    void cannotAddDuplicatesWhenAddingMultipleBcmValues() {
        assertThrows(IllegalArgumentException.class,
            () -> ParallelPortConfigBuilder.newInstance()
                .bcm(1, 2, 1)
                .build()
        );
    }
}

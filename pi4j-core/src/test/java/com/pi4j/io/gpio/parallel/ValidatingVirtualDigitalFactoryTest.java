package com.pi4j.io.gpio.parallel;

import com.pi4j.io.exception.IOAlreadyExistsException;
import com.pi4j.io.exception.IOBoundsException;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalInputBase;
import com.pi4j.io.gpio.digital.DigitalInputConfig;
import com.pi4j.io.gpio.digital.DigitalInputConfigBuilder;
import com.pi4j.io.gpio.digital.DigitalState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Being able to test validation in isolation largely eliminates the need to mock out providers which use it in
 * their entirety.
 */
class ValidatingVirtualDigitalFactoryTest {

    private final ParallelPortConfig inputPortConfig = ParallelPortConfigBuilder.newInstance()
        .bcm(0b101)
        .initialDirection(ParallelPort.Direction.INPUT)
        .build();


    @Test
    void canCreateValidInput() {
        var factory = new ValidatingVirtualDigitalFactory<>(inputPortConfig, this::createInput);

        var inputConfig = DigitalInputConfigBuilder.newInstance()
            .bcm(0)
            .build();

        var input = factory.createDigital(inputConfig);
        assertEquals("test-input", input.id());
    }

    @Test
    void failsToCreateInvalidInput() {
        var factory = new ValidatingVirtualDigitalFactory<>(inputPortConfig, this::createInput);

        var inputConfig = DigitalInputConfigBuilder.newInstance()
            .bcm(2)
            .build();

        assertThrows(IOBoundsException.class, () -> factory.createDigital(inputConfig));
    }

    @Test
    void cannotCreateConflictingInputs() {
        var factory = new ValidatingVirtualDigitalFactory<>(inputPortConfig, this::createInput);

        var inputConfig = DigitalInputConfigBuilder.newInstance()
            .bcm(0)
            .build();

        var input = factory.createDigital(inputConfig);
        assertNotNull(input);
        assertEquals("test-input", input.id());

        assertThrows(IOAlreadyExistsException.class, () -> factory.createDigital(inputConfig));
    }


    private DigitalInput createInput(DigitalInputConfig config) {
        return new DigitalInputBase(null, config) {
            @Override
            public String id() {
                return "test-input";
            }

            @Override
            public DigitalState state() {
                return DigitalState.HIGH;
            }
        };
    }
}

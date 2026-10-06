package com.pi4j.plugin.ffm.providers.parallel;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class FFMParallelPortTest {

    /**
     * Control test case
     */
    @Test
    void mapsWhenBitsAreInOrder() {
        var pinAllocation = List.of(0, 1, 2);
        FFMParallelPort.BitmaskMappingLogic operation = new FFMParallelPort.BitmaskMappingLogic(pinAllocation);

        assertFalse(operation.isRequired());

        for (int i = 0; i < 7; i++) {
            assertEquals(i, operation.map(i));
            assertEquals(i, operation.unmap(i));
        }
    }

    /**
     * Dynamic tests for all {@link Expectation}s
     */
    @TestFactory
    Stream<DynamicTest> mapsWhenBitsAreOutOfOrder() {
        return EXPECTATIONS.stream()
            .flatMap(expectation -> {
                FFMParallelPort.BitmaskMappingLogic operation = new FFMParallelPort.BitmaskMappingLogic(expectation.userPinAllocations);

                assertEquals(expectation.required, operation.isRequired());

                return IntStream.range(0, 8)
                    .mapToObj(inputValue -> {
                        var expectedValue = expectation.expectedValues.get(inputValue);
                        var testName = String.format("User input: %s (%d) -> Hardware value: %s (%d)",
                            Integer.toBinaryString(inputValue),
                            inputValue,
                            Integer.toBinaryString(expectedValue),
                            expectedValue
                        );

                        return DynamicTest.dynamicTest(testName, () -> {
                            int mappedValue = operation.map(inputValue);
                            assertEquals(expectation.expectedValues.get(inputValue), mappedValue);

                            int unmappedValue = operation.unmap(mappedValue);
                            assertEquals(inputValue, unmappedValue);
                        });
                    });
            });
    }

    static final List<Expectation> EXPECTATIONS = List.of(
        // User specified values in hardware-compatible order
        new Expectation(List.of(0, 1, 2),   List.of(0, 1, 2, 3, 4, 5, 6, 7), false),
        new Expectation(List.of(5, 10, 20), List.of(0, 1, 2, 3, 4, 5, 6, 7), false),
        // User specified values with bits 0 and 1 out of order
        new Expectation(List.of(1, 0, 2),   List.of(0, 2, 1, 3, 4, 6, 5, 7), true),
        new Expectation(List.of(18, 5, 22), List.of(0, 2, 1, 3, 4, 6, 5, 7), true),
        // User specified values with bit 2 out of order
        new Expectation(List.of(2, 0, 1),   List.of(0, 2, 4, 6, 1, 3, 5, 7), true),
        new Expectation(List.of(22, 5, 18), List.of(0, 2, 4, 6, 1, 3, 5, 7), true),
        // User specified values with bits in reverse order
        new Expectation(List.of(2, 1, 0),   List.of(0, 4, 2, 6, 1, 5, 3, 7), true),
        new Expectation(List.of(22, 18, 5), List.of(0, 4, 2, 6, 1, 5, 3, 7), true)
    );

    /**
     * Expected function outputs for specific input values
     * @param userPinAllocations pin allocations as might be provided by the user
     * @param expectedValues the value of the bit mask required by hardware
     * @param required whether the remapping process is necessary
     */
    record Expectation(
        List<Integer> userPinAllocations,
        List<Integer> expectedValues,
        boolean required
    ) {}
}
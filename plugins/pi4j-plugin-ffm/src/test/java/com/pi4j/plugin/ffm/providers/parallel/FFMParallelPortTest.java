package com.pi4j.plugin.ffm.providers.parallel;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class FFMParallelPortTest {

    @Test
    void mapsWhenBitsAreInOrder() {
        var pinAllocation = List.of(0, 1, 2);
        FFMParallelPort.RemapLogic operation = new FFMParallelPort.RemapLogic(pinAllocation);

        assertFalse(operation.isRequired());

        for (int i = 0; i < 7; i++) {
            assertEquals(i, operation.map(i));
            assertEquals(i, operation.unmap(i));
        }
    }

    @TestFactory
    Stream<DynamicTest> mapsWhenBitsAreOutOfOrder() {
        return EXPECTATIONS.stream()
            .map(expectation -> DynamicTest.dynamicTest(expectation.pinAllocations.toString(), () -> {
                FFMParallelPort.RemapLogic operation = new FFMParallelPort.RemapLogic(expectation.pinAllocations);

                for (int i = 0; i < 8; i++) {
                    assertEquals(expectation.required, operation.isRequired());

                    int mappedValue = operation.map(i);
                    assertEquals(expectation.expectedValues.get(i), mappedValue);

                    int unmappedValue = operation.unmap(mappedValue);
                    assertEquals(i, unmappedValue);
                }
            }));
    }

    static final List<Expectation> EXPECTATIONS = List.of(
        new Expectation(List.of(0, 1, 2),   List.of(0, 1, 2, 3, 4, 5, 6, 7), false),
        new Expectation(List.of(5, 10, 20), List.of(0, 1, 2, 3, 4, 5, 6, 7), false),
        new Expectation(List.of(1, 0, 2),   List.of(0, 2, 1, 3, 4, 6, 5, 7), true),
        new Expectation(List.of(18, 5, 22), List.of(0, 2, 1, 3, 4, 6, 5, 7), true),
        new Expectation(List.of(2, 0, 1),   List.of(0, 2, 4, 6, 1, 3, 5, 7), true),
        new Expectation(List.of(22, 5, 18), List.of(0, 2, 4, 6, 1, 3, 5, 7), true)
    );

    record Expectation(
        List<Integer> pinAllocations,
        List<Integer> expectedValues,
        boolean required
    ) {}
}
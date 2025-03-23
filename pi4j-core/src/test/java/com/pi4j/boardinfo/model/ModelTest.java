package com.pi4j.boardinfo.model;

import com.pi4j.boardinfo.definition.BoardModel;
import com.pi4j.boardinfo.util.BoardInfoHelper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ModelTest {

    @Test
    void testStringOutputFromOperatingSystem() {
        var os = new OperatingSystem("aaa", "bbb", "ccc");
        assertEquals("Name: aaa, version: bbb, architecture: ccc", os.toString());
    }

    @Test
    void testStringOutputFromJavaInfo() {
        var java = new JavaInfo("aaa", "bbb", "ccc", "ddd");
        assertEquals("Version: aaa, runtime: bbb, vendor: ccc, vendor version: ddd", java.toString());
    }

    @Test
    void testBoardReadingParsing() {
        var boardReading = new BoardReading(
            "Raspberry Pi 4 Model B Rev 1.1",
            "c03111",
            "temp=42.8'C",
            "08:06:15 up 85 days, 9:43, 0 users, load average: 0.00, 0.00, 0.00",
            "volt=0.8563V",
            "MemTotal: 3885396 kB",
            "throttled=0x3"
        );

        assertAll(
            () -> assertEquals(42.8, boardReading.getTemperatureInCelsius(), "Temperature in Celsius"),
            () -> assertEquals(109.04, boardReading.getTemperatureInFahrenheit(), 0.01, "Temperature in Fahrenheit"),
            () -> assertEquals(0.8563, boardReading.getVoltValue(), "Volt"),
            () -> assertEquals(3, boardReading.getThrottledStateAsInt(), "Throttled state as integer"), // Hex 0x3 to int
            () -> assertEquals(List.of(ThrottledState.UNDERVOLTAGE_DETECTED, ThrottledState.ARM_FREQUENCY_CAPPED),
                boardReading.getThrottledStates(),
                "Throttled states as list of active ThrottledState enums"),
            () -> assertEquals(
                "Undervoltage detected, ARM frequency capped",
                boardReading.getThrottledStatesDescription(),
                "Throttled states description for active states"
            )
        );
    }

    @Test
    void testMemoryParsing() {
        var memory = new JvmMemory(Runtime.getRuntime());

        assertAll(
            () -> assertEquals(memory.getFree() / 1024.0 / 1024.0, memory.getFreeInMb(), "Free memory in MB"),
            () -> assertEquals(memory.getMax() / 1024.0 / 1024.0, memory.getMaxInMb(), "Max memory in MB"),
            () -> assertEquals(memory.getUsed() / 1024.0 / 1024.0, memory.getUsedInMb(), "Used memory in MB"),
            () -> assertEquals(memory.getTotal() / 1024.0 / 1024.0, memory.getTotalInMb(), "Total memory in MB")
        );
    }

    @Test
    void testRP1() {
        assertAll(
            () -> assertFalse(BoardModel.MODEL_1_B.usesRP1(), "MODEL_1_B"),
            () -> assertTrue(BoardModel.MODEL_5_B.usesRP1(), "MODEL_5_B"),
            () -> assertTrue(BoardModel.MODEL_500.usesRP1(), "MODEL_500"),
            () -> assertTrue(BoardModel.COMPUTE_5.usesRP1(), "COMPUTE_5"),
            () -> assertFalse(BoardModel.GENERIC.usesRP1(), "GENERIC"),
            () -> assertTrue(BoardModel.GENERIC_RP1.usesRP1(), "GENERIC_RP1")
        );
    }

    @Test
    void testBoardModelOverwrite() {
        var boardInfo = BoardInfoHelper.current();

        boardInfo.setBoardModel(BoardModel.MODEL_500);
        assertEquals(BoardModel.MODEL_500, boardInfo.getBoardModel());
        assertTrue(BoardInfoHelper.runningOnRaspberryPi());
        assertTrue(BoardInfoHelper.usesRP1());

        boardInfo.setBoardModel(BoardModel.GENERIC);
        assertEquals(BoardModel.GENERIC, boardInfo.getBoardModel());
        assertTrue(BoardInfoHelper.runningOnRaspberryPi());
        assertFalse(BoardInfoHelper.usesRP1());

        boardInfo.setBoardModel(BoardModel.GENERIC_RP1);
        assertEquals(BoardModel.GENERIC_RP1, boardInfo.getBoardModel());
        assertTrue(BoardInfoHelper.runningOnRaspberryPi());
        assertTrue(BoardInfoHelper.usesRP1());
    }
}

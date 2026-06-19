package com.pi4j.boardinfo.util.command;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.pi4j.boardinfo.util.command.CommandExecutor.execute;
import static com.pi4j.boardinfo.util.command.CommandExecutor.splitCommand;
import static org.junit.jupiter.api.Assertions.*;

class CommandExecutorTest {

    @Test
    void shouldSplitSingleToken() {
        assertArrayEquals(new String[]{"uptime"}, splitCommand("uptime"));
    }

    @Test
    void shouldSplitExecutableAndArguments() {
        assertArrayEquals(new String[]{"vcgencmd", "measure_temp"}, splitCommand("vcgencmd measure_temp"));
    }

    @Test
    void shouldCollapseMultipleSpaces() {
        assertArrayEquals(new String[]{"vcgencmd", "measure_temp"}, splitCommand("vcgencmd   measure_temp"));
    }

    @Test
    void shouldSplitOnTabsAndMixedWhitespace() {
        assertArrayEquals(new String[]{"a", "b", "c"}, splitCommand("a\tb \t c"));
    }

    @Test
    void shouldIgnoreLeadingAndTrailingWhitespace() {
        assertArrayEquals(new String[]{"vcgencmd", "measure_volts"}, splitCommand("   vcgencmd measure_volts   "));
    }

    @ParameterizedTest
    @MethodSource("blankCommands")
    void shouldReturnFailureForBlankCommand(String command) {
        var result = execute(command);
        assertFalse(result.isSuccess());
        assertTrue(result.getErrorMessage().contains("No command provided"));
    }

    private static Stream<String> blankCommands() {
        return Stream.of(null, "", "   \t  ");
    }
}

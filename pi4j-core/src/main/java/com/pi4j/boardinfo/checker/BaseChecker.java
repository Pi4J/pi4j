package com.pi4j.boardinfo.checker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.pi4j.boardinfo.util.command.CommandExecutor.execute;

public class BaseChecker {
    private static final Logger logger = LoggerFactory.getLogger(BaseChecker.class);

    static CheckerResult.Check detectWithCommand(String command, String expectedOutput) {
        try {
            var output = execute(command);
            if (output.isSuccess() && !output.getOutputMessage().trim().isEmpty()) {
                return new CheckerResult.Check("Info returned by '" + command + "'",
                    expectedOutput, output.getOutputMessage());
            }
        } catch (Exception e) {
            logger.error("Error detecting SPI devices with command '{}': {}", command, e.getMessage());
        }
        return new CheckerResult.Check("No info returned by '" + command + "'", expectedOutput, "");
    }
}

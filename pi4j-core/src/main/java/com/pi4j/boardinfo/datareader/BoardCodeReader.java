package com.pi4j.boardinfo.datareader;

import com.pi4j.boardinfo.util.command.CommandResult;
import com.pi4j.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import static com.pi4j.boardinfo.util.command.CommandResult.failure;
import static com.pi4j.boardinfo.util.command.CommandResult.success;

/**
 * Reads the board model string from the local file system, by default from the
 * {@code /proc/device-tree/model} entry exposed by the Linux device tree on Raspberry Pi systems.
 * Used during board detection to identify the running hardware, returning the outcome as a
 * {@link CommandResult}.
 */
public class BoardCodeReader {

    private static final Logger logger = LoggerFactory.getLogger(BoardCodeReader.class);
    private static String modelFilePath = "/proc/device-tree/model";

    /**
     * Overrides the file path read by {@link #getBoardCode()}, primarily so tests can point the
     * reader at a fixture file instead of the real {@code /proc/device-tree/model} entry.
     *
     * @param path the absolute path of the model file to read from
     */
    public static void setModelFilePath(String path) {
        modelFilePath = path;
    }

    /**
     * Reads the configured model file and returns its trimmed contents as the board model string.
     *
     * @return a {@link CommandResult} whose {@link CommandResult#isSuccess()} is {@code true} and
     * whose {@link CommandResult#getOutputMessage()} holds the trimmed model string when the
     * file was read; on an I/O error a failure result is returned with the cause in
     * {@link CommandResult#getErrorMessage()}
     */
    public static CommandResult getBoardCode() {
        String outputMessage = StringUtil.EMPTY;
        String errorMessage = StringUtil.EMPTY;

        try (BufferedReader reader = new BufferedReader(new FileReader(modelFilePath))) {
            StringBuilder content = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                content.append(line);
            }

            outputMessage = content.toString().trim();
        } catch (IOException ex) {
            errorMessage = "IOException: " + ex.getMessage();
            logger.error("Failed to read the board model from '{}': {}", modelFilePath, errorMessage);
        }

        if (!errorMessage.isEmpty()) {
            return failure(errorMessage);
        }

        return success(outputMessage);
    }
}

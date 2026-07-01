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
 * Reads CPU information from the local file system, by default from the {@code /proc/cpuinfo} entry
 * on Linux systems, in order to extract the {@code Revision} field. On a Raspberry Pi this revision
 * code identifies the exact board model, so the value feeds into board detection. The outcome is
 * returned as a {@link CommandResult}.
 */
public class CpuInfoReader {

    private static final Logger logger = LoggerFactory.getLogger(CpuInfoReader.class);
    private static String cpuInfoFilePath = "/proc/cpuinfo";

    /**
     * Overrides the file path read by {@link #getCpuRevision()}, primarily so tests can point the
     * reader at a fixture file instead of the real {@code /proc/cpuinfo} entry.
     *
     * @param path the absolute path of the CPU info file to read from
     */
    public static void setCpuInfoFilePath(String path) {
        cpuInfoFilePath = path;
    }

    /**
     * Scans the configured CPU info file for the first line beginning with {@code Revision} and
     * returns the trimmed value following the colon.
     *
     * @return a {@link CommandResult} whose {@link CommandResult#isSuccess()} is {@code true} and
     *         whose {@link CommandResult#getOutputMessage()} holds the revision value when found;
     *         a failure result (with the reason in {@link CommandResult#getErrorMessage()}) is
     *         returned when the file cannot be read or contains no {@code Revision} entry
     */
    public static CommandResult getCpuRevision() {
        String outputMessage = StringUtil.EMPTY;
        String errorMessage = StringUtil.EMPTY;

        try (BufferedReader reader = new BufferedReader(new FileReader(cpuInfoFilePath))) {
            String line;
            // Read file line by line to locate the "Revision" entry.
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("Revision")) {
                    continue; // Skip lines that do not start with "Revision"
                }
                String[] parts = line.split(":");
                if (parts.length > 1) {
                    outputMessage = parts[1].trim(); // Extract and trim the revision value.
                }
                break; // No need to process further once "Revision" is found.
            }
        } catch (IOException ex) {
            errorMessage = "IOException: " + ex.getMessage();
            logger.error("Failed to read the CPU revision from '{}': {}", cpuInfoFilePath, errorMessage);
        }

        if (!errorMessage.isEmpty() || outputMessage.isEmpty()) {
            return failure(errorMessage.isEmpty() ? "CPU revision not found in file" : errorMessage);
        }

        return success(outputMessage);
    }
}

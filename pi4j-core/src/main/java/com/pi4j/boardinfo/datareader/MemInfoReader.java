package com.pi4j.boardinfo.datareader;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  MemInfoReader.java
 *
 * This file is part of the Pi4J project. More information about
 * this project can be found here:  https://pi4j.com/
 * **********************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
 * Reads memory information from the local file system, by default from the {@code /proc/meminfo}
 * entry on Linux systems, in order to extract the {@code MemTotal} line reporting the total amount
 * of installed RAM. The outcome is returned as a {@link CommandResult}.
 */
public class MemInfoReader {

    private static final Logger logger = LoggerFactory.getLogger(MemInfoReader.class);
    private static String memInfoFilePath = "/proc/meminfo";

    /**
     * Overrides the file path read by {@link #getMemTotal()}, primarily so tests can point the
     * reader at a fixture file instead of the real {@code /proc/meminfo} entry.
     *
     * @param path the absolute path of the memory info file to read from
     */
    public static void setMemInfoFilePath(String path) {
        memInfoFilePath = path;
    }

    /**
     * Scans the configured memory info file for the first line beginning with {@code MemTotal:} and
     * returns that whole trimmed line (key, value, and {@code kB} unit).
     *
     * @return a {@link CommandResult} whose {@link CommandResult#isSuccess()} is {@code true} and
     *         whose {@link CommandResult#getOutputMessage()} holds the trimmed {@code MemTotal} line
     *         when found; a failure result (with the reason in {@link CommandResult#getErrorMessage()})
     *         is returned when the file cannot be read or contains no {@code MemTotal} entry
     */
    public static CommandResult getMemTotal() {
        String errorMessage = StringUtil.EMPTY;
        String memTotalLine = StringUtil.EMPTY;

        try (BufferedReader reader = new BufferedReader(new FileReader(memInfoFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("MemTotal:")) {
                    continue; // Skip lines that don't start with "MemTotal:"
                }
                memTotalLine = line.trim();
                break; // No need to process further once "MemTotal:" is found
            }
        } catch (IOException ex) {
            errorMessage = "IOException: " + ex.getMessage();
            logger.error("Failed to read memory information from '{}': {}", memInfoFilePath, errorMessage);
        }

        if (!errorMessage.isEmpty()) {
            return failure(errorMessage);
        }
        if (memTotalLine.isEmpty()) {
            return failure("MemTotal entry not found in memory information file.");
        }

        return success(memTotalLine);
    }
}
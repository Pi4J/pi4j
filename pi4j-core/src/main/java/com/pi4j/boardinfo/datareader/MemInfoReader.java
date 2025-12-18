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
 * This class reads memory information from the file system, specifically
 * the `/proc/meminfo` file on Linux systems, to extract the total memory.
 */
public class MemInfoReader {

    private static final Logger logger = LoggerFactory.getLogger(MemInfoReader.class);
    private static String memInfoFilePath = "/proc/meminfo";

    /**
     * Sets the memory info file path for testing purposes.
     *
     * @param path The file path to be used.
     */
    public static void setMemInfoFilePath(String path) {
        memInfoFilePath = path;
    }

    /**
     * Reads the memory information file and extracts the "MemTotal" entry.
     *
     * @return A {@link CommandResult} containing:
     *         - {@code success}: true if the "MemTotal" entry is found and valid.
     *         - {@code outputMessage}: the value of the "MemTotal" entry (trimmed).
     *         - {@code errorMessage}: an error message if the entry is not found or if reading fails.
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
package com.pi4j.boardinfo.datareader;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  CpuInfoReader.java
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
 * This class reads CPU information from the file system, specifically
 * the `/proc/cpuinfo` file on Linux systems, to extract the CPU revision.
 */
public class CpuInfoReader {

    private static final Logger logger = LoggerFactory.getLogger(CpuInfoReader.class);
    private static String cpuInfoFilePath = "/proc/cpuinfo";

    /**
     * Sets the CPU info file path for testing purposes.
     *
     * @param path The file path to be used.
     */
    public static void setCpuInfoFilePath(String path) {
        cpuInfoFilePath = path;
    }

    /**
     * Reads the CPU revision from the `/proc/cpuinfo` file.
     *
     * @return A {@link CommandResult} containing:
     *         - {@code success}: true if the revision was successfully extracted, false otherwise.
     *         - {@code outputMessage}: the CPU revision value.
     *         - {@code errorMessage}: any error message encountered during the process.
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

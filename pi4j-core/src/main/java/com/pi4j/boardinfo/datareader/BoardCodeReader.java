package com.pi4j.boardinfo.datareader;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  BoardCodeReader.java
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
 * This class reads the board model code from the file system, specifically
 * from the `/proc/device-tree/model` file on Raspberry Pi systems.
 */
public class BoardCodeReader {

    private static final Logger logger = LoggerFactory.getLogger(BoardCodeReader.class);
    private static String modelFilePath = "/proc/device-tree/model";

    /**
     * Sets the file path for testing purposes.
     *
     * @param path The file path to be used.
     */
    public static void setModelFilePath(String path) {
        modelFilePath = path;
    }

    /**
     * Reads the board model code from the file system.
     *
     * @return A {@link CommandResult} containing:
     *         - {@code success}: true if the file was read successfully, false otherwise.
     *         - {@code outputMessage}: the content of the model file (trimmed).
     *         - {@code errorMessage}: any error message encountered during the process.
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

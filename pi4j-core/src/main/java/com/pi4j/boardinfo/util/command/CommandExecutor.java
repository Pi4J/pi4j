package com.pi4j.boardinfo.util.command;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  CommandExecutor.java
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import static com.pi4j.boardinfo.util.command.CommandResult.failure;
import static com.pi4j.boardinfo.util.command.CommandResult.success;

/**
 * A utility class for executing system commands and capturing their output.
 *
 * <p>Please be careful when using this method. Since we decided to remove the
 * 'sh -c' part, this method no longer supports shell-specific features such as pipes,
 * redirection, or complex shell commands. It is intended to execute simple commands directly.</p>
 */
public class CommandExecutor {
    private static final Logger logger = LoggerFactory.getLogger(CommandExecutor.class);
    private static final int COMMAND_TIMEOUT_SECONDS = 30;

    /**
     * Executes a given command and captures its output and error streams.
     *
     * <p>Please be careful when using this method. Since we decided to remove the
     * 'sh -c' part, this method no longer supports pipes, redirection, or other
     * shell-specific features. Only simple, direct commands should be executed.</p>
     *
     * @param command The command to execute (must be simple, direct command without shell features).
     * @return A {@link CommandResult} containing:
     *         - {@code success}: true if the command executed successfully within the timeout.
     *         - {@code outputMessage}: the standard output from the command.
     *         - {@code errorMessage}: the error output or any exception message.
     */
    public static CommandResult execute(String command) {
        boolean finished = false;
        String outputMessage = "";
        String errorMessage = "";

        // Configure the process builder with the command (no shell involved).
        ProcessBuilder builder = new ProcessBuilder(command.split(" "));

        try {
            Process process = builder.start();

            outputMessage = readStream(process.getInputStream());
            errorMessage = readStream(process.getErrorStream());

            finished = process.waitFor(COMMAND_TIMEOUT_SECONDS, TimeUnit.SECONDS);

            if (!finished) {
                process.destroyForcibly();
                errorMessage = "Process timeout after " + COMMAND_TIMEOUT_SECONDS + " seconds.";
            }

        } catch (IOException ex) {
            errorMessage = "IOException while executing command: " + ex.getMessage();
            logger.error("IOException during command execution '{}': {}", command, ex.getMessage(), ex);
        } catch (InterruptedException ex) {
            errorMessage = "InterruptedException during command execution: " + ex.getMessage();
            Thread.currentThread().interrupt(); // Restore the interrupted status.
            logger.error("InterruptedException during command execution '{}': {}", command, ex.getMessage(), ex);
        }

        if (!finished || !errorMessage.isEmpty()) {
            logger.error("Failed to execute command '{}': {}", command, errorMessage);
            return failure(errorMessage);
        }

        return success(outputMessage);
    }

    /**
     * Reads the content of an InputStream and returns it as a string.
     *
     * @param inputStream The InputStream to read.
     * @return The content of the InputStream as a string.
     */
    private static String readStream(InputStream inputStream) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line).append(System.lineSeparator());
            }
        } catch (IOException ex) {
            content.append("ERROR: ").append(ex.getMessage());
            logger.error("Error reading stream: {}", ex.getMessage(), ex);
        }
        return content.toString().trim(); // Trim trailing line separator.
    }
}

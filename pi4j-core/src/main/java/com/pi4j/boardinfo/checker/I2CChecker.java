package com.pi4j.boardinfo.checker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.pi4j.boardinfo.util.command.CommandExecutor.execute;

public class I2CChecker {

    private static final Logger logger = LoggerFactory.getLogger(I2CChecker.class);


    private I2CChecker() {
        // Hide constructor
    }

    public static String detectI2C() {
        StringBuilder result = new StringBuilder();

        // Check for I2C device files in specific locations
        detectFilesInDirectory(result, Paths.get("/dev"));
        detectFilesInDirectory(result, Paths.get("/sys/class/i2c-adapter"));
        detectFilesInDirectory(result, Paths.get("/sys/bus/i2c/devices"));

        // Executed commands which could return related info
        detectWithCommand(result, "lsmod | grep i2c");
        detectWithCommand(result, "which i2cdetect");

        return result.toString().trim();
    }

    private static StringBuilder detectFilesInDirectory(StringBuilder result, Path path) {
        try {
            if (Files.exists(path)) {
                try (var stream = Files.walk(path, 1)) {
                    var i2cDevices = stream
                        .filter(sub -> !path.equals(sub)) // exclude the root directory
                        .filter(sub -> sub.getFileName().toString().startsWith("i2c-"))
                        .sorted((a, b) -> {
                            // Sort by bus number
                            String nameA = a.getFileName().toString().substring(4);
                            String nameB = b.getFileName().toString().substring(4);
                            try {
                                int busA = Integer.parseInt(nameA);
                                int busB = Integer.parseInt(nameB);
                                return Integer.compare(busA, busB);
                            } catch (NumberFormatException e) {
                                return nameA.compareTo(nameB);
                            }
                        }).toList();
                    if (!i2cDevices.isEmpty()) {
                        result.append("I2C Hardware detected in ").append(path).append(":\n");
                        for (Path device : i2cDevices) {
                            String busNumber = device.getFileName().toString().substring(4);
                            result.append(busNumber).append(" ");
                        }
                        result.append("\n");
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error detecting I2C devices in path '{}': {}", path, e.getMessage());
        }

        return result;
    }

    private static StringBuilder detectWithCommand(StringBuilder result, String command) {
        try {
            var output = execute(command);
            if (output.isSuccess() && !output.getOutputMessage().trim().isEmpty()) {
                result.append("Info returned by '").append(command).append("':\n");
                result.append(output.getOutputMessage()).append("\n");
            }
        } catch (Exception e) {
            logger.error("Error detecting I2C devices with command '{}': {}", command, e.getMessage());
        }
        return result;
    }
}

package com.pi4j.boardinfo.checker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static com.pi4j.boardinfo.util.command.CommandExecutor.execute;

public class I2CChecker {

    private static final Logger logger = LoggerFactory.getLogger(I2CChecker.class);


    private I2CChecker() {
        // Hide constructor
    }

    public static CheckerResult detect() {
        return new CheckerResult("I2C Detection", List.of(
            // Check for I2C device files in specific locations
            detectFilesInDirectory(Paths.get("/dev")),
            detectFilesInDirectory(Paths.get("/sys/class/i2c-adapter")),
            detectFilesInDirectory(Paths.get("/sys/bus/i2c/devices")),

            // Executed commands which could return related info
            detectWithCommand("lsmod | grep i2c"),
            detectWithCommand("which i2cdetect")
        ));
    }

    private static CheckerResult.Check detectFilesInDirectory(Path path) {
        var result = new StringBuilder();

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

        if (result.isEmpty()) {
            return new CheckerResult.Check("No info found in '" + path + "'", "");
        } else {
            return new CheckerResult.Check("Hardware detected in " + path, result.toString());
        }
    }

    private static CheckerResult.Check detectWithCommand(String command) {
        try {
            var output = execute(command);
            if (output.isSuccess() && !output.getOutputMessage().trim().isEmpty()) {


                return new CheckerResult.Check("Info returned by '" + command + "'",
                    output.getOutputMessage());
            }
        } catch (Exception e) {
            logger.error("Error detecting I2C devices with command '{}': {}", command, e.getMessage());
        }
        return new CheckerResult.Check("No info returned by '" + command + "'", "");
    }
}

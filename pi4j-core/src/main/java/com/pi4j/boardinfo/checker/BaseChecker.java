package com.pi4j.boardinfo.checker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.pi4j.boardinfo.util.command.CommandExecutor.execute;

public class BaseChecker {
    private static final Logger logger = LoggerFactory.getLogger(BaseChecker.class);

    static CheckerResult.Check detectWithCommand(String command, String expectedOutput) {
        try {
            var output = execute(command);
            if (output.isSuccess() && !output.getOutputMessage().trim().isEmpty()) {
                return new CheckerResult.Check(CheckerResult.ResultStatus.TO_EVALUATE, "Info returned by '" + command + "'",
                    expectedOutput, output.getOutputMessage());
            }
        } catch (Exception e) {
            logger.error("Error detecting SPI devices with command '{}': {}", command, e.getMessage());
        }
        return new CheckerResult.Check(CheckerResult.ResultStatus.TO_EVALUATE, "No info returned by '" + command + "'", expectedOutput, "");
    }

    static CheckerResult.Check detectConfigSetting(String setting, String interfaceName, String expectedOutput) {
        var result = new StringBuilder();
        String[] configPaths = {"/boot/config.txt", "/boot/firmware/config.txt"};
        boolean foundAny = false;

        for (String configPath : configPaths) {
            try {
                Path path = Paths.get(configPath);
                if (Files.exists(path)) {
                    List<String> lines = Files.readAllLines(path);

                    for (String line : lines) {
                        if (line.contains(setting)) {
                            result.append("Found in ").append(configPath).append(": ").append(line.trim()).append("\n");
                            foundAny = true;
                        }
                    }
                }
            } catch (Exception e) {
                logger.debug("Could not read config file {}: {}", configPath, e.getMessage());
            }
        }

        if (!foundAny) {
            return new CheckerResult.Check(CheckerResult.ResultStatus.FAIL,
                "Configuration for " + interfaceName + " not found",
                expectedOutput, result.toString());
        }

        return new CheckerResult.Check(CheckerResult.ResultStatus.PASS,
            "Configuration for " + interfaceName + " found",
            expectedOutput, result.toString());
    }

    static CheckerResult.Check detectInterfaceFromDeviceTree(String interfaceType, String description) {
        var result = new StringBuilder();
        String expectedOutput = "Found " + interfaceType + " device-tree entries with status=okay";
        List<String> foundDevices = new ArrayList<>();

        try {
            var socPath = findSocPath();
            if (socPath.isPresent()) {
                try (var stream = Files.walk(socPath.get(), 2)) {
                    var interfacePaths = stream
                        .filter(Files::isDirectory)
                        .filter(path -> {
                            String name = path.getFileName().toString();
                            return name.contains(interfaceType);
                        })
                        .sorted()
                        .toList();

                    for (Path interfacePath : interfacePaths) {
                        String dirName = interfacePath.getFileName().toString();

                        // Check status
                        Path statusPath = interfacePath.resolve("status");
                        String status = "unknown";
                        if (Files.exists(statusPath)) {
                            try {
                                status = Files.readString(statusPath).trim().replace("\0", "");
                            } catch (Exception e) {
                                logger.debug("Could not read status for {}: {}", interfacePath, e.getMessage());
                            }
                        }

                        // Only include devices with okay status
                        if ("okay".equals(status)) {
                            foundDevices.add(dirName);
                            result.append("✓ ").append(dirName).append(" (status: ").append(status).append(")\n");
                        } else {
                            result.append("✗ ").append(dirName).append(" (status: ").append(status).append(")\n");
                        }
                    }
                }
            } else {
                result.append("Device-tree path /proc/device-tree/soc not available\n");
            }
        } catch (Exception e) {
            logger.debug("Error reading device-tree {} info: {}", interfaceType, e.getMessage());
            result.append("Error reading device-tree info: ").append(e.getMessage()).append("\n");
        }

        if (foundDevices.isEmpty()) {
            return new CheckerResult.Check(CheckerResult.ResultStatus.FAIL,
                "No active " + interfaceType.toUpperCase() + " devices found",
                expectedOutput, result.toString());
        }

        return new CheckerResult.Check(CheckerResult.ResultStatus.PASS,
            foundDevices.size() + " active " + interfaceType.toUpperCase() + " device(s) found",
            expectedOutput, result.toString());
    }

    static Optional<Path> findSocPath() {
        // Look for any directory in /proc/device-tree that starts with "soc"
        Path dtBasePath = Paths.get("/proc/device-tree");

        try {
            if (Files.exists(dtBasePath)) {
                try (var stream = Files.list(dtBasePath)) {
                    return stream
                        .filter(Files::isDirectory)
                        .filter(path -> path.getFileName().toString().startsWith("soc"))
                        .findFirst();
                }
            }
        } catch (IOException e) {
            logger.error("Error reading device-tree info: {}", e.getMessage());
        }

        return Optional.empty();
    }
}

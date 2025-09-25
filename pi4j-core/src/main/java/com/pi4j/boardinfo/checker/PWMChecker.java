package com.pi4j.boardinfo.checker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static com.pi4j.boardinfo.util.command.CommandExecutor.execute;

public class PWMChecker {

    private static final Logger logger = LoggerFactory.getLogger(PWMChecker.class);

    private PWMChecker() {
        // Hide constructor
    }

    public static CheckerResult detect() {
        return new CheckerResult("PWM Detection", List.of(
            // Check for PWM device files in specific locations
            detectFilesInDirectory(Paths.get("/sys/class/pwm")),
            detectFilesInDirectory(Paths.get("/sys/class/pwm-bcm2835")),
            detectFilesInDirectory(Paths.get("/sys/devices/platform/soc/3f20c000.pwm")),
            detectFilesInDirectory(Paths.get("/sys/devices/platform/soc/fe20c000.pwm")), // Pi 4 path

            // Check for PWM chip files
            detectPwmChips(),

            // Executed commands which could return related info
            detectWithCommand("lsmod | grep pwm"),
            detectWithCommand("find /sys -name 'pwm*' -type d 2>/dev/null | head -10"),
            detectWithCommand("cat /boot/config.txt | grep dtoverlay=pwm")
        ));
    }

    private static CheckerResult.Check detectFilesInDirectory(Path path) {
        var result = new StringBuilder();

        try {
            if (Files.exists(path)) {
                try (var stream = Files.walk(path, 1)) {
                    var pwmDevices = stream
                        .filter(sub -> !path.equals(sub)) // exclude the root directory
                        .filter(sub -> {
                            String name = sub.getFileName().toString();
                            return name.startsWith("pwm") || name.startsWith("pwmchip");
                        })
                        .sorted((a, b) -> {
                            // Sort by chip/device number
                            String nameA = a.getFileName().toString();
                            String nameB = b.getFileName().toString();
                            return nameA.compareTo(nameB);
                        }).toList();

                    if (!pwmDevices.isEmpty()) {
                        for (Path device : pwmDevices) {
                            String deviceName = device.getFileName().toString();
                            result.append(deviceName).append(" ");
                        }
                        result.append("\n");
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error detecting PWM devices in path '{}': {}", path, e.getMessage());
        }

        if (result.isEmpty()) {
            return new CheckerResult.Check("No info found in '" + path + "'", "");
        } else {
            return new CheckerResult.Check("Hardware detected in " + path, result.toString());
        }
    }

    private static CheckerResult.Check detectPwmChips() {
        var result = new StringBuilder();

        try {
            Path pwmPath = Paths.get("/sys/class/pwm");
            if (Files.exists(pwmPath)) {
                try (var stream = Files.walk(pwmPath, 2)) {
                    var pwmChips = stream
                        .filter(sub -> sub.getFileName().toString().startsWith("pwmchip"))
                        .sorted((a, b) -> {
                            String nameA = a.getFileName().toString().substring(7); // Remove "pwmchip"
                            String nameB = b.getFileName().toString().substring(7);
                            try {
                                int chipA = Integer.parseInt(nameA);
                                int chipB = Integer.parseInt(nameB);
                                return Integer.compare(chipA, chipB);
                            } catch (NumberFormatException e) {
                                return nameA.compareTo(nameB);
                            }
                        }).toList();

                    for (Path chip : pwmChips) {
                        String chipNumber = chip.getFileName().toString().substring(7);
                        result.append("chip").append(chipNumber);

                        // Check for npwm file to get number of PWM channels
                        Path npwmFile = chip.resolve("npwm");
                        if (Files.exists(npwmFile)) {
                            try {
                                String channels = Files.readString(npwmFile).trim();
                                result.append("(").append(channels).append(" channels)");
                            } catch (Exception e) {
                                logger.debug("Could not read npwm for {}: {}", chip, e.getMessage());
                            }
                        }
                        result.append(" ");
                    }
                    if (!pwmChips.isEmpty()) {
                        result.append("\n");
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error detecting PWM chips: {}", e.getMessage());
        }

        if (result.isEmpty()) {
            return new CheckerResult.Check("No PWM chips found", "");
        } else {
            return new CheckerResult.Check("PWM chips detected", result.toString());
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
            logger.error("Error detecting PWM devices with command '{}': {}", command, e.getMessage());
        }
        return new CheckerResult.Check("No info returned by '" + command + "'", "");
    }
}
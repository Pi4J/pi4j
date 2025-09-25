package com.pi4j.boardinfo.checker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static com.pi4j.boardinfo.util.command.CommandExecutor.execute;

public class GPIOChecker {

    private static final Logger logger = LoggerFactory.getLogger(GPIOChecker.class);

    private GPIOChecker() {
        // Hide constructor
    }

    public static CheckerResult detect() {
        return new CheckerResult("GPIO Detection", List.of(
            // Check for GPIO device files and sysfs entries
            detectFilesInDirectory(Paths.get("/dev")),
            detectFilesInDirectory(Paths.get("/sys/class/gpio")),
            detectGpioChips(),
            detectExportedPins(),

            // Check GPIO-specific paths
            detectGpioDrivers(),

            // Executed commands which could return related info
            detectWithCommand("lsmod | grep gpio"),
            detectWithCommand("lsmod | grep gpiod"),
            detectWithCommand("which gpiodetect"),
            detectWithCommand("which gpioinfo"),
            detectWithCommand("gpiodetect 2>/dev/null || echo 'gpiodetect not available'"),
            detectWithCommand("cat /proc/device-tree/soc/gpio*/status 2>/dev/null || echo 'no device-tree gpio info'")
        ));
    }

    private static CheckerResult.Check detectFilesInDirectory(Path path) {
        var result = new StringBuilder();

        try {
            if (Files.exists(path)) {
                try (var stream = Files.walk(path, 1)) {
                    var gpioDevices = stream
                        .filter(sub -> !path.equals(sub)) // exclude the root directory
                        .filter(sub -> {
                            String name = sub.getFileName().toString();
                            return name.startsWith("gpiochip") ||
                                name.startsWith("gpio") ||
                                name.equals("gpiomem");
                        })
                        .sorted((a, b) -> {
                            String nameA = a.getFileName().toString();
                            String nameB = b.getFileName().toString();

                            // Sort gpiochip devices numerically
                            if (nameA.startsWith("gpiochip") && nameB.startsWith("gpiochip")) {
                                return extractNumber(nameA, "gpiochip").compareTo(extractNumber(nameB, "gpiochip"));
                            } else if (nameA.startsWith("gpio") && nameB.startsWith("gpio") &&
                                !nameA.equals("gpiomem") && !nameB.equals("gpiomem")) {
                                return extractNumber(nameA, "gpio").compareTo(extractNumber(nameB, "gpio"));
                            }
                            return nameA.compareTo(nameB);
                        }).toList();

                    if (!gpioDevices.isEmpty()) {
                        for (Path device : gpioDevices) {
                            String deviceName = device.getFileName().toString();
                            result.append(deviceName);

                            // For gpiochip devices, try to get additional info
                            if (deviceName.startsWith("gpiochip")) {
                                try {
                                    String chipNum = deviceName.substring(8);
                                    Path labelPath = Paths.get("/sys/class/gpio/gpiochip" + chipNum + "/label");
                                    Path ngpioPath = Paths.get("/sys/class/gpio/gpiochip" + chipNum + "/ngpio");

                                    if (Files.exists(labelPath) && Files.exists(ngpioPath)) {
                                        String label = Files.readString(labelPath).trim();
                                        String ngpio = Files.readString(ngpioPath).trim();
                                        result.append(" (").append(label).append(", ").append(ngpio).append(" pins)");
                                    }
                                } catch (Exception e) {
                                    logger.debug("Could not read additional info for {}: {}", deviceName, e.getMessage());
                                }
                            }
                            result.append(" ");
                        }
                        result.append("\n");
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error detecting GPIO devices in path '{}': {}", path, e.getMessage());
        }

        if (result.isEmpty()) {
            return new CheckerResult.Check("No info found in '" + path + "'", "");
        } else {
            return new CheckerResult.Check("Hardware detected in " + path, result.toString());
        }
    }

    private static CheckerResult.Check detectGpioChips() {
        var result = new StringBuilder();

        try {
            Path gpioPath = Paths.get("/sys/class/gpio");
            if (Files.exists(gpioPath)) {
                try (var stream = Files.walk(gpioPath, 1)) {
                    var gpioChips = stream
                        .filter(sub -> sub.getFileName().toString().startsWith("gpiochip"))
                        .sorted((a, b) -> {
                            String nameA = a.getFileName().toString().substring(8);
                            String nameB = b.getFileName().toString().substring(8);
                            try {
                                int chipA = Integer.parseInt(nameA);
                                int chipB = Integer.parseInt(nameB);
                                return Integer.compare(chipA, chipB);
                            } catch (NumberFormatException e) {
                                return nameA.compareTo(nameB);
                            }
                        }).toList();

                    for (Path chip : gpioChips) {
                        String chipNumber = chip.getFileName().toString().substring(8);
                        result.append("chip").append(chipNumber);

                        // Get chip details
                        try {
                            Path basePath = chip.resolve("base");
                            Path labelPath = chip.resolve("label");
                            Path ngpioPath = chip.resolve("ngpio");

                            if (Files.exists(basePath) && Files.exists(labelPath) && Files.exists(ngpioPath)) {
                                String base = Files.readString(basePath).trim();
                                String label = Files.readString(labelPath).trim();
                                String ngpio = Files.readString(ngpioPath).trim();
                                result.append(" (base=").append(base)
                                    .append(", label=").append(label)
                                    .append(", pins=").append(ngpio).append(")");
                            }
                        } catch (Exception e) {
                            logger.debug("Could not read chip details for {}: {}", chip, e.getMessage());
                        }
                        result.append(" ");
                    }
                    if (!gpioChips.isEmpty()) {
                        result.append("\n");
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error detecting GPIO chips: {}", e.getMessage());
        }

        if (result.isEmpty()) {
            return new CheckerResult.Check("No GPIO chips found", "");
        } else {
            return new CheckerResult.Check("GPIO chips detected", result.toString());
        }
    }

    private static CheckerResult.Check detectExportedPins() {
        var result = new StringBuilder();

        try {
            Path gpioPath = Paths.get("/sys/class/gpio");
            if (Files.exists(gpioPath)) {
                try (var stream = Files.walk(gpioPath, 1)) {
                    var exportedPins = stream
                        .filter(sub -> {
                            String name = sub.getFileName().toString();
                            return name.startsWith("gpio") && !name.startsWith("gpiochip") && !name.equals("gpio");
                        })
                        .sorted((a, b) -> {
                            String nameA = a.getFileName().toString().substring(4);
                            String nameB = b.getFileName().toString().substring(4);
                            try {
                                int pinA = Integer.parseInt(nameA);
                                int pinB = Integer.parseInt(nameB);
                                return Integer.compare(pinA, pinB);
                            } catch (NumberFormatException e) {
                                return nameA.compareTo(nameB);
                            }
                        }).toList();

                    if (!exportedPins.isEmpty()) {
                        result.append("Exported pins: ");
                        for (Path pin : exportedPins) {
                            String pinNumber = pin.getFileName().toString().substring(4);
                            result.append(pinNumber);

                            // Try to get pin direction
                            try {
                                Path directionPath = pin.resolve("direction");
                                if (Files.exists(directionPath)) {
                                    String direction = Files.readString(directionPath).trim();
                                    result.append("(").append(direction).append(")");
                                }
                            } catch (Exception e) {
                                logger.debug("Could not read direction for pin {}: {}", pinNumber, e.getMessage());
                            }
                            result.append(" ");
                        }
                        result.append("\n");
                    } else {
                        result.append("No pins currently exported\n");
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error detecting exported GPIO pins: {}", e.getMessage());
        }

        if (result.isEmpty()) {
            return new CheckerResult.Check("No exported pin info available", "");
        } else {
            return new CheckerResult.Check("GPIO pin export status", result.toString());
        }
    }

    private static CheckerResult.Check detectGpioDrivers() {
        var result = new StringBuilder();

        try {
            // Check for GPIO-related platform drivers
            Path[] gpioPaths = {
                Paths.get("/sys/devices/platform/soc/3f200000.gpio"), // Pi 2/3
                Paths.get("/sys/devices/platform/soc/fe200000.gpio"), // Pi 4
                Paths.get("/sys/bus/platform/drivers/gpio-bcm2835"),
                Paths.get("/sys/bus/platform/drivers/pinctrl-bcm2835")
            };

            for (Path gpioPath : gpioPaths) {
                if (Files.exists(gpioPath)) {
                    String pathName = gpioPath.getFileName().toString();
                    result.append(pathName).append(" ");
                }
            }

            if (!result.isEmpty()) {
                result.append("\n");
            }
        } catch (Exception e) {
            logger.error("Error detecting GPIO drivers: {}", e.getMessage());
        }

        if (result.isEmpty()) {
            return new CheckerResult.Check("No GPIO driver paths found", "");
        } else {
            return new CheckerResult.Check("GPIO drivers detected", result.toString());
        }
    }

    private static Integer extractNumber(String name, String prefix) {
        try {
            return Integer.parseInt(name.substring(prefix.length()));
        } catch (NumberFormatException e) {
            return Integer.MAX_VALUE; // Put non-numeric entries at the end
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
            logger.error("Error detecting GPIO devices with command '{}': {}", command, e.getMessage());
        }
        return new CheckerResult.Check("No info returned by '" + command + "'", "");
    }
}
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
            detectLoadedGpioModules(),
            detectGpioTools(),
            detectGpioDevicesWithTools(),
            detectDeviceTreeGpioInfo()
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


    private static CheckerResult.Check detectLoadedGpioModules() {
        var result = new StringBuilder();

        try {
            Path modulesPath = Paths.get("/proc/modules");
            if (Files.exists(modulesPath)) {
                List<String> lines = Files.readAllLines(modulesPath);
                for (String line : lines) {
                    String moduleName = line.split("\\s+")[0]; // First column is module name
                    String lowerName = moduleName.toLowerCase();
                    if (lowerName.contains("gpio") || lowerName.contains("gpiod")) {
                        result.append(line).append("\n");
                    }
                }
            }
        } catch (Exception e) {
            logger.debug("Error reading loaded modules for GPIO detection: {}", e.getMessage());
        }

        if (result.isEmpty()) {
            return new CheckerResult.Check("No GPIO modules loaded", "");
        } else {
            return new CheckerResult.Check("GPIO modules loaded", result.toString());
        }
    }

    private static CheckerResult.Check detectGpioTools() {
        var result = new StringBuilder();

        // Common paths where gpio tools might be installed
        String[] commonPaths = {"/usr/bin", "/usr/local/bin", "/bin", "/sbin", "/usr/sbin"};
        String[] toolNames = {"gpiodetect", "gpioinfo", "gpioset", "gpioget"};

        for (String toolName : toolNames) {
            boolean found = false;
            for (String path : commonPaths) {
                Path toolPath = Paths.get(path, toolName);
                if (Files.exists(toolPath) && Files.isExecutable(toolPath)) {
                    result.append(toolName).append(" found at ").append(toolPath).append("\n");
                    found = true;
                    break;
                }
            }
            if (!found) {
                result.append(toolName).append(" not found\n");
            }
        }

        if (result.isEmpty()) {
            return new CheckerResult.Check("No GPIO tool availability info", "");
        } else {
            return new CheckerResult.Check("GPIO tool availability", result.toString());
        }
    }

    private static CheckerResult.Check detectGpioDevicesWithTools() {
        var result = new StringBuilder();

        try {
            // Try to find gpiodetect tool first
            String gpiodetectPath = null;
            String[] commonPaths = {"/usr/bin/gpiodetect", "/usr/local/bin/gpiodetect", "/bin/gpiodetect"};

            for (String path : commonPaths) {
                if (Files.exists(Paths.get(path)) && Files.isExecutable(Paths.get(path))) {
                    gpiodetectPath = path;
                    break;
                }
            }

            if (gpiodetectPath != null) {
                // Run gpiodetect without shell redirections
                var output = execute(gpiodetectPath);
                if (output.isSuccess() && !output.getOutputMessage().trim().isEmpty()) {
                    result.append(output.getOutputMessage());
                } else {
                    result.append("gpiodetect available but returned no output\n");
                }
            } else {
                result.append("gpiodetect not available\n");
            }
        } catch (Exception e) {
            logger.debug("Error detecting GPIO devices with gpiodetect: {}", e.getMessage());
            result.append("Error running gpiodetect: ").append(e.getMessage()).append("\n");
        }

        return new CheckerResult.Check("GPIO device detection with tools", result.toString());
    }

    private static CheckerResult.Check detectDeviceTreeGpioInfo() {
        var result = new StringBuilder();

        try {
            // Look for GPIO device-tree information
            Path dtBasePath = Paths.get("/proc/device-tree/soc");
            if (Files.exists(dtBasePath)) {
                try (var stream = Files.walk(dtBasePath, 2)) {
                    var gpioPaths = stream
                        .filter(Files::isDirectory)
                        .filter(path -> {
                            String name = path.getFileName().toString();
                            return name.contains("gpio");
                        })
                        .sorted()
                        .toList();

                    if (!gpioPaths.isEmpty()) {
                        for (Path gpioPath : gpioPaths) {
                            String dirName = gpioPath.getFileName().toString();
                            result.append("Found GPIO device-tree entry: ").append(dirName);

                            // Try to read status
                            Path statusPath = gpioPath.resolve("status");
                            if (Files.exists(statusPath)) {
                                try {
                                    String status = Files.readString(statusPath).trim();
                                    // Remove null bytes that might be present in device-tree files
                                    status = status.replace("\0", "");
                                    if (!status.isEmpty()) {
                                        result.append(" (status: ").append(status).append(")");
                                    }
                                } catch (Exception e) {
                                    logger.debug("Could not read status for {}: {}", gpioPath, e.getMessage());
                                }
                            }
                            result.append("\n");
                        }
                    } else {
                        result.append("No GPIO device-tree entries found in /proc/device-tree/soc\n");
                    }
                }
            } else {
                result.append("Device-tree path /proc/device-tree/soc not available\n");
            }
        } catch (Exception e) {
            logger.debug("Error reading device-tree GPIO info: {}", e.getMessage());
            result.append("Error reading device-tree info: ").append(e.getMessage()).append("\n");
        }

        return new CheckerResult.Check("Device-tree GPIO information", result.toString());
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
}
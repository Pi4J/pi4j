package com.pi4j.boardinfo.checker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static com.pi4j.boardinfo.util.command.CommandExecutor.execute;

public class SPIChecker {

    private static final Logger logger = LoggerFactory.getLogger(SPIChecker.class);

    private SPIChecker() {
        // Hide constructor
    }

    public static CheckerResult detect() {
        return new CheckerResult("SPI Detection", List.of(
            // Check for SPI device files in specific locations
            detectFilesInDirectory(Paths.get("/dev")),
            detectFilesInDirectory(Paths.get("/sys/class/spidev")),
            detectFilesInDirectory(Paths.get("/sys/bus/spi/devices")),
            detectFilesInDirectory(Paths.get("/sys/devices/platform/soc/3f204000.spi")), // Pi 2/3 main SPI
            detectFilesInDirectory(Paths.get("/sys/devices/platform/soc/fe204000.spi")), // Pi 4 main SPI
            detectFilesInDirectory(Paths.get("/sys/devices/platform/soc/3f215080.spi")), // Pi 2/3 aux SPI
            detectFilesInDirectory(Paths.get("/sys/devices/platform/soc/fe215080.spi")), // Pi 4 aux SPI

            // Executed commands which could return related info
            detectWithCommand("lsmod | grep spi"),
            detectWithCommand("which spi-config"),
            detectWithCommand("cat /boot/config.txt | grep dtparam=spi"),
            detectSpiPins()
        ));
    }

    private static CheckerResult.Check detectFilesInDirectory(Path path) {
        var result = new StringBuilder();

        try {
            if (Files.exists(path)) {
                try (var stream = Files.walk(path, 1)) {
                    var spiDevices = stream
                        .filter(sub -> !path.equals(sub)) // exclude the root directory
                        .filter(sub -> {
                            String name = sub.getFileName().toString();
                            return name.startsWith("spidev") || name.startsWith("spi-");
                        })
                        .sorted((a, b) -> {
                            String nameA = a.getFileName().toString();
                            String nameB = b.getFileName().toString();

                            // Extract bus and device numbers for proper sorting
                            if (nameA.startsWith("spidev") && nameB.startsWith("spidev")) {
                                try {
                                    String[] partsA = nameA.substring(6).split("\\.");
                                    String[] partsB = nameB.substring(6).split("\\.");
                                    if (partsA.length == 2 && partsB.length == 2) {
                                        int busA = Integer.parseInt(partsA[0]);
                                        int busB = Integer.parseInt(partsB[0]);
                                        if (busA != busB) {
                                            return Integer.compare(busA, busB);
                                        }
                                        int devA = Integer.parseInt(partsA[1]);
                                        int devB = Integer.parseInt(partsB[1]);
                                        return Integer.compare(devA, devB);
                                    }
                                } catch (NumberFormatException e) {
                                    // Fall back to string comparison
                                }
                            }
                            return nameA.compareTo(nameB);
                        }).toList();

                    if (!spiDevices.isEmpty()) {
                        for (Path device : spiDevices) {
                            String deviceName = device.getFileName().toString();
                            if (deviceName.startsWith("spidev")) {
                                // Extract bus.device format
                                String busDevice = deviceName.substring(6);
                                result.append(busDevice).append(" ");
                            } else {
                                result.append(deviceName).append(" ");
                            }
                        }
                        result.append("\n");
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error detecting SPI devices in path '{}': {}", path, e.getMessage());
        }

        if (result.isEmpty()) {
            return new CheckerResult.Check("No info found in '" + path + "'", "");
        } else {
            return new CheckerResult.Check("Hardware detected in " + path, result.toString());
        }
    }

    private static CheckerResult.Check detectSpiPins() {
        var result = new StringBuilder();

        try {
            // Check if SPI pins are configured by looking at GPIO export states
            Path gpioPath = Paths.get("/sys/class/gpio");
            if (Files.exists(gpioPath)) {
                // Main SPI pins: MISO(9), MOSI(10), SCLK(11), CE0(8), CE1(7)
                // Aux SPI pins: MISO(19), MOSI(20), SCLK(21), CE0(18), CE1(17), CE2(16)
                int[] mainSpiPins = {7, 8, 9, 10, 11};
                int[] auxSpiPins = {16, 17, 18, 19, 20, 21};

                result.append("Main SPI pins (7,8,9,10,11): ");
                checkPins(mainSpiPins, result);
                result.append("\nAux SPI pins (16,17,18,19,20,21): ");
                checkPins(auxSpiPins, result);
            }
        } catch (Exception e) {
            logger.error("Error detecting SPI pin configuration: {}", e.getMessage());
        }

        if (result.isEmpty()) {
            return new CheckerResult.Check("No SPI pin info available", "");
        } else {
            return new CheckerResult.Check("SPI pin status", result.toString());
        }
    }

    private static void checkPins(int[] pins, StringBuilder result) {
        int configuredCount = 0;
        for (int pin : pins) {
            Path pinPath = Paths.get("/sys/class/gpio/gpio" + pin);
            if (Files.exists(pinPath)) {
                configuredCount++;
            }
        }
        if (configuredCount > 0) {
            result.append(configuredCount).append(" configured");
        } else {
            result.append("not configured");
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
            logger.error("Error detecting SPI devices with command '{}': {}", command, e.getMessage());
        }
        return new CheckerResult.Check("No info returned by '" + command + "'", "");
    }
}
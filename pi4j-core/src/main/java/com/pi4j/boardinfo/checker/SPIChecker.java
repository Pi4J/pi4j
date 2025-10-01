package com.pi4j.boardinfo.checker;

import com.pi4j.boardinfo.definition.Generation;
import com.pi4j.boardinfo.util.BoardInfoHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SPIChecker extends BaseChecker {

    private static final Logger logger = LoggerFactory.getLogger(SPIChecker.class);

    private SPIChecker() {
        // Hide constructor
    }

    public static CheckerResult detect() {
        var checks = new ArrayList<CheckerResult.Check>();
        checks.add(detectConfigSetting("dtparam=spi", "SPI", "dtparam=spi=on"));
        checks.add(detectInterfaceFromDeviceTree("spi", "SPI bus controller"));

        // Check for SPI device files in specific locations
        checks.add(detectFilesInDirectory(Paths.get("/dev"), "spidev0.0 spidev0.1 (main SPI bus devices when dtparam=spi=on)"));
        checks.add(detectFilesInDirectory(Paths.get("/sys/class/spidev"), "spidev0.0 spidev0.1"));
        checks.add(detectFilesInDirectory(Paths.get("/sys/bus/spi/devices"), "spi0.0 spi0.1 (SPI device entries)"));

        // Executed commands which could return related info
        checks.add(detectWithCommand("which spi-config", "spi-config utility not commonly available by default"));

        // Extra checks
        checks.add(detectLoadedSpiModules());
        checks.add(detectSpiConfigSettings());
        checks.add(detectSpiPins());

        var boardGeneration = BoardInfoHelper.current().getBoardModel().getGeneration();
        if (boardGeneration == Generation.GENERATION_2 || boardGeneration == Generation.GENERATION_3) {
            checks.add(detectFilesInDirectory(Paths.get("/sys/devices/platform/soc/3f204000.spi"), "SPI hardware platform device directory for RPi 2 and 3 main SPI"));
            checks.add(detectFilesInDirectory(Paths.get("/sys/devices/platform/soc/3f215080.spi"), "SPI hardware platform device directory for RPi 2 and 3 aux SPI"));
        } else if (boardGeneration == Generation.GENERATION_4) {
            checks.add(detectFilesInDirectory(Paths.get("/sys/devices/platform/soc/fe204000.spi"), "SPI hardware platform device directory for RPi 4 main SPI"));
            checks.add(detectFilesInDirectory(Paths.get("/sys/devices/platform/soc/fe215080.spi"), "SPI hardware platform device directory for RPi 4 aux SPI"));
        }

        return new CheckerResult("SPI Detection", checks);
    }

    private static CheckerResult.Check detectFilesInDirectory(Path path, String expectedOutput) {
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
            return new CheckerResult.Check(CheckerResult.ResultStatus.FAIL,
                "No info found in '" + path + "'", expectedOutput, "");
        } else {
            return new CheckerResult.Check(CheckerResult.ResultStatus.PASS,
                "Hardware detected in " + path, expectedOutput, result.toString());
        }
    }

    private static CheckerResult.Check detectSpiPins() {
        var result = new StringBuilder();
        String expectedOutput = "Main SPI pins (7,8,9,10,11): not configured, Aux SPI pins (16,17,18,19,20,21): not configured (normal state when SPI not actively used)";

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
            return new CheckerResult.Check(CheckerResult.ResultStatus.FAIL,
                "No SPI pin info available", expectedOutput, "");
        } else {
            return new CheckerResult.Check(CheckerResult.ResultStatus.PASS,
                "SPI pin status", expectedOutput, result.toString());
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

    private static CheckerResult.Check detectLoadedSpiModules() {
        var result = new StringBuilder();
        String expectedOutput = "spi_bcm2835 or spidev (SPI kernel driver modules when SPI is enabled)";

        try {
            Path modulesPath = Paths.get("/proc/modules");
            if (Files.exists(modulesPath)) {
                List<String> lines = Files.readAllLines(modulesPath);
                for (String line : lines) {
                    String moduleName = line.split("\\s+")[0]; // First column is module name
                    if (moduleName.toLowerCase().contains("spi")) {
                        result.append(line).append("\n");
                    }
                }
            }
        } catch (Exception e) {
            logger.debug("Error reading loaded modules for SPI detection: {}", e.getMessage());
        }

        if (result.isEmpty()) {
            return new CheckerResult.Check(CheckerResult.ResultStatus.FAIL,
                "No SPI modules loaded", expectedOutput, "");
        } else {
            return new CheckerResult.Check(CheckerResult.ResultStatus.PASS,
                "SPI modules loaded", expectedOutput, result.toString());
        }
    }

    private static CheckerResult.Check detectSpiConfigSettings() {
        var result = new StringBuilder();
        String expectedOutput = "dtparam=spi=on (in /boot/config.txt or /boot/firmware/config.txt)";

        String[] configPaths = {"/boot/config.txt", "/boot/firmware/config.txt"};

        for (String configPath : configPaths) {
            try {
                Path path = Paths.get(configPath);
                if (Files.exists(path)) {
                    List<String> lines = Files.readAllLines(path);
                    boolean foundSpiConfig = false;

                    for (String line : lines) {
                        if (line.contains("dtparam=spi")) {
                            result.append(configPath).append(": ").append(line).append("\n");
                            foundSpiConfig = true;
                        }
                    }

                    if (!foundSpiConfig) {
                        result.append("No dtparam=spi setting found in ").append(configPath).append("\n");
                    }
                }
            } catch (Exception e) {
                logger.debug("Could not read config file {}: {}", configPath, e.getMessage());
            }
        }

        if (result.isEmpty()) {
            return new CheckerResult.Check(CheckerResult.ResultStatus.FAIL,
                "No SPI config files accessible", expectedOutput, "");
        } else {
            return new CheckerResult.Check(CheckerResult.ResultStatus.PASS,
                "SPI configuration settings", expectedOutput, result.toString());
        }
    }
}
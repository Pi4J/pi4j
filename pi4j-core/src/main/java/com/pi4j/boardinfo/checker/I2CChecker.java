package com.pi4j.boardinfo.checker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class I2CChecker extends BaseChecker {

    private static final Logger logger = LoggerFactory.getLogger(I2CChecker.class);


    private I2CChecker() {
        // Hide constructor
    }

    public static CheckerResult detect() {
        return new CheckerResult("I2C Detection", List.of(
            detectConfigSetting("dtparam=i2c", "I2C", "dtparam=i2c_arm=on"),
            detectInterfaceFromDeviceTree("i2c", "I2C bus controller"),

            // Check for I2C device files in specific locations
            detectFilesInDirectory(Paths.get("/dev"), "i2c-1 (and possibly i2c-0 if camera/display interface enabled)"),
            detectFilesInDirectory(Paths.get("/sys/class/i2c-adapter"), "i2c-1 (and possibly i2c-0)"),
            detectFilesInDirectory(Paths.get("/sys/bus/i2c/devices"), "1-0048 1-0049 (or similar I2C device addresses if devices connected)"),

            // "lsmod | grep i2c"
            detectLoadedI2cModules(),

            // Executed commands which could return related info
            detectWithCommand("which i2cdetect", "/usr/bin/i2cdetect or /usr/sbin/i2cdetect (path to i2c-tools utility)")
        ));
    }

    private static CheckerResult.Check detectFilesInDirectory(Path path, String expectedOutput) {
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
            return new CheckerResult.Check(CheckerResult.ResultStatus.FAIL,
                "No info found in '" + path + "'", expectedOutput, "");
        } else {
            return new CheckerResult.Check(CheckerResult.ResultStatus.PASS,
                "Hardware detected in " + path, expectedOutput, result.toString());
        }
    }

    private static CheckerResult.Check detectLoadedI2cModules() {
        var result = new StringBuilder();
        String expectedOutput = "i2c_bcm2835 or i2c_bcm2708 (I2C kernel driver module)";

        try {
            Path modulesPath = Paths.get("/proc/modules");
            if (Files.exists(modulesPath)) {
                List<String> lines = Files.readAllLines(modulesPath);
                for (String line : lines) {
                    String moduleName = line.split("\\s+")[0]; // First column is module name
                    if (moduleName.toLowerCase().contains("i2c")) {
                        result.append(line).append("\n");
                    }
                }
            }
        } catch (Exception e) {
            logger.debug("Error reading loaded modules for I2C detection: {}", e.getMessage());
        }

        if (result.isEmpty()) {
            return new CheckerResult.Check(CheckerResult.ResultStatus.FAIL,
                "No I2C modules loaded", expectedOutput, "");
        } else {
            return new CheckerResult.Check(CheckerResult.ResultStatus.PASS,
                "I2C modules loaded", expectedOutput, result.toString());
        }
    }

}

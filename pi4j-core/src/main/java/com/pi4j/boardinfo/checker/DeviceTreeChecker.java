package com.pi4j.boardinfo.checker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DeviceTreeChecker {

    private static final Logger logger = LoggerFactory.getLogger(DeviceTreeChecker.class);

    private DeviceTreeChecker() {
        // Hide constructor
    }

    public static CheckerResult detect() {
        return new CheckerResult("Device Tree Hardware Detection", List.of(
            detectGpioFromDeviceTree(),
            detectI2cFromDeviceTree(),
            detectSpiFromDeviceTree(),
            detectPwmFromDeviceTree(),
            detectUartFromDeviceTree()
        ));
    }

    private static CheckerResult.Check detectGpioFromDeviceTree() {
        return detectInterfaceFromDeviceTree("gpio", "GPIO controller with pinctrl functionality");
    }

    private static CheckerResult.Check detectI2cFromDeviceTree() {
        return detectInterfaceFromDeviceTree("i2c", "I2C bus controller");
    }

    private static CheckerResult.Check detectSpiFromDeviceTree() {
        return detectInterfaceFromDeviceTree("spi", "SPI bus controller");
    }

    private static CheckerResult.Check detectPwmFromDeviceTree() {
        return detectInterfaceFromDeviceTree("pwm", "PWM controller");
    }

    private static CheckerResult.Check detectUartFromDeviceTree() {
        return detectInterfaceFromDeviceTree("uart", "UART serial controller");
    }

    private static CheckerResult.Check detectInterfaceFromDeviceTree(String interfaceType, String description) {
        var result = new StringBuilder();
        String expectedOutput = "Found " + interfaceType + " device-tree entries with status=okay";
        List<String> foundDevices = new ArrayList<>();

        try {
            Path dtBasePath = Paths.get("/proc/device-tree/soc");
            if (Files.exists(dtBasePath)) {
                try (var stream = Files.walk(dtBasePath, 2)) {
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

        String title = foundDevices.isEmpty() ?
            "No active " + interfaceType.toUpperCase() + " devices found" :
            foundDevices.size() + " active " + interfaceType.toUpperCase() + " device(s) found";

        return new CheckerResult.Check(title, expectedOutput, result.toString());
    }
}
package com.pi4j.boardinfo.checker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static com.pi4j.boardinfo.util.command.CommandExecutor.execute;

public class SerialChecker {

    private static final Logger logger = LoggerFactory.getLogger(SerialChecker.class);

    private SerialChecker() {
        // Hide constructor
    }

    public static CheckerResult detect() {
        return new CheckerResult("Serial Detection", List.of(
            // Check for serial device files in specific locations
            detectFilesInDirectory(Paths.get("/dev")),
            detectSerialBySerial(),
            detectFilesInDirectory(Paths.get("/sys/class/tty")),

            // Check UART-specific paths
            detectUartDevices(),

            // Executed commands which could return related info
            detectWithCommand("lsmod | grep uart"),
            detectWithCommand("lsmod | grep serial"),
            detectWithCommand("dmesg | grep -i uart | tail -5"),
            detectWithCommand("cat /boot/config.txt | grep enable_uart"),
            detectWithCommand("stty -F /dev/ttyS0 2>/dev/null || echo 'ttyS0 not available'"),
            detectWithCommand("stty -F /dev/ttyAMA0 2>/dev/null || echo 'ttyAMA0 not available'")
        ));
    }

    private static CheckerResult.Check detectFilesInDirectory(Path path) {
        var result = new StringBuilder();

        try {
            if (Files.exists(path)) {
                try (var stream = Files.walk(path, 1)) {
                    var serialDevices = stream
                        .filter(sub -> !path.equals(sub)) // exclude the root directory
                        .filter(sub -> {
                            String name = sub.getFileName().toString();
                            return name.startsWith("ttyS") ||
                                name.startsWith("ttyAMA") ||
                                name.startsWith("ttyUSB") ||
                                name.startsWith("ttyACM") ||
                                name.equals("serial0") ||
                                name.equals("serial1");
                        })
                        .sorted((a, b) -> {
                            String nameA = a.getFileName().toString();
                            String nameB = b.getFileName().toString();

                            // Sort by device type, then by number
                            if (nameA.startsWith("ttyS") && nameB.startsWith("ttyS")) {
                                return extractNumber(nameA, "ttyS").compareTo(extractNumber(nameB, "ttyS"));
                            } else if (nameA.startsWith("ttyAMA") && nameB.startsWith("ttyAMA")) {
                                return extractNumber(nameA, "ttyAMA").compareTo(extractNumber(nameB, "ttyAMA"));
                            } else if (nameA.startsWith("ttyUSB") && nameB.startsWith("ttyUSB")) {
                                return extractNumber(nameA, "ttyUSB").compareTo(extractNumber(nameB, "ttyUSB"));
                            }
                            return nameA.compareTo(nameB);
                        }).toList();

                    if (!serialDevices.isEmpty()) {
                        for (Path device : serialDevices) {
                            String deviceName = device.getFileName().toString();
                            result.append(deviceName);

                            // Try to get additional info about the device
                            if (deviceName.startsWith("tty")) {
                                try {
                                    Path devicePath = Paths.get("/sys/class/tty/" + deviceName + "/device");
                                    if (Files.exists(devicePath)) {
                                        result.append(" (active)");
                                    }
                                } catch (Exception e) {
                                    // Ignore errors getting device info
                                }
                            }
                            result.append(" ");
                        }
                        result.append("\n");
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error detecting serial devices in path '{}': {}", path, e.getMessage());
        }

        if (result.isEmpty()) {
            return new CheckerResult.Check("No info found in '" + path + "'", "");
        } else {
            return new CheckerResult.Check("Hardware detected in " + path, result.toString());
        }
    }

    private static CheckerResult.Check detectSerialBySerial() {
        var result = new StringBuilder();

        try {
            Path serialPath = Paths.get("/dev/serial/by-id");
            if (Files.exists(serialPath)) {
                try (var stream = Files.walk(serialPath, 1)) {
                    var devices = stream
                        .filter(sub -> !serialPath.equals(sub))
                        .sorted((a, b) -> a.getFileName().toString().compareTo(b.getFileName().toString()))
                        .toList();

                    for (Path device : devices) {
                        String deviceName = device.getFileName().toString();
                        result.append(deviceName).append(" ");

                        // Try to resolve the symlink to see the actual device
                        try {
                            Path realPath = device.toRealPath();
                            result.append("-> ").append(realPath.getFileName()).append(" ");
                        } catch (Exception e) {
                            // Ignore symlink resolution errors
                        }
                    }
                    if (!devices.isEmpty()) {
                        result.append("\n");
                    }
                }
            }

            Path serialPathPath = Paths.get("/dev/serial/by-path");
            if (Files.exists(serialPathPath)) {
                try (var stream = Files.walk(serialPathPath, 1)) {
                    var devices = stream
                        .filter(sub -> !serialPathPath.equals(sub))
                        .sorted((a, b) -> a.getFileName().toString().compareTo(b.getFileName().toString()))
                        .toList();

                    if (!devices.isEmpty() && !result.isEmpty()) {
                        result.append("by-path: ");
                    }
                    for (Path device : devices) {
                        result.append(device.getFileName().toString()).append(" ");
                    }
                    if (!devices.isEmpty()) {
                        result.append("\n");
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error detecting serial devices by-id/by-path: {}", e.getMessage());
        }

        if (result.isEmpty()) {
            return new CheckerResult.Check("No serial by-id/by-path devices found", "");
        } else {
            return new CheckerResult.Check("Serial devices by-id/by-path", result.toString());
        }
    }

    private static CheckerResult.Check detectUartDevices() {
        var result = new StringBuilder();

        try {
            // Check for UART-specific sysfs entries
            Path[] uartPaths = {
                Paths.get("/sys/devices/platform/soc/3f201000.serial"), // Pi 2/3
                Paths.get("/sys/devices/platform/soc/fe201000.serial"), // Pi 4
                Paths.get("/sys/devices/platform/soc/3f215040.serial"), // Pi 2/3 aux
                Paths.get("/sys/devices/platform/soc/fe215040.serial")  // Pi 4 aux
            };

            for (Path uartPath : uartPaths) {
                if (Files.exists(uartPath)) {
                    String pathName = uartPath.getFileName().toString();
                    result.append(pathName).append(" ");
                }
            }

            if (!result.isEmpty()) {
                result.append("\n");
            }
        } catch (Exception e) {
            logger.error("Error detecting UART devices: {}", e.getMessage());
        }

        if (result.isEmpty()) {
            return new CheckerResult.Check("No UART hardware paths found", "");
        } else {
            return new CheckerResult.Check("UART hardware detected", result.toString());
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
            logger.error("Error detecting serial devices with command '{}': {}", command, e.getMessage());
        }
        return new CheckerResult.Check("No info returned by '" + command + "'", "");
    }
}
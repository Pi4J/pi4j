package com.pi4j.boardinfo.checker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SerialChecker extends BaseChecker {

    private static final Logger logger = LoggerFactory.getLogger(SerialChecker.class);

    private SerialChecker() {
        // Hide constructor
    }

    public static CheckerResult detect() {
        return new CheckerResult("Serial Detection", List.of(
            detectConfigSetting("enable_uart", "UART", "enable_uart=1"),
            detectInterfaceFromDeviceTree("uart", "UART serial controller"),

            // Check for serial device files in specific locations
            detectFilesInDirectory(Paths.get("/dev"), "ttyS0 serial0 (and possibly ttyAMA0 when UART is enabled)"),
            detectFilesInDirectory(Paths.get("/sys/class/tty"), "ttyS0 ttyAMA0 console (various TTY devices including serial)"),

            //
            detectSerialBySerial(),

            // Check UART-specific paths
            detectUartDevices(),

            // Executed commands which could return related info
            detectLoadedSerialModules(),
            detectDmesgUartInfo(),
            detectUartConfigSettings(),
            detectSerialPortAvailability()
        ));
    }

    private static CheckerResult.Check detectFilesInDirectory(Path path, String expectedOutput) {
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
            return new CheckerResult.Check(CheckerResult.ResultStatus.FAIL,
                "No info found in '" + path + "'", expectedOutput, "");
        } else {
            return new CheckerResult.Check(CheckerResult.ResultStatus.PASS,
                "Hardware detected in " + path, expectedOutput, result.toString());
        }
    }

    private static CheckerResult.Check detectSerialBySerial() {
        var result = new StringBuilder();
        String expectedOutput = "USB-to-serial devices or hardware serial interfaces (when present)";

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
            return new CheckerResult.Check(CheckerResult.ResultStatus.FAIL,
                "No serial by-id/by-path devices found", expectedOutput, "");
        } else {
            return new CheckerResult.Check(CheckerResult.ResultStatus.PASS,
                "Serial devices by-id/by-path", expectedOutput, result.toString());
        }
    }

    private static CheckerResult.Check detectUartDevices() {
        var result = new StringBuilder();
        String expectedOutput = "3f201000.serial or fe201000.serial (main UART), 3f215040.serial or fe215040.serial (aux UART)";

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
            return new CheckerResult.Check(CheckerResult.ResultStatus.FAIL,
                "No UART hardware paths found", expectedOutput, "");
        } else {
            return new CheckerResult.Check(CheckerResult.ResultStatus.PASS,
                "UART hardware detected", expectedOutput, result.toString());
        }
    }

    private static Integer extractNumber(String name, String prefix) {
        try {
            return Integer.parseInt(name.substring(prefix.length()));
        } catch (NumberFormatException e) {
            return Integer.MAX_VALUE; // Put non-numeric entries at the end
        }
    }

    private static CheckerResult.Check detectLoadedSerialModules() {
        var result = new StringBuilder();
        String expectedOutput = "8250 or amba-pl011 (UART kernel driver modules)";

        try {
            Path modulesPath = Paths.get("/proc/modules");
            if (Files.exists(modulesPath)) {
                List<String> lines = Files.readAllLines(modulesPath);
                for (String line : lines) {
                    String moduleName = line.split("\\s+")[0]; // First column is module name
                    String lowerName = moduleName.toLowerCase();
                    if (lowerName.contains("uart") || lowerName.contains("serial")) {
                        result.append(line).append("\n");
                    }
                }
            }
        } catch (Exception e) {
            logger.debug("Error reading loaded modules for serial detection: {}", e.getMessage());
        }

        if (result.isEmpty()) {
            return new CheckerResult.Check(CheckerResult.ResultStatus.FAIL,
                "No serial/UART modules loaded", expectedOutput, "");
        } else {
            return new CheckerResult.Check(CheckerResult.ResultStatus.PASS,
                "Serial/UART modules loaded", expectedOutput, result.toString());
        }
    }

    private static CheckerResult.Check detectDmesgUartInfo() {
        var result = new StringBuilder();
        String expectedOutput = "UART initialization messages and device registration info";

        try {
            Path dmesgPath = Paths.get("/var/log/dmesg");
            Path kernelLogPath = Paths.get("/var/log/kern.log");

            // Try to read from /var/log/dmesg first, then /var/log/kern.log
            Path logPath = Files.exists(dmesgPath) ? dmesgPath :
                Files.exists(kernelLogPath) ? kernelLogPath : null;

            if (logPath != null) {
                List<String> lines = Files.readAllLines(logPath);
                List<String> uartLines = new ArrayList<>();

                // Find lines containing "uart" (case insensitive) and keep last 5
                for (String line : lines) {
                    if (line.toLowerCase().contains("uart")) {
                        uartLines.add(line);
                    }
                }

                // Get last 5 entries
                int startIndex = Math.max(0, uartLines.size() - 5);
                for (int i = startIndex; i < uartLines.size(); i++) {
                    result.append(uartLines.get(i)).append("\n");
                }
            } else {
                result.append("No accessible dmesg log files found\n");
            }
        } catch (Exception e) {
            logger.debug("Error reading dmesg for UART info: {}", e.getMessage());
            result.append("Error reading dmesg: ").append(e.getMessage()).append("\n");
        }

        if (result.isEmpty()) {
            return new CheckerResult.Check(CheckerResult.ResultStatus.FAIL,
                "No UART info in dmesg", expectedOutput, "");
        } else {
            return new CheckerResult.Check(CheckerResult.ResultStatus.PASS,
                "Recent UART messages from dmesg", expectedOutput, result.toString());
        }
    }

    private static CheckerResult.Check detectUartConfigSettings() {
        var result = new StringBuilder();
        String expectedOutput = "enable_uart=1 (in /boot/config.txt or /boot/firmware/config.txt)";

        String[] configPaths = {"/boot/config.txt", "/boot/firmware/config.txt"};

        for (String configPath : configPaths) {
            try {
                Path path = Paths.get(configPath);
                if (Files.exists(path)) {
                    List<String> lines = Files.readAllLines(path);
                    boolean foundUartConfig = false;

                    for (String line : lines) {
                        if (line.contains("enable_uart")) {
                            result.append(configPath).append(": ").append(line).append("\n");
                            foundUartConfig = true;
                        }
                    }

                    if (!foundUartConfig) {
                        result.append("No enable_uart setting found in ").append(configPath).append("\n");
                    }
                }
            } catch (Exception e) {
                logger.debug("Could not read config file {}: {}", configPath, e.getMessage());
            }
        }

        if (result.isEmpty()) {
            return new CheckerResult.Check(CheckerResult.ResultStatus.FAIL,
                "No UART config files accessible", expectedOutput, "");
        } else {
            return new CheckerResult.Check(CheckerResult.ResultStatus.PASS,
                "UART configuration settings", expectedOutput, result.toString());
        }
    }

    private static CheckerResult.Check detectSerialPortAvailability() {
        var result = new StringBuilder();
        String expectedOutput = "/dev/ttyS0 exists (readable, writable), /dev/ttyAMA0 exists (readable, writable) when UART enabled";

        String[] serialDevices = {"/dev/ttyS0", "/dev/ttyAMA0", "/dev/ttyUSB0", "/dev/ttyACM0"};

        for (String devicePath : serialDevices) {
            try {
                Path device = Paths.get(devicePath);
                if (Files.exists(device)) {
                    // Check if device is readable/writable
                    boolean readable = Files.isReadable(device);
                    boolean writable = Files.isWritable(device);

                    result.append(devicePath).append(" exists");
                    if (readable || writable) {
                        result.append(" (");
                        if (readable) result.append("readable");
                        if (readable && writable) result.append(", ");
                        if (writable) result.append("writable");
                        result.append(")");
                    } else {
                        result.append(" (no permissions)");
                    }
                    result.append("\n");
                } else {
                    result.append(devicePath).append(" not available\n");
                }
            } catch (Exception e) {
                result.append(devicePath).append(" - error checking: ").append(e.getMessage()).append("\n");
            }
        }

        return new CheckerResult.Check(CheckerResult.ResultStatus.TO_EVALUATE, "Serial port availability", expectedOutput, result.toString());
    }
}
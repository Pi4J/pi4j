package com.pi4j.boardinfo.checker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
            detectFilesInDirectory(),
            detectSerialPortAvailability()
        ));
    }

    private static CheckerResult.Check detectFilesInDirectory() {
        var result = new StringBuilder();
        var serialPath = Paths.get("/dev/serial");

        try {
            if (Files.exists(serialPath)) {
                try (var stream = Files.walk(serialPath, 1)) {
                    stream
                        .filter(sub -> !serialPath.equals(sub)) // exclude the root directory
                        .map(sub -> sub.getFileName() + " ")
                        .filter(fileName -> fileName.startsWith("ttyS") ||
                            fileName.startsWith("ttyAMA") ||
                            fileName.startsWith("ttyUSB") ||
                            fileName.startsWith("ttyACM") ||
                            fileName.equals("serial0") ||
                            fileName.equals("serial1"))
                        .sorted()
                        .forEach(result::append);
                }
            }
        } catch (Exception e) {
            logger.error("Error detecting serial devices in path '{}': {}", serialPath, e.getMessage());
        }

        var command = "ls -l /sys/class/tty";
        var expectedOutput = "ttyS0 ttyAMA0 (or similar)";
        if (result.isEmpty()) {
            return new CheckerResult.Check(CheckerResult.ResultStatus.FAIL,
                command, expectedOutput, "");
        } else {
            return new CheckerResult.Check(CheckerResult.ResultStatus.PASS,
                command, expectedOutput, result.toString());
        }
    }

    private static CheckerResult.Check detectSerialPortAvailability() {
        var result = new StringBuilder();
        String[] serialDevices = {"/dev/ttyS0", "/dev/ttyAMA0", "/dev/ttyUSB0", "/dev/ttyACM0"};
        var found = false;

        for (String devicePath : serialDevices) {
            try {
                Path device = Paths.get(devicePath);
                if (Files.exists(device)) {
                    // Check if device is readable/writable
                    boolean readable = Files.isReadable(device);
                    boolean writable = Files.isWritable(device);

                    result.append(devicePath).append(" exists");
                    if (readable || writable) {
                        found = true;
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

        var command = "Checking serial device availability";
        var expectedOutput = "/dev/ttyS0 exists (readable, writable)";

        if (!found) {
            return new CheckerResult.Check(CheckerResult.ResultStatus.FAIL,
                command, expectedOutput, result.toString());
        }

        return new CheckerResult.Check(CheckerResult.ResultStatus.PASS,
            command, expectedOutput, result.toString());
    }
}
package com.pi4j.boardinfo.checker;

import com.pi4j.util.HexFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.pi4j.boardinfo.util.command.CommandExecutor.execute;

public class I2CChecker extends BaseChecker {

    private static final Logger logger = LoggerFactory.getLogger(I2CChecker.class);


    private I2CChecker() {
        // Hide constructor
    }

    public static CheckerResult detect() {
        var devices = detectI2CDevices();

        var result = new CheckerResult("I2C Detection", new ArrayList<>(List.of(
            detectConfigSetting("dtparam=i2c", "I2C", "dtparam=i2c_arm=on"),
            detectInterfaceFromDeviceTree("i2c", "I2C bus controller"),
            detectI2CDevicesWithCommand(devices)
        )));

        if (!devices.isEmpty()) {
            devices.forEach(d -> result.addResult(detectI2CUsedAddresses(d)));
        }

        return result;
    }

    private static CheckerResult.Check detectI2CDevicesWithCommand(List<I2CDevice> devices) {
        String expectedOutput = "One or more devices, e.g. 'i2c-1' (I2C bus adapters detected by 'i2cdetect -l')";

        if (devices.isEmpty()) {
            return new CheckerResult.Check(CheckerResult.ResultStatus.FAIL,
                "i2cdetect -l",
                expectedOutput, "");
        } else {
            return new CheckerResult.Check(CheckerResult.ResultStatus.PASS,
                "i2cdetect -l",
                expectedOutput,
                devices.stream()
                    .map(I2CDevice::output)
                    .collect(Collectors.joining("\n")));
        }
    }

    private static CheckerResult.Check detectI2CUsedAddresses(I2CDevice device) {
        var result = new StringBuilder();
        String expectedOutput = "One or more I2C used addresses detected on bus " + device.getBusNumber();
        List<Byte> addresses = new ArrayList<>();

        try {
            var output = execute("i2cdetect -y " + device.getBusNumber());
            if (output.isSuccess() && !output.getOutputMessage().trim().isEmpty()) {
                addresses = parseI2CDeviceAddresses(output.getOutputMessage());

                if (!addresses.isEmpty()) {
                    result.append("Found ").append(addresses.size())
                        .append(" used addres(ses) on bus ").append(device.getBusNumber())
                        .append(": ");
                    result.append(addresses.stream()
                        .map(HexFormatter::format).sorted()
                        .collect(Collectors.joining(", ")));
                } else {
                    result.append("No used addresses found on bus ").append(device.getBusNumber()).append("\n");
                }
            }
        } catch (Exception e) {
            logger.error("Error detecting I2C addresses on bus {}: {}", device.getBusNumber(), e.getMessage());
            return new CheckerResult.Check(CheckerResult.ResultStatus.FAIL,
                "Error scanning I2C bus " + device.getBusNumber(),
                expectedOutput, e.getMessage());
        }

        if (addresses.isEmpty()) {
            return new CheckerResult.Check(CheckerResult.ResultStatus.FAIL,
                "i2cdetect -y " + device.getBusNumber(),
                expectedOutput, result.toString());
        } else {
            return new CheckerResult.Check(CheckerResult.ResultStatus.PASS,
                "i2cdetect -y " + device.getBusNumber(),
                expectedOutput, result.toString());
        }
    }

    static List<Byte> parseI2CDeviceAddresses(String output) {
        List<Byte> addresses = new ArrayList<>();

        try {
            for (String line : output.split("\n")) {
                line = line.trim();
                if (line.startsWith("00: ") || line.startsWith("10: ") ||
                    line.startsWith("20: ") || line.startsWith("30: ") || line.startsWith("40: ") ||
                    line.startsWith("50: ") || line.startsWith("60: ") || line.startsWith("70: ")) {

                    // Skip the row number at the beginning
                    String[] parts = line.substring(3).split("\\s+");

                    // Start from index 1 to skip the row number (00:, 10:, etc.)
                    for (int i = 0; i < parts.length; i++) {
                        String part = parts[i].trim();
                        // Valid addresses are two hex digits (not -- or UU)
                        if (part.matches("[0-9a-fA-F]{2}")) {
                            addresses.add((byte) Integer.parseInt(part.toLowerCase(), 16));
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error parsing I2C addresses: {}", e.getMessage());
        }

        return addresses;
    }

    private static List<I2CDevice> detectI2CDevices() {
        try {
            var output = execute("i2cdetect -l");
            if (output.isSuccess() && !output.getOutputMessage().trim().isEmpty()) {
                return parseI2CDetectOutput(output.getOutputMessage());
            }
        } catch (Exception e) {
            logger.error("Error detecting I2C devices with i2cdetect command: {}", e.getMessage());
        }

        return new ArrayList<>();
    }

    private static List<I2CDevice> parseI2CDetectOutput(String output) {
        List<I2CDevice> devices = new ArrayList<>();

        for (String line : output.split("\n")) {
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }

            // Split by whitespace (multiple spaces/tabs)
            String[] parts = line.split("\\s+");

            if (parts.length >= 4) {
                String bus = parts[0];          // e.g., "i2c-1"
                String type = parts[1];         // e.g., "i2c"
                String name = parts[2];         // e.g., "Synopsys" or "107d508200.i2c"

                // Join remaining parts as description
                String description = String.join(" ", Arrays.copyOfRange(parts, 3, parts.length));

                devices.add(new I2CDevice(bus, type, name, description));
            }
        }

        return devices;
    }

    private record I2CDevice(String bus, String type, String name, String description) {
        public int getBusNumber() {
            try {
                return Integer.parseInt(bus.replace("i2c-", ""));
            } catch (NumberFormatException e) {
                return -1; // or throw exception
            }
        }

        public String output() {
            return getBusNumber() + ": " + name + ", " + description;
        }
    }
}

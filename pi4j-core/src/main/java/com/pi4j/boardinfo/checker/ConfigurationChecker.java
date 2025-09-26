package com.pi4j.boardinfo.checker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ConfigurationChecker {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationChecker.class);

    private ConfigurationChecker() {
        // Hide constructor
    }

    public static CheckerResult detect() {
        return new CheckerResult("Configuration Settings Detection", List.of(
            detectPwmConfigSettings(),
            detectSpiConfigSettings(),
            detectI2cConfigSettings(),
            detectUartConfigSettings()
        ));
    }

    private static CheckerResult.Check detectPwmConfigSettings() {
        return detectConfigSetting("dtoverlay=pwm", "PWM", "dtoverlay=pwm (or dtoverlay=pwm-2chan for 2-channel PWM)");
    }

    private static CheckerResult.Check detectSpiConfigSettings() {
        return detectConfigSetting("dtparam=spi", "SPI", "dtparam=spi=on");
    }

    private static CheckerResult.Check detectI2cConfigSettings() {
        return detectConfigSetting("dtparam=i2c", "I2C", "dtparam=i2c_arm=on");
    }

    private static CheckerResult.Check detectUartConfigSettings() {
        return detectConfigSetting("enable_uart", "UART", "enable_uart=1");
    }

    private static CheckerResult.Check detectConfigSetting(String setting, String interfaceName, String expectedOutput) {
        var result = new StringBuilder();
        String[] configPaths = {"/boot/config.txt", "/boot/firmware/config.txt"};
        boolean foundAny = false;

        for (String configPath : configPaths) {
            try {
                Path path = Paths.get(configPath);
                if (Files.exists(path)) {
                    List<String> lines = Files.readAllLines(path);
                    boolean foundInFile = false;

                    for (String line : lines) {
                        if (line.contains(setting)) {
                            result.append(configPath).append(": ").append(line.trim()).append("\n");
                            foundInFile = true;
                            foundAny = true;
                        }
                    }

                    if (!foundInFile) {
                        result.append("No ").append(setting).append(" setting found in ").append(configPath).append("\n");
                    }
                }
            } catch (Exception e) {
                logger.debug("Could not read config file {}: {}", configPath, e.getMessage());
            }
        }

        String title = foundAny ?
            interfaceName + " configuration found" :
            "No " + interfaceName + " configuration found";

        return new CheckerResult.Check(title, expectedOutput, result.toString());
    }
}
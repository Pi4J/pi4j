package com.pi4j.boardinfo.checker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class PWMChecker extends BaseChecker {

    private static final Logger logger = LoggerFactory.getLogger(PWMChecker.class);

    private PWMChecker() {
        // Hide constructor
    }

    public static CheckerResult detect() {
        return new CheckerResult("PWM Detection", List.of(
            detectConfigSetting("dtoverlay=pwm", "PWM", "dtoverlay=pwm (or dtoverlay=pwm-2chan for 2-channel PWM)"),
            detectPwmChips(),
            detectPwmFromPinctrl()
        ));
    }

    private static CheckerResult.Check detectPwmFromPinctrl() {
        var result = new StringBuilder();

        try {
            ProcessBuilder processBuilder = new ProcessBuilder("pinctrl");
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            try (var reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("PWM")) {
                        result.append(line).append("\n");
                    }
                }
            }

            process.waitFor();
        } catch (Exception e) {
            logger.debug("Could not execute pinctrl command: {}", e.getMessage());
        }

        var command = "pinctrl | grep PWM";
        var expectedOutput = "GPIO line(s) with PWM function (e.g., GPIO18 = PWM0_CHAN2)";

        if (result.isEmpty()) {
            return new CheckerResult.Check(CheckerResult.ResultStatus.FAIL,
                command, expectedOutput, "");
        } else {
            return new CheckerResult.Check(CheckerResult.ResultStatus.PASS,
                command, expectedOutput, result.toString());
        }
    }

    private static CheckerResult.Check detectPwmChips() {
        var result = new StringBuilder();

        try {
            Path pwmPath = Paths.get("/sys/class/pwm");
            if (Files.exists(pwmPath)) {
                try (var stream = Files.walk(pwmPath, 2)) {
                    stream
                        .map(Path::getFileName)
                        .filter(fileName -> fileName.toString().startsWith("pwmchip"))
                        .sorted()
                        .forEach(result::append);
                }
            }
        } catch (Exception e) {
            logger.error("Error detecting PWM chips: {}", e.getMessage());
        }

        var command = "ls -l /sys/class/pwm/";
        var expectedOutput = "pwmchipX (X = number, when dtoverlay=pwm is properly configured)";

        if (result.isEmpty()) {
            return new CheckerResult.Check(CheckerResult.ResultStatus.FAIL,
                command, expectedOutput, "");
        } else {
            return new CheckerResult.Check(CheckerResult.ResultStatus.PASS,
                command, expectedOutput, result.toString());
        }
    }
}
package com.pi4j.boardinfo.checker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.pi4j.boardinfo.util.command.CommandExecutor.execute;

public class GPIOChecker extends BaseChecker {

    private static final Logger logger = LoggerFactory.getLogger(GPIOChecker.class);

    private GPIOChecker() {
        // Hide constructor
    }

    public static CheckerResult detect() {
        return new CheckerResult("GPIO Detection", List.of(
            detectGpioDevicesWithTools()
        ));
    }

    private static CheckerResult.Check detectGpioDevicesWithTools() {
        var found = new ArrayList<String>();

        try {
            Path[] commonPaths = {
                Path.of("/usr/bin/gpiodetect"),
                Path.of("/usr/local/bin/gpiodetect"),
                Path.of("/bin/gpiodetect")
            };

            for (Path path : commonPaths) {
                if (Files.exists(path) && Files.isExecutable(path)) {
                    var output = execute(path.toString());
                    if (output.isSuccess() && !output.getOutputMessage().trim().isEmpty()) {
                        found.add(output.getOutputMessage());
                    }
                }
            }
        } catch (Exception e) {
            logger.debug("Error detecting GPIO devices with gpiodetect: {}", e.getMessage());
        }

        var command = "gpiodetect";
        var expectedOutput = "gpiochip0 [pinctrl-bcm2835] (54 lines) or similar";

        if (found.isEmpty()) {
            return new CheckerResult.Check(CheckerResult.ResultStatus.FAIL,
                command, expectedOutput, "");
        } else {
            return new CheckerResult.Check(CheckerResult.ResultStatus.PASS,
                command, expectedOutput,
                found.stream()
                    .map(String::trim)
                    .distinct()
                    .sorted()
                    .collect(Collectors.joining("\n"))
            );
        }
    }
}
package com.pi4j.boardinfo.checker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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
            // Try to find gpiodetect tool first
            String gpiodetectPath = null;
            String[] commonPaths = {"/usr/bin/gpiodetect", "/usr/local/bin/gpiodetect", "/bin/gpiodetect"};

            for (String path : commonPaths) {
                if (Files.exists(Paths.get(path)) && Files.isExecutable(Paths.get(path))) {
                    gpiodetectPath = path;
                    break;
                }
            }

            if (gpiodetectPath != null) {
                // Run gpiodetect without shell redirections
                var output = execute(gpiodetectPath);
                if (output.isSuccess() && !output.getOutputMessage().trim().isEmpty()) {
                    found.add("In " + gpiodetectPath + ": " + output.getOutputMessage());
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
                command, expectedOutput, String.join("\n", found));
        }
    }
}
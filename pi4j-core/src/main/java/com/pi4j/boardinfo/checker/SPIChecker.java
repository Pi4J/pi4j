package com.pi4j.boardinfo.checker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class SPIChecker extends BaseChecker {

    private static final Logger logger = LoggerFactory.getLogger(SPIChecker.class);

    private SPIChecker() {
        // Hide constructor
    }

    public static CheckerResult detect() {
        return new CheckerResult("SPI Detection", List.of(
            detectConfigSetting("dtparam=spi", "SPI", "dtparam=spi=on"),
            detectInterfaceFromDeviceTree("spi", "SPI bus controller"),
            detectSpi()
        ));
    }

    private static CheckerResult.Check detectSpi() {
        var result = new StringBuilder();

        try {
            Path pwmPath = Paths.get("/sys/bus/spi/devices");
            if (Files.exists(pwmPath)) {
                try (var stream = Files.walk(pwmPath, 2)) {
                    stream
                        .map(Path::getFileName)
                        .filter(fileName -> fileName.toString().startsWith("spi"))
                        .sorted()
                        .forEach(result::append);
                }
            }
        } catch (Exception e) {
            logger.error("Error detecting SPI: {}", e.getMessage());
        }

        var command = "ls -l /sys/bus/spi/devices";
        var expectedOutput = "spiX.Y (X and Y = numbers, when dtparam=spi=on is properly configured)";

        if (result.isEmpty()) {
            return new CheckerResult.Check(CheckerResult.ResultStatus.FAIL,
                command, expectedOutput, "");
        } else {
            return new CheckerResult.Check(CheckerResult.ResultStatus.PASS,
                command, expectedOutput, result.toString());
        }
    }
}
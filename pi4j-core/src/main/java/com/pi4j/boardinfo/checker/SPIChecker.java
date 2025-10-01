package com.pi4j.boardinfo.checker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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
            detectFilesInDirectory(Paths.get("/sys/bus/spi/devices"), "spi0.0 spi0.1 (SPI device entries)")
        ));
    }

    private static CheckerResult.Check detectFilesInDirectory(Path path, String expectedOutput) {
        var result = new ArrayList<String>();

        try {
            if (Files.exists(path)) {
                try (var stream = Files.walk(path, 1)) {
                    stream
                        .filter(sub -> !path.equals(sub)) // exclude the root directory
                        .filter(sub -> {
                            String name = sub.getFileName().toString();
                            return name.startsWith("spidev") || name.startsWith("spi-");
                        })
                        .sorted()
                        .forEach(sub -> result.add(sub.getFileName().toString()));
                }
            }
        } catch (Exception e) {
            logger.error("Error detecting SPI devices in path '{}': {}", path, e.getMessage());
        }

        var command = path.toString();

        if (result.isEmpty()) {
            return new CheckerResult.Check(CheckerResult.ResultStatus.FAIL,
                command, expectedOutput, "");
        } else {
            return new CheckerResult.Check(CheckerResult.ResultStatus.PASS,
                command, expectedOutput, result.toString());
        }
    }
}
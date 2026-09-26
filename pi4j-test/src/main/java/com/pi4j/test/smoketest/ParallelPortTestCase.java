package com.pi4j.test.smoketest;

import com.pi4j.io.gpio.parallel.ParallelPort;
import com.pi4j.io.gpio.parallel.ParallelPortConfig;
import com.pi4j.io.gpio.parallel.ParallelPortConfigBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test case which outputs values `0..3` on BCM 20 and 21 and reads the value on BCM 7 and 12. The test fails
 * if the value read does not match the value that was written.
 */
public class ParallelPortTestCase {

    private ParallelPortTestCase() {}

    private static final Logger logger = LoggerFactory.getLogger(ParallelPortTestCase.class);

    private static final String TEST_NAME = "Parallel Port";

    private static final ParallelPortConfig OUTPUT_CONFIG = ParallelPortConfigBuilder.newInstance()
        .id("parallel-out")
        .initialDirection(ParallelPort.Direction.OUTPUT)
        .shutdownValue(0)
        .bcm(20)
        .bcm(21)
        .build();

    private static final ParallelPortConfig INPUT_CONFIG = ParallelPortConfigBuilder.newInstance()
        .id("parallel-in")
        .initialDirection(ParallelPort.Direction.INPUT)
        .shutdownValue(0)
        .bcm(7)
        .bcm(12)
        .build();

    public static TestResult run(ProviderContext providerContext) {
        logger.info("Starting Parallel Port test");

        try(var outputPort = providerContext.getContext().create(OUTPUT_CONFIG);
            var inputPort = providerContext.getContext().create(INPUT_CONFIG)) {

            logger.info("Testing with configured port directions");

            for (int i = 0; i < 4; i++) {
                outputPort.write(i);
                var inputValue = inputPort.read();
                if (inputValue != i) {
                    return new TestResult(TEST_NAME, false,
                        "Input value didn't match output value (" + i + " != " + inputValue + ")"
                    );
                }
            }

            logger.info("Reversing port directions");

            outputPort.setDirection(ParallelPort.Direction.INPUT);
            inputPort.setDirection(ParallelPort.Direction.OUTPUT);

            for (int i = 0; i < 4; i++) {
                inputPort.write(i);
                var inputValue = outputPort.read();
                if (inputValue != i) {
                    return new TestResult(TEST_NAME, false,
                        "Reversed values did not match (" + i + " != " + inputValue + ")"
                    );
                }
            }
        } catch (Exception e) {
            logger.error("Test failure", e);
            return new TestResult(TEST_NAME, false, "Test failure: " + e.getMessage());
        }

        return new TestResult(TEST_NAME, true, "Write-read operations completed");
    }
}

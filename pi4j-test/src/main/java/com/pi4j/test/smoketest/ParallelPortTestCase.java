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
        .initialValue(2)
        .shutdownValue(1)   // shutdown value should be exclusive of the initial value
        .bcm(24)            // output port used by DigitalOutputTestCase
        .bcm(26)            // output port used by DigitalInputTestCase
        .build();

    private static final ParallelPortConfig INPUT_CONFIG = ParallelPortConfigBuilder.newInstance()
        .id("parallel-in")
        .initialDirection(ParallelPort.Direction.INPUT)
        .bcm(25)            // input port used by DigitalOutputTestCase
        .bcm(16)            // input port used by DigitalInputTestCase
        .build();

    public static TestResult run(ProviderContext providerContext) {
        logger.info("Starting Parallel Port test");

        try(var outputPort = providerContext.getContext().create(OUTPUT_CONFIG);
            var inputPort = providerContext.getContext().create(INPUT_CONFIG)) {

            logger.info("Testing initial value");

            var initialValue = inputPort.read();
            if (initialValue != OUTPUT_CONFIG.initialValue()) {
                return new TestResult(TEST_NAME, false,
                    String.format("Initial value did not match config (%d != %d)",
                        initialValue,
                        OUTPUT_CONFIG.initialValue()
                    )
                );
            }

            logger.info("Testing with configured port directions");

            for (int i = 0; i < 4; i++) {
                outputPort.write(i);
                var inputValue = inputPort.read();
                if (i != inputValue) {
                    return new TestResult(TEST_NAME, false,
                        String.format("Input value didn't match output value (%d != %d)", i, inputValue)
                    );
                }
            }

            logger.info("Reversing port directions");

            outputPort.setDirection(ParallelPort.Direction.INPUT);
            inputPort.setDirection(ParallelPort.Direction.OUTPUT);

            for (int i = 0; i < 4; i++) {
                inputPort.write(i);
                var inputValue = outputPort.read();
                if (i != inputValue) {
                    return new TestResult(TEST_NAME, false,
                        String.format("Reversed values did not match (%d != %d)", i, inputValue)
                    );
                }
            }

            // reset input port input mode
            inputPort.setDirection(ParallelPort.Direction.INPUT);
            outputPort.setDirection(ParallelPort.Direction.OUTPUT);
        } catch (Exception e) {
            logger.error("Write-read test failure", e);
            return new TestResult(TEST_NAME, false, "Write-read test failure: " + e.getMessage());
        }

        logger.info("Validating shutdown value");

        try(var input = providerContext.getContext().create(INPUT_CONFIG)) {
            var inputValue = input.read();
            if (inputValue != OUTPUT_CONFIG.shutdownValue()) {
                return new TestResult(TEST_NAME, false,
                    String.format("Input value did not match shutdown value (%d != %d)",
                        inputValue,
                        OUTPUT_CONFIG.shutdownValue()
                    )
                );
            }
        } catch (RuntimeException e) {
            logger.error("Failed to validate shutdown value: ", e);
            return new TestResult(TEST_NAME, false, "Shutdown validation test failure: " + e.getMessage());
        }

        logger.info("Reverting output port to input mode");

        try(var output = providerContext.getContext().create(OUTPUT_CONFIG)) {
            output.setDirection(ParallelPort.Direction.INPUT);
        }

        return new TestResult(TEST_NAME, true, "Parallel port operations completed");
    }
}

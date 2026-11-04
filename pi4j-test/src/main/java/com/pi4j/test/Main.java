package com.pi4j.test;

import com.pi4j.test.smoketest.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple test of the six providers. Dependent upon the wiring described in the README file.
 */
public class Main extends TestCase {

    /**
     * The entry point for the application. This method processes command-line
     * arguments to configure the application, initializes required components,
     * runs tests, and outputs results.
     *
     * @param args Command-line arguments for the application. Accepted parameters:
     *             - "-p <provider>": Specifies the test provider, either "newautocontext", or "ffm".
     *             - "-h": Displays help information and exits the application.
     */
    public static void main(String[] args) {
        TestCase.run(Main.class, args);
    }

    @Override
    public TestResult run(ProviderContext providerContext) {
        Logger logger = LoggerFactory.getLogger(Main.class);

        // Run the tests
        List<Class<? extends TestCase>> testClasses = List.of(
            I2CTestCase.class,
            I2CWithOffsetTestCase.class,
            SpiTestCase.class,
            SpiWithOffsetTestCase.class,
            // SpiWriteReadTestCase.class, // requires manual CS across write/read operation
            PWMTestCase.class,
            DigitalInputTestCase.class,
            FiveDigitalInputsTestCase.class,
            DigitalOutputTestCase.class,
            // DigitalInputDebounceMonitorTestCase.class, // This test needs a Logic Analyzer
            DigitalInputDebounceTimeTestCase.class,
            DigitalInputDebounceCountTestCase.class,
            DigitalInputMultipleListenersTestCase.class,
            ParallelPortTestCase.class,
            ParallelPortTestEventsCase.class
        );
        var tests = new ArrayList<TestResult>();
        for (var testClass : testClasses) {
            try {
                tests.add(testClass.getDeclaredConstructor().newInstance().run(providerContext));
            } catch (Exception | LinkageError e) {
                Throwable cause = e instanceof java.lang.reflect.InvocationTargetException && e.getCause() != null
                    ? e.getCause() : e;
                tests.add(new TestResult(testClass.getSimpleName(), false, cause.toString()));
            }
        }

        // Overall results
        logger.info("");
        logger.info("==============================================================");
        logger.info("Test results with {}:", providerContext.getTestProvider().name());
        logger.info("\tTotal Tests: {}", tests.size());
        logger.info("\tPassed: {}", tests.stream().filter(TestResult::success).count());
        logger.info("\tFailed: {}", tests.stream().filter(t -> !t.success()).count());
        logger.info("==============================================================");
        logger.info("");

        // Output results
        tests.forEach(t -> t.log(logger));

        long failed = tests.stream().filter(t -> !t.success()).count();
        return new TestResult("Smoke test suite", failed == 0,
            (tests.size() - failed) + " passed, " + failed + " failed");
    }
}

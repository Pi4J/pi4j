package com.pi4j.test.smoketest;

import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.gpio.parallel.ParallelPort;
import com.pi4j.io.gpio.parallel.ParallelPort.PinStateChangedEvent;
import com.pi4j.io.gpio.parallel.ParallelPortConfig;
import com.pi4j.io.gpio.parallel.ParallelPortConfigBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

/**
 * Test case which writes values 0..3 to a parallel port and expects events to be fired on a second, input parallel port.
 * The test will fail if the expected events are not received.
 */
public class ParallelPortTestEventsCase {

    private ParallelPortTestEventsCase() {}

    private static final Logger logger = LoggerFactory.getLogger(ParallelPortTestEventsCase.class);

    private static final String TEST_NAME = "Parallel Port Events";

    private static final ParallelPortConfig OUTPUT_CONFIG = ParallelPortConfigBuilder.newInstance()
        .id("parallel-out")
        .initialDirection(ParallelPort.Direction.OUTPUT)
        .shutdownValue(0)   // shutdown value should be exclusive of the initial value
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
        logger.info("Starting " + TEST_NAME + " test");

        try(var outputPort = providerContext.getContext().create(OUTPUT_CONFIG);
            var inputPort = providerContext.getContext().create(INPUT_CONFIG)) {

            var nextExpectation = new AtomicReference<Function<ParallelPort.PinStateChangedNotification, TestResult>>();
            var resultQueue = new ArrayBlockingQueue<TestResult>(1);

            inputPort.addListener(event -> {
                logger.info("Got event: {}", event);
                resultQueue.add(nextExpectation.get().apply(event));
            });
            outputPort.write(0);

            // First event is fired
            nextExpectation.set(event ->
                Arrays.asList(event.events()).contains(new PinStateChangedEvent(0, DigitalState.HIGH))
                    ? new TestResult(TEST_NAME, true, "Got first event")
                    : new TestResult(TEST_NAME, false, "Failed to get first event")
            );
            outputPort.write(1);
            var result = resultQueue.take();
            if (!result.success()) return result;

            // Second event is fired
            nextExpectation.set(event -> {
                if (event.events().length != 2) {
                    return new TestResult(TEST_NAME, false, "Didn't get 2 events");
                }

                if (Arrays.stream(event.events()).noneMatch(new PinStateChangedEvent(0, DigitalState.LOW)::equals)) {
                    return new TestResult(TEST_NAME, false, "No event for offset 0, state LOW");
                }

                if (Arrays.stream(event.events()).noneMatch(new PinStateChangedEvent(1, DigitalState.HIGH)::equals)) {
                    return new TestResult(TEST_NAME, false, "No event for offset 1, state HIGH");
                }

                return new TestResult(TEST_NAME, true, "Got second event");
            });
            outputPort.write(2);
            result = resultQueue.take();
            if (!result.success()) return result;

            // Third event is fired
            nextExpectation.set(event -> {
                if (event.events().length != 1) {
                    return new TestResult(TEST_NAME, false, "Didn't get 1 event");
                }

                if (Arrays.stream(event.events()).noneMatch(new PinStateChangedEvent(0, DigitalState.HIGH)::equals)) {
                    return new TestResult(TEST_NAME, false, "No event for offset 0, state HIGH");
                }

                return new TestResult(TEST_NAME, true, "Got third event");
            });
            outputPort.write(3);
            result = resultQueue.take();
            if (!result.success()) return result;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Test interrupted", e);
            return new TestResult(TEST_NAME, false, "Test interrupted: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Test failure", e);
            return new TestResult(TEST_NAME, false, "Test failure: " + e.getMessage());
        }

        return new TestResult(TEST_NAME, true, "Parallel port event operations completed");
    }
}

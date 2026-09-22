package com.pi4j.io.gpio.digital;

import com.pi4j.io.exception.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/** Internal helper implementing legacy operations for DigitalOutput. */
final class DigitalOutputOperations {

    private DigitalOutputOperations() {}

    private static final Logger logger = LoggerFactory.getLogger(DigitalOutputOperations.class);
    private static final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

    /**
     * This method will blink an output pin of the RPi according the given specifications.
     * The pin itself is created while creating a DigitalOutput configuration where one of
     * the parameters is an address (= a BCM pin number).
     *
     * @param delay    The toggle time.
     * @param duration The amount of times the output has to toggle.
     *                 <p>
     *                 Representation:
     *
     *                 <pre>
     *                   Output HIGH +-----+     +-----+     +-----+     +-----+     +-----+
     *                               |     |     |     |     |     |     |     |     |     |
     *                   Output LOW  +     +-----+     +-----+     +-----+     +-----+     +-----+
     *                               ^                                                           ^
     *                        start -┘                                                           └- stop
     *                                \___/ \___/
     *                                delay  delay
     *
     *                               \___________________________________________________________/
     *                                                        duration
     *                 </pre>
     *                 <p>
     *                 Example:
     *                 <p style = "margin-left: 100px">
     *                 Delay = 1 sec / duration = 5<br>
     *                 Output will be like so (suppose the initial state is set to HIGH):<br>
     *                 1 - 0 - 1 - 0 - 1 - 0 - 1 - 0 - 1 - 0 with each state lasting for 1 second.<br>
     *                 So, if you would connect a LED to the pin, you would see the LED switching<br>
     *                 on and off for 5 times.<br>
     *                 </p>
     *                 <p>
     *                 <b>Note: this is a blocking method!</b><br>
     *                 For as long as it takes to manipulate the output pin, the method will not return.<br>
     *                 <p>
     *                 In the example given above, it means the method will block for 10 seconds (5 times high for a second<br>
     *                 and 5 times low for a second), also for calling the callback function.
     *                 <p>
     *                 If you don't want the <code>blink()</code> method to block the calling thread, pls. use the
     *                 {@link #blinkAsync(DigitalOutput digitalOutput, int, int, java.util.concurrent.TimeUnit, com.pi4j.io.gpio.digital.DigitalState, java.util.concurrent.Callable) blinkAsync()} method instead.<br>
     *                 <p>
     * @param unit     The time unit used to calculate the delay.
     * @param state    The initial state of the pin.
     * @param callback The method to call, if any, once the blinking is done.
     */
    static DigitalOutput blink(DigitalOutput digitalOutput, int delay, int duration, TimeUnit unit, DigitalState state, Callable<Void> callback) {
        long millis = validateArguments(delay, duration, unit);

        digitalOutput.state(state);

        for (int i = 0; i < ((duration * 2) - 1); i++) {
            // block the current thread for the pulse duration
            // if you don't want a blocking call, pls. use the blinkAsync() method instead.
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                throw new RuntimeException("Pulse blocking thread interrupted. Exception message: [" + e.getMessage() + "].");
            }

            // toggle the pulse state
            digitalOutput.toggle();
        }

        // invoke callback if one was defined
        if (callback != null) {
            try {
                logger.info("Calling callback from blocking blink() method");
                callback.call();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        return digitalOutput;
    }


    static DigitalOutput pulse(DigitalOutput digitalOutput, int interval, TimeUnit unit, DigitalState state, Callable<Void> callback) throws IOException {

        long millis = validateArguments(interval, unit);

        // start the pulse state
        digitalOutput.state(state);

        // block the current thread for the pulse duration
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException("Pulse blocking thread interrupted.", e);
        }

        // end the pulse state
        digitalOutput.state(DigitalState.getInverseState(state));

        // invoke callback if one was defined
        if (callback != null) {
            try {
                logger.info("Calling callback from blocking pulse() method");
                callback.call();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }

        return digitalOutput;
    }


    static Future<?> pulseAsync(DigitalOutput digitalOutput, int interval, TimeUnit unit, DigitalState state, Callable<Void> callback) {
        validateArguments(interval, unit);
        return executorService.submit(() -> pulse(digitalOutput, interval, unit, state, callback));
    }


    /**
     * This method is exactly the same as the blink() method, except that this method is <b>non-blocking</b> and returns a {@link Future} with which the action can be cancelled, or it can be detected if the task is complete
     * <p>
     * See the {@link #blink(com.pi4j.io.gpio.digital.DigitalOutput, int, int, java.util.concurrent.TimeUnit, com.pi4j.io.gpio.digital.DigitalState, java.util.concurrent.Callable) blink()}
     * method for a more detailed explanation on how the method works.
     *
     * @param delay    The toggle time.
     * @param duration The amount of times the output has to toggle.
     * @param unit     The time unit used to calculate the delay.
     * @param state    The initial state of the pin.
     * @param callback The method to call, if any, once the blinking is done.
     * @return A Future object that can be used to observe the end of the async blinking.
     */
    public static Future<?> blinkAsync(DigitalOutput digitalOutput, int delay, int duration, TimeUnit unit, DigitalState state, Callable<Void> callback) {
        validateArguments(delay, duration, unit);
        return executorService.submit(() -> blink(digitalOutput, delay, duration, unit, state, callback));
    }



    ////////////////////////////////////////////////////////////////////////////////
    // Private section
    ////////////////////////////////////////////////////////////////////////////////


    /**
     * This method verifies the interval given and indirectly the time unit given.
     * The interval must be > 0, else an IllegalArgumentException is thrown.
     *
     * @param interval The output change interval.
     * @param unit     A time unit.
     * @return Number of milliseconds.
     */
    private static long validateArguments(int interval, TimeUnit unit) {

        if (interval <= 0) {
            throw new IllegalArgumentException("A time interval of zero or less is not supported.");
        }

        return validateTimeUnit(interval, unit);
    }


    /**
     * This method verifies the interval and duration given and indirectly the time unit given.
     * Both the interval as well as the duration must be > 0, else an IllegalArgumentException is thrown.
     *
     * @param interval The output change interval.
     * @param duration The amount of times the output toggles.
     * @param unit     A time unit.
     * @return Number of milliseconds.
     */
    private static long validateArguments(int interval, int duration, TimeUnit unit) {

        if (interval <= 0) {
            throw new IllegalArgumentException("A time interval of zero or less is not supported.");
        }

        if (duration <= 0) {
            throw new IllegalArgumentException("A time duration of zero or less is not supported.");
        }

        return validateTimeUnit(interval, unit);
    }


    /**
     * This method verifies the time unit given.
     * When an unsupported unit is encountered, an IllegalArgumentException is thrown.
     * Unsupported time units are:
     * - TimeUnit.NANOSECONDS
     * - TimeUnit.MICROSECONDS
     * - TimeUnit.DAYS
     *
     * @param unit A time unit.
     * @return Number of milliseconds.
     */
    private static long validateTimeUnit(int interval, TimeUnit unit) {
        long millis;
        switch (unit) {
            case NANOSECONDS:
                throw new IllegalArgumentException("TimeUnit.NANOSECONDS is not supported.");
            case MICROSECONDS:
                throw new IllegalArgumentException("TimeUnit.MICROSECONDS is not supported.");
            case DAYS:
                throw new IllegalArgumentException("TimeUnit.DAYS is not supported.");
            default:
                millis = unit.toMillis(interval);
                break;
        }

        return millis;
    }

}

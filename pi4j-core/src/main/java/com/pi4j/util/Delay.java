package com.pi4j.util;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * Provide a way to set a sleep time -- and to materialize it immediately -- or later at a different place,
 * where the sleep time is actually required.
 * <p>
 * Device communication often requires delays between different operations. Just sleeping often will waste this time.
 * For instance, while waiting for a display to be ready to receive the next image, the next image could be
 * rendered already.
 * <p>
 * This class allows setting a delay after an operation imposing a delay -- but then defer its materialized until the
 * delay is actually required -- typically, immediately before the next IO operation.
 * <p>
 * The set call is chainable to make immediately materializing a call concise and simple.
 * <p>
 * If the high precision constructor parameter is set, active waiting will be used for
 * delay times below the precsion of parking on linux.
 */
public class Delay {

    private static long MAX_ACTIVE_WAITING_NANOS = 100_000;
    private static long MAX_PARKING_NANOS = 100_000_000;

    private final boolean highPrecision;
    private Instant busyUntil = Instant.now();

    public Delay() {
        this(false);
    }

    /**
     * If the high precision parameter is set, active waiting will be used for
     * delay times below the precsion of parking on linux.
     */
    public Delay(boolean highPrecision) {
        this.highPrecision = highPrecision;
    }

    /**
     * Sets a delay in microseconds that will be materialized when materialize() is called.
     * <p>
     * If a delay was set already previously, it will only be overridden if it results in a later "busyUntil" time.
     * <p>
     * Typically called after device IO calls where it's known how long command processing will take and the
     * device will be ready again.
     */
    public Delay setMicros(long micros) {
        return setNanos(micros * 1_000);
    }

    /**
     * Sets a delay in milliseconds that will be materialized when materialize() is called.
     * <p>
     * If a delay was set already previously, it will only be overridden if it results in a later "busyUntil" time.
     * <p>
     * Typically called after device IO calls where it's known how long command processing will take and the
     * device will be ready again.
     */
    public Delay setMillis(long millis) {
        return setNanos(millis * 1_000_000);
    }

    /**
     * Sets a delay in nanoseconds that will be materialized when materialize() is called.
     * <p>
     * If a delay was set already previously, it will only be overridden if it results in a later "busyUntil" time.
     * <p>
     * Typically called after device IO calls where it's known how long command processing will take and the
     * device will be ready again.
     */
    public Delay setNanos(long nanos) {
        Instant candidate = Instant.now().plusNanos(nanos);
        if (candidate.isAfter(busyUntil)) {
            busyUntil = candidate;
        }
        return this;
    }

    /** Returns the instant when the requested delay will be over. Can be in the past or future. */
    public Instant getBusyUntil() {
        return busyUntil;
    }

    /**
     * Sleeps until the requested delay time has passed.
     * Depending on the remaining time and the high precision constructor setting,
     * active waiting or parking will be used for shorter time periods.
     * <p>
     * If an InterruptedException occurs during
     * sleeping, the exception will be re-thrown as RuntimeException.
     */
    public void materialize() {
        while (true) {
            long remaining = Instant.now().until(busyUntil, ChronoUnit.NANOS);
            if (remaining < 0) {
                break;
            } else if (highPrecision && remaining <= MAX_ACTIVE_WAITING_NANOS) {
                // Keep waiting actively
            } else if (remaining <= MAX_PARKING_NANOS) {
                LockSupport.parkNanos(remaining - (highPrecision ? MAX_ACTIVE_WAITING_NANOS : 0));
            } else {
                remaining -= MAX_PARKING_NANOS;
                try {
                    Thread.sleep(remaining / 1_000_000, (int) (remaining % 1_000_000));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }
            }
        }
    }
}

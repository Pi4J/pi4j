package com.pi4j.util;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Provide a way to set a sleep time -- and to materialize it later at a different place, where the sleep time
 * is actually required.
 * <p>
 * Device communication often requires delays between different operations. Just sleeping often will waste this time.
 * For instance, while waiting for a display to be ready to receive the next image, the next image could be
 * rendered already.
 * <p>
 * This class allows setting a delay after an operation imposing a delay -- but then defer its materialized until the
 * delay is actually required -- typically, immediately before the next IO operation.
 */
public class DeferredDelay {

    private Instant busyUntil = Instant.now();

    /**
     * Sets a delay in milliseconds that will be materialized when materializeDelay() is called.
     * <p>
     * If a delay was set already previously, it will only be overridden if it results in a later "busyUntil" time.
     * <p>
     * Typically called after device IO calls where it's known how long command processing will take and the
     * device will be ready again.
     */
    public void setDelayMillis(long millis) {
        setDelayNanos(millis * 1_000_000);
    }

    /**
     * Sets a delay in nanoseconds that will be materialized when materializeDelay() is called.
     * <p>
     * If a delay was set already previously, it will only be overridden if it results in a later "busyUntil" time.
     * <p>
     * Typically called after device IO calls where it's known how long command processing will take and the
     * device will be ready again.
     */
    public void setDelayNanos(long nanos) {
        Instant candidate = Instant.now().plusNanos(nanos);
        if (candidate.isAfter(busyUntil)) {
            busyUntil = candidate;
        }
    }

    /** Returns the instant when the requested delay will be over. Can be in the past or future. */
    public Instant getBusyUntil() {
        return busyUntil;
    }

    /**
     * Sleeps until the delay time requested by setDelay has passed. If an InterruptedException occurs during
     * sleeping, the exception will be re-thrown as RuntimeException.
     */
    public void materializeDelay() {
        while (true) {
            long remaining = Instant.now().until(busyUntil, ChronoUnit.NANOS);
            if (remaining < 0) {
                break;
            }
            try {
                Thread.sleep(remaining / 1_000_000, (int) (remaining % 1_000_000));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }
    }
}

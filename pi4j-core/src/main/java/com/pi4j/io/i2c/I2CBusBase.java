package com.pi4j.io.i2c;

import com.pi4j.exception.Pi4JException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.String.format;

/**
 * Base implementation of {@link I2CBus} providing fair, lock-based serialization of bus access so that the
 * {@code execute} actions of multiple {@link I2C} devices sharing the same bus cannot interleave. Concrete
 * provider buses extend this and route their {@code execute(...)} calls through {@link #_execute(I2C, Callable)}.
 */
public abstract class I2CBusBase implements I2CBus {

    private static final Logger logger = LoggerFactory.getLogger(I2CBusBase.class);

    /** Default time to wait when acquiring the bus access lock, in {@link #DEFAULT_LOCK_ACQUIRE_TIMEOUT_UNITS}. */
    public static final long DEFAULT_LOCK_ACQUIRE_TIMEOUT = 1000;
    /** Time unit for {@link #DEFAULT_LOCK_ACQUIRE_TIMEOUT}. */
    public static final TimeUnit DEFAULT_LOCK_ACQUIRE_TIMEOUT_UNITS = TimeUnit.MILLISECONDS;

    protected final int bus;

    protected final long lockAquireTimeout;
    protected final TimeUnit lockAquireTimeoutUnit;
    private final ReentrantLock lock = new ReentrantLock(true);

    /**
     * Creates the bus base from the given device configuration, capturing the bus number and applying the default
     * lock acquisition timeout.
     *
     * @param config the configuration whose bus number identifies this bus
     * @throws IllegalArgumentException if the configuration does not specify a bus number
     */
    public I2CBusBase(I2CConfig config) {
        if (config.bus() == null)
            throw new IllegalArgumentException("I2C bus must be specified");

        this.bus = config.getBus();

        this.lockAquireTimeout = DEFAULT_LOCK_ACQUIRE_TIMEOUT;
        this.lockAquireTimeoutUnit = DEFAULT_LOCK_ACQUIRE_TIMEOUT_UNITS;
    }

    /**
     * Runs the given action while holding this bus's exclusive lock, providing thread-safe access to the shared
     * bus. The lock is acquired immediately if free, otherwise the call waits up to the configured timeout before
     * failing.
     *
     * @param i2c    the device on whose behalf the action is performed; used for diagnostics
     * @param action the work to perform while the bus is locked
     * @param <R>    the result type produced by the action
     * @return the value returned by the action
     * @throws NullPointerException if {@code i2c} or {@code action} is {@code null}
     * @throws Pi4JException        if the lock cannot be acquired within the timeout, or if the action throws
     * @throws RuntimeException     if the calling thread is interrupted while waiting for the lock
     */
    protected <R> R _execute(I2C i2c, Callable<R> action) {
        if (i2c == null)
            throw new NullPointerException("Parameter 'i2c' is mandatory!");
        if (action == null)
            throw new NullPointerException("Parameter 'action' is mandatory!");
        try {
            if (this.lock.tryLock() || this.lock.tryLock(this.lockAquireTimeout, this.lockAquireTimeoutUnit)) {
                try {
                    return action.call();
                } finally {
                    this.lock.unlock();
                }
            } else {
                throw new Pi4JException(
                    format("Failed to get I2C lock on bus {0} after {1} {2}", this.bus, this.lockAquireTimeout,
                        this.lockAquireTimeoutUnit));
            }
        } catch (InterruptedException e) {
            logger.error("Failed locking {}-{}", getClass().getSimpleName(), this.bus, e);
            throw new RuntimeException("Could not obtain an access-lock!", e);
        } catch (Exception e) {
            throw new Pi4JException("Failed to execute action for device " + i2c.device() + " on bus " + this.bus, e);
        }
    }
}

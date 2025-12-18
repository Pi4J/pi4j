package com.pi4j.io.i2c;

import com.pi4j.exception.Pi4JException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.String.format;

public abstract class I2CBusBase implements I2CBus {

    private static final Logger logger = LoggerFactory.getLogger(I2CBusBase.class);

    public static final long DEFAULT_LOCK_ACQUIRE_TIMEOUT = 1000;
    public static final TimeUnit DEFAULT_LOCK_ACQUIRE_TIMEOUT_UNITS = TimeUnit.MILLISECONDS;

    protected final int bus;

    protected final long lockAquireTimeout;
    protected final TimeUnit lockAquireTimeoutUnit;
    private final ReentrantLock lock = new ReentrantLock(true);

    public I2CBusBase(I2CConfig config) {
        if (config.bus() == null)
            throw new IllegalArgumentException("I2C bus must be specified");

        this.bus = config.getBus();

        this.lockAquireTimeout = DEFAULT_LOCK_ACQUIRE_TIMEOUT;
        this.lockAquireTimeoutUnit = DEFAULT_LOCK_ACQUIRE_TIMEOUT_UNITS;
    }

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

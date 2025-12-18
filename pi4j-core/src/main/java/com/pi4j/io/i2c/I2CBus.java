package com.pi4j.io.i2c;

import java.util.concurrent.Callable;

/**
 * This interface defines method to be performed on an I2C bus. Most importantly the {@link #execute(I2C, Callable)}
 * allows to perform bulk operations on the bus in a thread safe manner.
 */
public interface I2CBus {

    /**
     * Executes the given action, which typically performs multiple I2C reads and/or writes on the I2C bus in a thread
     * safe manner, i.e. the bus is blocked till the action is completed.
     *
     * @param i2c    the device for which to perform the action
     * @param action the action to perform
     * @param <R>    the result type of the action, if any
     *
     * @return the result of the action
     */
    <R> R execute(I2C i2c, Callable<R> action);
}

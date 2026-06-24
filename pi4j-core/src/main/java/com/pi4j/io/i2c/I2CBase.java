package com.pi4j.io.i2c;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  I2CBase.java
 *
 * This file is part of the Pi4J project. More information about
 * this project can be found here:  https://pi4j.com/
 * **********************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.pi4j.context.Context;
import com.pi4j.exception.ShutdownException;
import com.pi4j.io.IOBase;
import com.pi4j.io.i2c.impl.DefaultI2CRegister;

import java.util.concurrent.Callable;

/**
 * Base class for {@link I2C} device implementations, tracking the open/closed state and delegating bus-level
 * serialization to its associated {@link I2CBus}. Concrete providers extend this to add the actual read/write
 * transport for a specific platform.
 *
 * @param <T> the concrete {@link I2CBus} type this device communicates over
 */
public abstract class I2CBase<T extends I2CBus> extends IOBase<I2C, I2CConfig, I2CProvider> implements I2C {

    protected boolean isOpen;
    protected final T i2CBus;

    /**
     * Creates an I2C device bound to the given provider, configuration and bus, marking it as open.
     *
     * @param provider the provider that created this device
     * @param config   the configuration describing the bus and device address
     * @param i2CBus   the bus instance used to serialize access for this device
     */
    public I2CBase(I2CProvider provider, I2CConfig config, T i2CBus) {
        super(provider, config);
        this.isOpen = true;
        this.i2CBus = i2CBus;
    }

    @Override
    public boolean isOpen() {
        return this.isOpen;
    }

    @Override
    public void close() {
        if (isOpen) {
            super.close();
            this.isOpen = false;
        }
    }

    @Override
    public I2CRegister getRegister(int address) {
        return new DefaultI2CRegister(this, address);
    }

    @Override
    public <V> V execute(Callable<V> action) {
        if (action == null)
            throw new NullPointerException("Parameter 'action' is mandatory!");
        return this.i2CBus.execute(this, action);
    }

    @Override
    public I2C shutdownInternal(Context context) throws ShutdownException {
        // if this I2C device is still open, then we need to close it since we are shutting down
        if (this.isOpen()) {
            try {
                close();
            } catch (Exception e) {
                throw new ShutdownException(e);
            }
        }
        return this;
    }
}

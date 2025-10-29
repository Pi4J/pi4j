package com.pi4j.plugin.linuxfs.provider.gpio.digital;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: PLUGIN   :: LinuxFS I/O Providers
 * FILENAME      :  LinuxFsDigitalInput.java
 *
 * This file is part of the Pi4J project. More information about
 * this project can be found here:  https://pi4j.com/
 * **********************************************************************
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import com.pi4j.context.Context;
import com.pi4j.exception.InitializeException;
import com.pi4j.exception.ShutdownException;
import com.pi4j.io.exception.IOException;
import com.pi4j.io.gpio.digital.*;
import com.pi4j.plugin.linuxfs.internal.LinuxGpio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.*;
import java.util.concurrent.Future;

/**
 * <p>LinuxFsDigitalInput class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public class LinuxFsDigitalInput extends DigitalInputBase implements DigitalInput {

    protected final LinuxGpio gpio;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    protected DigitalState state = DigitalState.UNKNOWN;
    private Future<?> inputListener;

    /**
     * <p>Constructor for LinuxFsDigitalInput.</p>
     *
     * @param gpio     a {@link LinuxGpio} linux file system GPIO object.
     * @param provider a {@link com.pi4j.io.gpio.digital.DigitalInputProvider} object.
     * @param config   a {@link com.pi4j.io.gpio.digital.DigitalInputConfig} object.
     */
    public LinuxFsDigitalInput(LinuxGpio gpio, DigitalInputProvider provider, DigitalInputConfig config) {
        super(provider, config);
        this.gpio = gpio;
    }

    @Override
    public DigitalInput initialize(Context context) throws InitializeException {
        logger.trace("initializing GPIO [{}]; {}", this.config.pin(), gpio.getPinPath());

        // [EXPORT] requested GPIO pin if its not already exported
        try {
            if (!gpio.isExported()) {
                logger.trace("exporting GPIO [{}]; {}", this.config.pin(), gpio.getPinPath());
                gpio.export();
            } else {
                logger.trace("GPIO [{}] is already exported; {}", this.config.pin(), gpio.getPinPath());
            }
        } catch (java.io.IOException e) {
            logger.error(e.getMessage(), e);
            throw new InitializeException("Unable to export GPIO [" + config.pin() + "] @ <" + gpio.systemPath() + ">; " + e.getMessage(), e);
        }

        // [INPUT] configure GPIO pin direction as digital input
        try {
            logger.trace("set direction [IN] on GPIO {}", gpio.getPinPath());
            gpio.direction(LinuxGpio.Direction.IN);
        } catch (java.io.IOException e) {
            logger.error(e.getMessage(), e);
            throw new InitializeException("Unable to set GPIO [" + config.pin() + "] DIRECTION=[IN] @ <" + gpio.pinPath() + ">; " + e.getMessage(), e);
        }

        // GPIO PIN PULL UP/DOWN
        if (this.config.pull() == PullResistance.PULL_DOWN) {
            // TODO :: IMPLEMENT GPIO PULL DOWN
        } else if (this.config.pull() == PullResistance.PULL_UP) {
            // TODO :: IMPLEMENT GPIO PULL UP
        } else if (this.config.pull() == PullResistance.OFF) {
            // TODO :: IMPLEMENT GPIO PULL OFF
        }

        // [STATE] get current GPIO state via Linux File System
        try {
            this.state = gpio.state();
        } catch (java.io.IOException e) {
            logger.error(e.getMessage(), e);
            throw new InitializeException("Unable to get GPIO [" + config.pin() + "] state @ <" + gpio.pinPath() + ">; " + e.getMessage(), e);
        }

        // [INTERRUPT] enable GPIO interrupt via Linux File System (if supported)
        try {
            if (gpio.isInterruptSupported()) gpio.interruptEdge(LinuxGpio.Edge.BOTH);
        } catch (java.io.IOException e) {
            logger.error(e.getMessage(), e);
            throw new InitializeException("Unable to set GPIO [" + config.pin() + "] INTERRUPT EDGE=[BOTH] @ <" + gpio.pinPath() + ">; " + e.getMessage(), e);
        }

        // [INITIALIZE] perform any further initialization on GPIO  via superclass impl
        super.initialize(context);

        // [MONITOR] start background monitoring thread for GPIO state changes
        logger.trace("start monitoring thread for GPIO [{}]; {}", this.config.pin(), gpio.getPinPath());
        Runnable monitorTask = () -> {
            try {
                // create file system watcher
                logger.trace("monitoring thread watching GPIO [{}]; {}",
                    LinuxFsDigitalInput.this.config.pin(),
                    gpio.getPinPath());
                WatchService watchService = FileSystems.getDefault().newWatchService();
                WatchKey key;

                // create GPIO path to monitor
                Path path = Paths.get(gpio.getPinPath());

                // only watch for modified files in this path
                path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

                // dispatch value change event
                while ((key = watchService.take()) != null) {
                    for (WatchEvent<?> event : key.pollEvents()) {
                        if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                            if (event.context().toString().equalsIgnoreCase("value")) {
                                // filter out any redundant event notifications for same state
                                DigitalState newState = LinuxFsDigitalInput.this.gpio.state();
                                if (newState != LinuxFsDigitalInput.this.state) {
                                    LinuxFsDigitalInput.this.state = newState;
                                    LinuxFsDigitalInput.this.dispatch(
                                        new DigitalStateChangeEvent(LinuxFsDigitalInput.this, newState));
                                }
                            }
                        }
                    }
                    key.reset();
                }
            } catch (java.io.IOException e) {
                logger.error(e.getMessage(), e);
            } catch (InterruptedException e) {
                // thread interrupted; likely exiting on shutdown
            }
        };
        this.inputListener = context.submitTask(monitorTask);

        // return this I/O instance
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DigitalInput shutdownInternal(Context context) throws ShutdownException {
        logger.trace("shutdown GPIO [{}]; {}", this.config.pin(), gpio.getPinPath());

        // this line will execute immediately, not waiting for your task to complete
        logger.trace("shutdown monitoring thread for GPIO [{}]; {}", this.config.pin(), gpio.getPinPath());
        if (this.inputListener != null) {
            if (!this.inputListener.cancel(true))
                logger.error("Failed to cancel input listener!");
        }

        // perform any shutdown cleanup via superclass
        super.shutdownInternal(context);

        // un-export the GPIO pin from the Linux file system impl
        try {
            logger.trace("un-exporting GPIO [{}]; {}", this.config.pin(), gpio.getPinPath());
            gpio.unexport();
        } catch (java.io.IOException e) {
            logger.error(e.getMessage(), e);
            throw new ShutdownException("Failed to UN-EXPORT GPIO [" + config().pin() + "] @ <" + gpio.systemPath() + ">; " + e.getMessage(), e);
        }

        // return this digital input instance
        return this;
    }

    @Override
    public DigitalState state() {
        logger.trace("get state on GPIO [{}]; {}", this.config.pin(), gpio.getPinPath());
        try {
            // acquire actual GPIO state directly from Linux file system impl
            this.state = gpio.state();
            return this.state;
        } catch (java.io.IOException e) {
            logger.error(e.getMessage(), e);
            throw new IOException(e.getMessage(), e);
        }
    }
}

package com.pi4j.plugin.linuxfs.provider.gpio.digital;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: PLUGIN   :: LinuxFS I/O Providers
 * FILENAME      :  LinuxFsDigitalOutput.java
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


/**
 * <p>LinuxFsDigitalOutput class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public class LinuxFsDigitalOutput extends DigitalOutputBase implements DigitalOutput {

    protected final LinuxGpio gpio;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * <p>Constructor for LinuxFsDigitalOutput.</p>
     *
     * @param gpio a {@link LinuxGpio} linux file system GPIO object.
     * @param provider a {@link com.pi4j.io.gpio.digital.DigitalOutputProvider} object.
     * @param config a {@link com.pi4j.io.gpio.digital.DigitalOutputConfig} object.
     */
    public LinuxFsDigitalOutput(LinuxGpio gpio, DigitalOutputProvider provider, DigitalOutputConfig config){
        super(provider, config);
        this.gpio = gpio;
    }

    @Override
    public DigitalOutput initialize(Context context) throws InitializeException {
        logger.trace("initializing GPIO [{}]; {}", this.config.address(), gpio.getPinPath());

        // [EXPORT] requested GPIO pin if its not already exported
        try {
            if(!gpio.isExported()) {
                logger.trace("exporting GPIO [{}]; {}", this.config.address(), gpio.getPinPath());
                gpio.export();
            } else{
                logger.trace("GPIO [{}] is already exported; {}", this.config.address(), gpio.getPinPath());
            }
        } catch (java.io.IOException e) {
            logger.error(e.getMessage(), e);
            throw new InitializeException("Unable to export GPIO [" + config.address() + "] @ <" + gpio.systemPath() + ">; " + e.getMessage(), e);
        }

        // [OUTPUT] configure GPIO pin direction as digital output
        try {
            logger.trace("set direction [OUT] on GPIO {}", gpio.getPinPath());
            gpio.direction(LinuxGpio.Direction.OUT);
        } catch (java.io.IOException e) {
            logger.error(e.getMessage(), e);
            throw new InitializeException("Unable to set GPIO [" + config.address() + "] DIRECTION=[OUT] @ <" + gpio.pinPath() + ">; " + e.getMessage(), e);
        }

        // [INTERRUPT] enable GPIO interrupt via Linux File System (if supported)
        try {
            if (gpio.isInterruptSupported()) gpio.interruptEdge(LinuxGpio.Edge.BOTH);
        } catch (java.io.IOException e) {
            logger.error(e.getMessage(), e);
            throw new InitializeException("Unable to set GPIO [" + config.address() + "] INTERRUPT EDGE=[BOTH] @ <" + gpio.pinPath() + ">; " + e.getMessage(), e);
        }

        // [INITIALIZE STATE] initialize GPIO pin state (via superclass impl)
        super.initialize(context);

        // return this instance
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public DigitalOutput shutdownInternal(Context context) throws ShutdownException {
        logger.trace("shutdown GPIO [{}]; {}", this.config.address(), gpio.getPinPath());

        // --------------------------------------------------------------------------
        // [ATTENTION]
        // --------------------------------------------------------------------------
        // IF WE UN-EXPORT THE PIN, ANY SHUTDOWN STATE WILL BE IGNORED AND
        // THE PIN WILL DEFAULT TO A LOW STATE OR POTENTIALLY A FLOATING STATE
        // IF THE PIN IS DEFAULTED AS AN INPUT PIN AS A RESULT OF UN-EXPORTING IT.
        //
        // IF A VALID SHUTDOWN STATE HAS BEEN CONFIGURED FOR THIS DIGITAL OUTPUT INSTANCE,
        // WE WILL LEAVE THE GPIO PIN EXPORTED ON SHUTDOWN OF PI4J
        //
        // IF NO SHUTDOWN STATE HAS BEEN CONFIGURED FOR THIS DIGITAL OUTPUT INSTANCE,
        // WE WILL UN-EXPORT THE GPIO PIN ON SHUTDOWN OF PI4J
        //
        // (this was the same behavior in Pi4J v1.x)
        // --------------------------------------------------------------------------

        // set pin state to shutdown state if a shutdown state is configured
        if(config().shutdownState() != null && config().shutdownState() != DigitalState.UNKNOWN){
            return super.shutdownInternal(context);
        }

        // otherwise ... un-export the GPIO pin from the Linux file system impl
        try {
            logger.trace("un-exporting GPIO [{}]; {}", this.config.address(), gpio.getPinPath());
            gpio.unexport();
        } catch (java.io.IOException e) {
            logger.error(e.getMessage(), e);
            throw new ShutdownException("Failed to UN-EXPORT GPIO [" + config().address() + "] @ <" + gpio.systemPath() + ">; " + e.getMessage(), e);
        }

        // return this digital output instance
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public DigitalOutput state(DigitalState state) throws IOException {
        logger.trace("set state [{}] on GPIO [{}]; {}", state.getName(), this.config.address(), gpio.getPinPath());
        try {
            // apply requested GPIO state via Linux FS
            gpio.state(state);
        } catch (java.io.IOException e) {
            logger.error(e.getMessage(), e);
            throw new IOException(e.getMessage(), e);
        }
        return super.state(state);
    }

    @Override
    public DigitalState state() {
        logger.trace("get state on GPIO [{}]; {}", this.config.address(), gpio.getPinPath());

        try {
            // acquire actual GPIO state directly from Linux file system impl
            DigitalState currentState = gpio.state();

            // update/sync internal state tracking variable if mismatch
            if(this.state != currentState) {
                this.state = currentState;
                logger.trace("state mismatch detected; sync internal state [{}] on GPIO [{}]; {}",
                             this.state.getName(),
                             this.config.address(),
                             gpio.getPinPath());
            }
        } catch (java.io.IOException e) {
            logger.error(e.getMessage(), e);
            throw new IOException(e.getMessage(), e);
        }

        // return current GPIO state via superclass impl
        return super.state();
    }
}

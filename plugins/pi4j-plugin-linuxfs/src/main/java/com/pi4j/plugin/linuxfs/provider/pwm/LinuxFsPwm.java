package com.pi4j.plugin.linuxfs.provider.pwm;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: PLUGIN   :: LinuxFS I/O Providers
 * FILENAME      :  LinuxFsPwm.java
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
import com.pi4j.io.pwm.*;
import com.pi4j.plugin.linuxfs.internal.LinuxPwm;
import com.pi4j.util.Frequency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * <p>LinuxFsPwm class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public class LinuxFsPwm extends PwmBase implements Pwm {

    protected final LinuxPwm pwm;
    protected int actualFrequency = -1;
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    private final boolean initializing = false;

    /**
     * <p>Constructor for PiGpioPwmHardware.</p>
     *
     * @param pwm      a {@link LinuxPwm} object.
     * @param provider a {@link PwmProvider} object.
     * @param config   a {@link PwmConfig} object.
     */
    public LinuxFsPwm(LinuxPwm pwm, PwmProvider provider, PwmConfig config) {
        super(provider, config);
        this.pwm = pwm;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pwm initialize(Context context) throws InitializeException {
        logger.trace("initializing PWM [{}]; {}", this.config.address(), pwm.getPwmPath());

        // first determine if this PWM chipset supports this PWM channel/pin number
        try {
            if (this.config.address() >= pwm.channels()) {
                throw new InitializeException("Unsupported pin/channel by PWM chipset for PWM [" + config.address() + "] @ <" + pwm.systemPath() + ">");
            }
        } catch (java.io.IOException e) {
            logger.error(e.getMessage(), e);
            throw new InitializeException("Unable to get number of pins/channels supported by PWM chipset for PWM [" + config.address() + "] @ <" + pwm.systemPath() + ">; " + e.getMessage(), e);
        }

        // [EXPORT] requested PWM channel if its not already exported
        try {
            if (!pwm.isExported()) {
                logger.trace("exporting PWM [{}]; {}", this.config.address(), pwm.getPwmPath());
                pwm.export();
                // Delay to allow the SSD to persist the new directory and device partitions
                Thread.sleep(70);
            } else {
                logger.trace("PWM [{}] is already exported; {}", this.config.address(), pwm.getPwmPath());
            }
        } catch (java.io.IOException e) {
            logger.error(e.getMessage(), e);
            throw new InitializeException("Unable to export PWM [" + config.address() + "] @ <" + pwm.systemPath() + ">; " + e.getMessage(), e);
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
            throw new InitializeException("Programmed delay failure, unable to export PWM [" + config.address() + "] @ <" + pwm.systemPath() + ">; " + e.getMessage(), e);
        }

        // [INITIALIZE STATE] initialize PWM pin state (via superclass impl)
        super.initialize(context);

        // return this instance
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pwm on() throws IOException {
        try {
            // before attempting to set new values to PWM pin, lets disable it and reset the duty-cycle
            // (we reset the duty cycle to zero because a failure will occur if applying a new period
            // (nanoseconds) that is less than the actively/existing configured duty cycle.
            if (pwm.dutyCycle() > 0) pwm.dutyCycle(0);
            if (pwm.enabled()) pwm.disable();
            this.onState = false;

            // if the frequency is zero, then bail out
            if (this.frequency <= 0) return this;

            // calculate period in nanoseconds from configured frequency
            long period = Frequency.nanoseconds(this.frequency);

            // set PWM period in nanoseconds based on configured frequency
            logger.trace("set 'period' of PWM [{}] to [{}]; {}", this.config.address(), Long.toUnsignedString(period), pwm.getPwmPath());
            pwm.period(period);

            // calculate duty cycle nanoseconds from configured duty cycle percentage
            long dcycle = Math.round(period * this.dutyCycle / 100);

            // set PWM duty-cycle nanoseconds
            logger.trace("set 'duty_cycle' of PWM [{}] to [{}]; {}", this.config.address(), dcycle, pwm.getPwmPath());
            pwm.dutyCycle(dcycle);

            // set PWM polarity
            logger.trace("set 'polarity' of PWM [{}] to [{}]; {}", this.config.address(), this.polarity.getName(), pwm.getPwmPath());
            pwm.polarity((this.polarity == PwmPolarity.INVERSED) ? LinuxPwm.Polarity.INVERSED : LinuxPwm.Polarity.NORMAL);

            // enable PWM signal
            logger.trace("enable PWM [{}]; {}", this.config.address(), pwm.getPwmPath());
            pwm.enable();

            // update tracking state
            this.onState = true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new IOException(e);
        }

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pwm off() throws IOException {
        try {
            // disable PWM
            logger.trace("disable PWM [{}]; {}", this.config.address(), pwm.getPwmPath());
            if (pwm.enabled()) {
                pwm.disable();
            }
            
            // update tracking state
            this.onState = false;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new IOException("Unable to disable (OFF) PWM [" + config.address() + "] @ <" + pwm.pwmPath() + ">; " + e.getMessage(), e);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isOn() {
        try {
            return pwm.isEnabled();
        } catch (java.io.IOException e) {
            logger.error(e.getMessage(), e);
            throw new IOException("Unable to get ON (enabled) state from PWM [" + config.address() + "] @ <" + pwm.pwmPath() + ">; " + e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getActualFrequency() throws IOException {
        try {
            // get period in nanoseconds from PWM pin
            long period = pwm.period();

            // calculate actual frequency from the obtained period (nanoseconds)
            this.actualFrequency = Frequency.getFrequencyFromNanos(period);
            return this.actualFrequency;
        } catch (java.io.IOException e) {
            logger.error(e.getMessage(), e);
            throw new IOException("Unable to read PWM [" + config.address() + "] period @ <" + pwm.pwmPath() + ">; " + e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pwm shutdownInternal(Context context) throws ShutdownException {
        logger.trace("shutdown PWM [{}]; {}", this.config.address(), pwm.getPwmPath());

        // --------------------------------------------------------------------------
        // [ATTENTION]
        // --------------------------------------------------------------------------
        // IF WE UN-EXPORT THE PWM PIN, ANY SHUTDOWN STATE WILL BE IGNORED AND
        // THE PWM PIN WILL DEFAULT TO A LOW STATE OR POTENTIALLY A FLOATING STATE
        // IF THE PIN IS DEFAULTED AS AN INPUT PIN AS A RESULT OF UN-EXPORTING IT.
        //
        // IF A VALID SHUTDOWN STATE HAS BEEN CONFIGURED FOR THIS PWM OUTPUT INSTANCE,
        // WE WILL LEAVE THE PWM PIN EXPORTED ON SHUTDOWN OF PI4J
        //
        // IF NO SHUTDOWN STATE HAS BEEN CONFIGURED FOR THIS PWM INSTANCE,
        // WE WILL UN-EXPORT THE PWM PIN ON SHUTDOWN OF PI4J
        // --------------------------------------------------------------------------

        // set pin state to shutdown state if a shutdown state is configured
        if (config().shutdownValue() != null) {
            return super.shutdownInternal(context);
        }

        // otherwise ... un-export the GPIO pin from the Linux file system impl
        try {
            logger.trace("un-exporting PWM [{}]; {}", this.config.address(), pwm.getPwmPath());
            if (pwm.isExported()) {
                pwm.unexport();
            }
        } catch (java.io.IOException e) {
            logger.error(e.getMessage(), e);
            throw new ShutdownException("Failed to UN-EXPORT PWM [" + config().address() + "] @ <" + pwm.systemPath() + ">; " + e.getMessage(), e);
        }

        // return this PWM instance
        return this;
    }

}

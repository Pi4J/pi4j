package com.pi4j.plugin.pigpio.provider.pwm;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: PLUGIN   :: PIGPIO I/O Providers
 * FILENAME      :  PiGpioPwmSoftware.java
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
import com.pi4j.io.exception.IOException;
import com.pi4j.io.pwm.Pwm;
import com.pi4j.io.pwm.PwmConfig;
import com.pi4j.io.pwm.PwmPolarity;
import com.pi4j.io.pwm.PwmProvider;
import com.pi4j.library.pigpio.PiGpio;
import com.pi4j.library.pigpio.PiGpioMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * <p>PiGpioPwmSoftware class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public class PiGpioPwmSoftware extends PiGpioPwmBase implements Pwm {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    // fixed range for software PWM
    /**
     * Constant <code>RANGE=255</code>
     */
    public static int RANGE = 255;

    /**
     * <p>Constructor for PiGpioPwmSoftware.</p>
     *
     * @param piGpio   a {@link PiGpio} object.
     * @param provider a {@link PwmProvider} object.
     * @param config   a {@link PwmConfig} object.
     */
    public PiGpioPwmSoftware(PiGpio piGpio, PwmProvider provider, PwmConfig config) {
        super(piGpio, provider, config, RANGE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pwm initialize(Context context) throws InitializeException {
        try {
            // inversed polarity is not supported
            if (config.polarity() != null) {
                if (config.polarity() == PwmPolarity.INVERSED) {
                    throw new IOException("<PIGPIO> INVERSED POLARITY UNSUPPORTED; PWM CHANNEL: " + this.getChannel());
                }
            }

            // set pin mode to output
            piGpio.gpioSetMode(this.getChannel(), PiGpioMode.OUTPUT);

            // set PWM range (to fixed range)
            piGpio.gpioSetPWMrange(this.getChannel(), RANGE);

            // get actual PWM frequency
            this.actualFrequency = piGpio.gpioGetPWMfrequency(this.getChannel());

            // get current frequency from config or from actual PWM pin
            if (config.frequency() != null) {
                this.frequency = config.frequency();
            } else {
                this.frequency = this.actualFrequency;
            }

            // get current duty-cycle from config or set to default 50%
            if (config.dutyCycle() != null) {
                this.dutyCycle = config.dutyCycle();
            } else {
                // get updated duty-cycle value from PiGpio
                this.dutyCycle = 50;  // default duty-cycle is 50% of total range
            }

            // apply an initial value if configured
            if (config.initialValue() != null) {
                try {
                    if (this.config.initialValue() <= 0) {
                        this.off();
                    } else {
                        this.on(this.config.initialValue());
                    }
                } catch (IOException e) {
                    throw new InitializeException(e);
                }
            }
        } catch (Exception e) {
            throw new InitializeException(e);
        }

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pwm on() throws IOException {
        try {
            // set PWM frequency; return actual frequency
            this.actualFrequency = piGpio.gpioSetPWMfrequency(this.getChannel(), frequency);

            // set PWM duty-cycle and enable PWM
            piGpio.gpioPWM(this.getChannel(), calculateActualDutyCycle(this.dutyCycle));

            // update tracking state
            this.onState = (this.frequency > 0 && this.dutyCycle > 0);
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
            // set PWM duty-cycle and enable PWM
            piGpio.gpioPWM(this.getChannel(), 0);

            // update tracking state
            this.onState = false;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new IOException(e);
        }
        return this;
    }
}

package com.pi4j.plugin.pigpio.provider.pwm;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: PLUGIN   :: PIGPIO I/O Providers
 * FILENAME      :  PiGpioPwmHardware.java
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
 * <p>PiGpioPwmHardware class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public class PiGpioPwmHardware extends PiGpioPwmBase implements Pwm {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    private boolean initializing = false;

    // fixed range for hardware PWM
    /**
     * Constant <code>RANGE=1000000</code>
     */
    public static int RANGE = 1000000;

    /**
     * <p>Constructor for PiGpioPwmHardware.</p>
     *
     * @param piGpio   a {@link com.pi4j.library.pigpio.PiGpio} object.
     * @param provider a {@link com.pi4j.io.pwm.PwmProvider} object.
     * @param config   a {@link com.pi4j.io.pwm.PwmConfig} object.
     */
    public PiGpioPwmHardware(PiGpio piGpio, PwmProvider provider, PwmConfig config) {
        super(piGpio, provider, config, RANGE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pwm initialize(Context context) throws InitializeException {
        try {
            initializing = true;

            // TODO :: SET PIN ALT MODES FOR HARDWARE PWM ON COMPUTE MODULE
            //  12  PWM channel 0  All models but A and B
            //  13  PWM channel 1  All models but A and B
            //  18  PWM channel 0  All models
            //  19  PWM channel 1  All models but A and B
            //  40  PWM channel 0  Compute module only
            //  41  PWM channel 1  Compute module only
            //  45  PWM channel 1  Compute module only
            //  52  PWM channel 0  Compute module only
            //  53  PWM channel 1  Compute module only

            if (this.config().address() == 12 || this.config().address() == 13 || this.config().address() == 41 || this.config().address() == 42 || this.config().address() == 45) {
                piGpio.gpioSetMode(this.config().address(), PiGpioMode.ALT0);
            } else if (this.config().address() == 18 || this.config().address() == 19) {
                piGpio.gpioSetMode(this.config().address(), PiGpioMode.ALT0);
            }
//            else{
//                throw new IOException("<PIGPIO> UNSUPPORTED HARDWARE PWM PIN: " + this.address());
//            }

            // inversed polarity is not supported
            if (config.polarity() != null) {
                if (config.polarity() == PwmPolarity.INVERSED) {
                    throw new IOException("<PIGPIO> INVERSED POLARITY UNSUPPORTED; PWM PIN: " + this.config().address());
                }
            }

            // set pin mode to output
            piGpio.gpioSetMode(this.config().address(), PiGpioMode.OUTPUT);

            // get actual PWM frequency
            this.actualFrequency = piGpio.gpioGetPWMfrequency(this.config().address());

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

            // done initializing
            initializing = false;
        } catch (Exception e) {
            initializing = false;
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
            // set PWM frequency & duty-cycle; enable PWM signal
            piGpio.gpioHardwarePWM(this.config().address(), this.frequency, calculateActualDutyCycle(this.dutyCycle));

            // get actual PWM frequency
            this.actualFrequency = piGpio.gpioGetPWMfrequency(this.config().address());

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
            // This is a hack to get the hardware PWM signal to stop if an initial
            // value was configured.  It seems PIPGIO must actually perform a
            // change value to apply a "ZERO" frequency & duty-cycle after
            // the library is first initialized and the PWM was not active
            if (initializing) {
                piGpio.gpioHardwarePWM(this.config().address(), 1, 1);
            }

            // set PWM duty-cycle and enable PWM
            piGpio.gpioHardwarePWM(this.config().address(), 0, 0);

            // update tracking state
            this.onState = false;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new IOException(e);
        }
        return this;
    }
}

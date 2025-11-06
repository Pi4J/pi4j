package com.pi4j.plugin.pigpio.provider.gpio.digital;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: PLUGIN   :: PIGPIO I/O Providers
 * FILENAME      :  PiGpioDigitalOutput.java
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
import com.pi4j.io.gpio.digital.*;
import com.pi4j.library.pigpio.PiGpio;
import com.pi4j.library.pigpio.PiGpioException;
import com.pi4j.library.pigpio.PiGpioMode;
import com.pi4j.library.pigpio.PiGpioState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>PiGpioDigitalOutput class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public class PiGpioDigitalOutput extends DigitalOutputBase implements DigitalOutput {
    private final PiGpio piGpio;
    private final int pin;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * <p>Constructor for PiGpioDigitalOutput.</p>
     *
     * @param piGpio   a {@link com.pi4j.library.pigpio.PiGpio} object.
     * @param provider a {@link com.pi4j.io.gpio.digital.DigitalOutputProvider} object.
     * @param config   a {@link com.pi4j.io.gpio.digital.DigitalOutputConfig} object.
     */
    public PiGpioDigitalOutput(PiGpio piGpio, DigitalOutputProvider provider, DigitalOutputConfig config) {
        super(provider, config);
        this.piGpio = piGpio;
        this.pin = config.bcm().intValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DigitalOutput initialize(Context context) throws InitializeException {
        super.initialize(context);
        try {
            // configure GPIO pin as an OUTPUT pin
            this.piGpio.gpioSetMode(pin, PiGpioMode.OUTPUT);
        } catch (PiGpioException e) {
            logger.error(e.getMessage(), e);
            throw new InitializeException(e);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DigitalOutput state(DigitalState state) throws IOException {
        try {
            this.piGpio.gpioWrite(pin, PiGpioState.from(state.value()));
        } catch (PiGpioException e) {
            logger.error(e.getMessage(), e);
            throw new IOException(e.getMessage(), e);
        }
        return super.state(state);
    }
}

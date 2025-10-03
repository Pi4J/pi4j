package com.pi4j.plugin.pigpio.provider.gpio.digital;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: PLUGIN   :: PIGPIO I/O Providers
 * FILENAME      :  PiGpioDigitalInput.java
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
import com.pi4j.io.gpio.digital.*;
import com.pi4j.library.pigpio.PiGpio;
import com.pi4j.library.pigpio.PiGpioMode;
import com.pi4j.library.pigpio.PiGpioPud;
import com.pi4j.library.pigpio.PiGpioStateChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>PiGpioDigitalInput class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public class PiGpioDigitalInput extends DigitalInputBase implements DigitalInput {
    private final PiGpio piGpio;
    private final int pin;
    private DigitalState state = DigitalState.LOW;
    private Logger logger = LoggerFactory.getLogger(this.getClass());


    /**
     * Default Constructor
     *
     * @param piGpio a {@link com.pi4j.library.pigpio.PiGpio} object.
     * @param provider a {@link com.pi4j.io.gpio.digital.DigitalInputProvider} object.
     * @param config a {@link com.pi4j.io.gpio.digital.DigitalInputConfig} object.
     */
    public PiGpioDigitalInput(PiGpio piGpio, DigitalInputProvider provider, DigitalInputConfig config) {
        super(provider, config);
        this.piGpio = piGpio;
        this.pin = config.address().intValue();
    }

    /**
     * PIGPIO Pin Change Event Handler
     * <p>
     * This listener implementation will forward pin change events received from PIGPIO
     * to registered Pi4J 'DigitalChangeEvent' event listeners on this digital pin.
     */
    private PiGpioStateChangeListener piGpioPinListener =
            event -> dispatch(new DigitalStateChangeEvent(PiGpioDigitalInput.this, DigitalState.getState(event.state().value())));

    /** {@inheritDoc} */
    @Override
    public DigitalInput initialize(Context context) throws InitializeException {
        super.initialize(context);

        // configure GPIO pin as an INPUT pin
        this.piGpio.gpioSetMode(pin, PiGpioMode.INPUT);

        // if configured, set GPIO pin pull resistance
        switch(config.pull()){
            case PULL_DOWN:{
                this.piGpio.gpioSetPullUpDown(pin, PiGpioPud.DOWN);
                break;
            }
            case PULL_UP:{
                this.piGpio.gpioSetPullUpDown(pin, PiGpioPud.UP);
                break;
            }
        }

        // if configured, set GPIO debounce
        if(this.config.debounce() != null) {
            int steadyInterval = 0;
            if(this.config.debounce() > 300000){
                steadyInterval = 300000;
            } else{
                steadyInterval = this.config.debounce().intValue();
            }
            this.piGpio.gpioNoiseFilter(pin, 0, 0);
            this.piGpio.gpioGlitchFilter(pin, steadyInterval);
        }

        // add this pin listener
        this.piGpio.addPinListener(pin, piGpioPinListener);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public DigitalState state() {
        try {
            switch (this.piGpio.gpioRead(pin)) {
                case LOW: {
                    this.state = DigitalState.LOW;
                    break;
                }
                case HIGH: {
                    this.state = DigitalState.HIGH;
                    break;
                }
                default: {
                    this.state = DigitalState.UNKNOWN;
                    break;
                }
            }
            return this.state;
        } catch (Exception e){
            logger.error(e.getMessage(), e);
            return DigitalState.UNKNOWN;
        }
    }

    /** {@inheritDoc} */
    @Override
    public DigitalInput shutdownInternal(Context context) throws ShutdownException {
        // remove this pin listener
        this.piGpio.removePinListener(pin, piGpioPinListener);
        return super.shutdownInternal(context);
    }
}

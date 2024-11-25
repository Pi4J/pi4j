package com.pi4j.plugin.gpiod.provider.gpio.digital;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: PLUGIN   :: PIGPIO I/O Providers
 * FILENAME      :  PiGpioDigitalOutputProviderImpl.java
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
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalOutputConfig;
import com.pi4j.io.gpio.digital.DigitalOutputProvider;
import com.pi4j.io.gpio.digital.DigitalOutputProviderBase;
import com.pi4j.library.gpiod.internal.GpioDContext;
import com.pi4j.library.gpiod.internal.GpioLine;

/**
 * <p>PiGpioDigitalOutputProviderImpl class.</p>
 *
 * @author Alexander Liggesmeyer (<a href="https://alexander.liggesmeyer.net/">https://alexander.liggesmeyer.net/</a>)
 * @version $Id: $Id
 */
public class GpioDDigitalOutputProviderImpl extends DigitalOutputProviderBase implements GpioDDigitalOutputProvider {

    /**
     * <p>Constructor for PiGpioDigitalOutputProviderImpl.</p>
     */
    public GpioDDigitalOutputProviderImpl() {
        this.id = ID;
        this.name = NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DigitalOutput create(DigitalOutputConfig config) {
        // create new I/O instance based on I/O config
        GpioLine line = GpioDContext.getInstance().getOrOpenLine(config.address());
        GpioDDigitalOutput digitalOutput = new GpioDDigitalOutput(line, this, config);
        this.context.registry().add(digitalOutput);
        return digitalOutput;
    }

    @Override
    public int getPriority() {
        // the gpioD driver should be higher priority always
        return 150;
    }

    @Override
    public DigitalOutputProvider initialize(Context context) throws InitializeException {
        DigitalOutputProvider provider = super.initialize(context);
        GpioDContext.getInstance().initialize();
        return provider;
    }

    @Override
    public DigitalOutputProvider shutdown(Context context) throws ShutdownException {
        GpioDContext.getInstance().close();
        return super.shutdown(context);
    }
}

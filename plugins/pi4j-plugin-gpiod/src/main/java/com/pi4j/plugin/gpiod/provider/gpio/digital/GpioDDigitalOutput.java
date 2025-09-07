package com.pi4j.plugin.gpiod.provider.gpio.digital;

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
import com.pi4j.exception.ShutdownException;
import com.pi4j.io.exception.IOException;
import com.pi4j.io.gpio.digital.*;
import com.pi4j.library.gpiod.internal.GpioDContext;
import com.pi4j.library.gpiod.internal.GpioDException;
import com.pi4j.library.gpiod.internal.GpioLine;
import com.pi4j.library.gpiod.internal.LineDirection;

/**
 * <p>PiGpioDigitalOutput class.</p>
 *
 * @author Alexander Liggesmeyer (<a href="https://alexander.liggesmeyer.net/">https://alexander.liggesmeyer.net/</a>)
 * @version $Id: $Id
 */
public class GpioDDigitalOutput extends DigitalOutputBase implements DigitalOutput {
    private final GpioLine line;

    /**
     * <p>Constructor for GpioDDigitalOutput.</p>
     *
     * @param line     a {@link com.pi4j.library.gpiod.internal.GpioLine} object.
     * @param provider a {@link DigitalOutputProvider} object.
     * @param config   a {@link DigitalOutputConfig} object.
     */
    public GpioDDigitalOutput(GpioLine line, DigitalOutputProvider provider, DigitalOutputConfig config) {
        super(provider, config);
        this.line = line;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DigitalOutput initialize(Context context) throws InitializeException {
        try {
            int initialState;
            if (this.config.getInitialState() == null)
                initialState = DigitalState.LOW.value().intValue();
            else
                initialState = this.config.initialState().value().intValue();
            if (this.line.getDirection() == LineDirection.INPUT)
                GpioDContext.getInstance().closeLine(this.line);
            this.line.requestOutput(this.config.getId(), initialState);
        } catch (GpioDException e) {
            throw new InitializeException("Failed to initialize output " + this.id, e);
        }
        super.initialize(context);
        return this;
    }

    @Override
    public DigitalOutput shutdown(Context context) throws ShutdownException {
        super.shutdown(context);
        this.line.close();
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DigitalOutput state(DigitalState state) throws IOException {
        try {
            this.line.setValue(state.value().intValue());
        } catch (GpioDException e) {
            throw new IOException("Failed to set state for output " + this.id + " to " + state, e);
        }
        return super.state(state);
    }
}

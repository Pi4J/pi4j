package com.pi4j.library.pigpio;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: JNI Wrapper for PIGPIO Library
 * FILENAME      :  PiGpioStateChangeEvent.java
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

/**
 * <p>PiGpioStateChangeEvent class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public class PiGpioStateChangeEvent {

    protected final PiGpioState state;
    protected final int pin;
    protected final long tick;

    /**
     * Default constructor
     *
     * @param state the value changed for this event instance
     * @param pin   a int.
     * @param tick  a long.
     */
    public PiGpioStateChangeEvent(final int pin,
                                  final PiGpioState state,
                                  final long tick) {
        this.state = state;
        this.pin = pin;
        this.tick = tick;
    }

    /**
     * The new state of this event
     *
     * @return a {@link com.pi4j.library.pigpio.PiGpioState} object.
     */
    public PiGpioState state() {
        return this.state;
    }

    /**
     * The pin number for this event
     *
     * @return a int.
     */
    public int pin() {
        return this.pin;
    }

    /**
     * The tick time for this event
     *
     * @return a long.
     */
    public long tick() {
        return this.tick;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("<<STATE CHANGE EVENT>> PIN [");
        result.append(this.pin());
        result.append("] STATE: [");
        result.append(this.state().name());
        result.append("] (TICK=");
        result.append(this.tick());
        result.append(")");
        return result.toString();
    }
}

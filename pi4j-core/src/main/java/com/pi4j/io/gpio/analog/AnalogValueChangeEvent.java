package com.pi4j.io.gpio.analog;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  AnalogValueChangeEvent.java
 *
 * This file is part of the Pi4J project. More information about
 * this project can be found here:  https://pi4j.com/
 * **********************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


/**
 * <p>AnalogChangeEvent class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 * @param <ANALOG_TYPE>
 */
public class AnalogValueChangeEvent<ANALOG_TYPE extends Analog> implements AnalogEvent {

    // internal event copy of the changed analog values and analog I/O source
    protected Integer oldValue;
    protected Integer value;
    protected ANALOG_TYPE source;

    /**
     * Default constructor
     *
     * @param newValue the value changed for this event instance
     * @param source a ANALOG_TYPE object.
     * @param oldValue a {@link java.lang.Integer} object.
     */
    public AnalogValueChangeEvent(ANALOG_TYPE source, Integer newValue, Integer oldValue){
        this.value = newValue; // cache a copy of the event instance new value
        this.oldValue = oldValue; // cache a copy of the event instance old value
        this.source = source; // cache analog I/O source
    }

    /**
     * The old/prior value change for this event instance
     *
     * @return a {@link java.lang.Integer} object.
     */
    public Integer oldValue() {
        return this.oldValue;
    }

    /**
     * The current/new value change for this event instance
     *
     * @return a {@link java.lang.Integer} object.
     */
    public Integer value() {
        return this.value;
    }

    /** {@inheritDoc} */
    @Override
    public ANALOG_TYPE source() {
        return this.source;
    }

    /** {@inheritDoc} */
    @Override
    public String toString(){

        StringBuilder result = new StringBuilder();
        result.append("<<ANALOG CHANGE EVENT>> [");
        result.append(source());
        result.append("] VALUE: [");
        result.append(this.oldValue().toString());
        result.append(" -> ");
        result.append(this.value().toString());
        result.append("]");
        return result.toString();
    }
}

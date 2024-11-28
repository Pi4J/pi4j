package com.pi4j.io.gpio.digital;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  DigitalStateChangeEvent.java
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
 * <p>DigitalChangeEvent class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 * @param <DIGITAL_TYPE>
 */
public class DigitalStateChangeEvent<DIGITAL_TYPE extends Digital> implements DigitalEvent {

    // internal event copy of the changed digital state
    protected DigitalState state;

    protected DIGITAL_TYPE source;

    /**
     * Default constructor
     *
     * @param state the value changed for this event instance
     * @param source a DIGITAL_TYPE object.
     */
    public DigitalStateChangeEvent(DIGITAL_TYPE source, DigitalState state){
        this.state = state; // cache a copy of the event instance state
        this.source = source; // cache digital I/O source
    }

    /**
     * The value change for this event instance
     *
     * @return a {@link com.pi4j.io.gpio.digital.DigitalState} object.
     */
    public DigitalState state() {
        return this.state;
    }

    /** {@inheritDoc} */
    @Override
    public DIGITAL_TYPE source() {
        return this.source;
    }


    /** {@inheritDoc} */
    @Override
    public String toString(){
        StringBuilder result = new StringBuilder();
        result.append("<<DIGITAL CHANGE EVENT>> [");
        result.append(source());
        result.append("] STATE: [");
        result.append(DigitalState.getInverseState(this.state()));
        result.append(" -> ");
        result.append(this.state());
        result.append("]");
        return result.toString();
    }
}

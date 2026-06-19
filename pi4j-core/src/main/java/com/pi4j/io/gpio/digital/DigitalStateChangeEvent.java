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
 * Event fired when the {@link DigitalState} of a digital I/O instance changes, delivered to registered
 * {@link DigitalStateChangeListener}s. It carries the I/O source that changed and the new state.
 *
 * @param <DIGITAL_TYPE> the type of the {@link Digital} I/O source that produced the event
 */
public class DigitalStateChangeEvent<DIGITAL_TYPE extends Digital> implements DigitalEvent {

    // internal event copy of the changed digital state
    protected DigitalState state;

    protected DIGITAL_TYPE source;

    /**
     * Creates a new state-change event.
     *
     * @param source the digital I/O instance whose state changed
     * @param state  the new digital state after the change
     */
    public DigitalStateChangeEvent(DIGITAL_TYPE source, DigitalState state){
        this.state = state; // cache a copy of the event instance state
        this.source = source; // cache digital I/O source
    }

    /**
     * Returns the new state recorded by this event.
     *
     * @return the digital state after the change
     */
    public DigitalState state() {
        return this.state;
    }

    @Override
    public DIGITAL_TYPE source() {
        return this.source;
    }


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

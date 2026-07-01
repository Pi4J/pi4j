package com.pi4j.io.gpio.digital;



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

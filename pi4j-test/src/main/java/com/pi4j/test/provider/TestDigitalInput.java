package com.pi4j.test.provider;

import com.pi4j.io.gpio.digital.*;

public class TestDigitalInput extends DigitalInputBase implements DigitalInput {

    private DigitalState state = DigitalState.UNKNOWN;

    @Override
    public DigitalState state() {
        return this.state;
    }

    public TestDigitalInput(DigitalInputProvider provider, DigitalInputConfig config){
        super(provider, config);
    }

    public TestDigitalInput test(DigitalState state){

        // check to see of there is a state change; if there is then we need
        // to update the internal value variable and dispatch the change event
        if(this.state().equals(state)) {

            // update current/new value
            this.state = state;

            // dispatch value change event
            this.dispatch(new DigitalStateChangeEvent(this, this.state()));
        }
        return this;
    }
}

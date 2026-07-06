package com.pi4j.io.pwm.impl;

import com.pi4j.io.pwm.PwmPreset;

public class DefaultPwmPreset  implements PwmPreset {

    protected final String name;
    protected final Integer dutyCycle;
    protected final Integer frequency;

    public DefaultPwmPreset(String name, Integer dutyCycle){
        this.name = name.toLowerCase().trim();
        this.dutyCycle = dutyCycle;
        this.frequency = null;
    }

    public DefaultPwmPreset(String name, Integer dutyCycle, Integer frequency){
        this.name = name.toLowerCase().trim();

        // bounds check the duty-cycle value
        if(dutyCycle != null) {
            Integer dc = dutyCycle;
            if (dc < 0) dc = 0;
            if (dc > 100) dc = 100;
            this.dutyCycle = dc;
        } else {
            this.dutyCycle = null;
        }
        this.frequency = frequency;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public Integer dutyCycle() {
        return this.dutyCycle;
    }

    @Override
    public Integer frequency() {
        return this.frequency;
    }
}

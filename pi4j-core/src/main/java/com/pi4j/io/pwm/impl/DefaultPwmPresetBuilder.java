package com.pi4j.io.pwm.impl;

import com.pi4j.io.pwm.PwmPreset;
import com.pi4j.io.pwm.PwmPresetBuilder;

public class DefaultPwmPresetBuilder implements PwmPresetBuilder{
    protected Integer dutyCycle = null;
    protected Integer frequency = null;
    protected final String name;

    /**
     * PRIVATE CONSTRUCTOR
     */
    protected DefaultPwmPresetBuilder(String name){
        super(); this.name = name;
    }

    /**
     * Creates a new instance of {@link DefaultPwmPresetBuilder} with the specified name.
     *
     * @param name the name of the PWM preset
     * @return a new {@link PwmPresetBuilder} instance
     */
    public static PwmPresetBuilder newInstance(String name) {
        return new DefaultPwmPresetBuilder(name);
    }

    @Override
    public PwmPresetBuilder frequency(Integer frequency) {
        this.frequency = frequency;
        return this;
    }

    @Override
    public PwmPresetBuilder dutyCycle(Integer dutyCycle) {
        Integer dc = dutyCycle ;

        // bounds check the duty-cycle value
        if(dc < 0) dc = 0;
        if(dc > 100) dc = 100;

        this.dutyCycle = dc;
        return this;
    }
    @Override
    public PwmPreset build() {
        return new DefaultPwmPreset(this.name, this.dutyCycle, this.frequency);
    }
}

package com.pi4j.test.smoketest;

import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.gpio.digital.PullResistance;

public class TestCase {

    protected static final int ID_VALUE_MSK_BMP = 0x58;   // expected chpId value BMP28
    protected static final int ID_VALUE_MSK_BME = 0x60;   // expected chpId value BME280

    protected static DigitalInput createDigitalInput(Context pi4j, int bcm, PullResistance pull, long debounceTime) {
        var inputConfig3 = DigitalInput.newConfigBuilder(pi4j)
            .bcm(bcm)
            .pull(pull)
            .debounce(debounceTime);
        return pi4j.create(inputConfig3);
    }

    protected static DigitalOutput createDigitalOutput(Context pi4j, int bcm, DigitalState initial, DigitalState shutDown) {
        var outputConfig3 = DigitalOutput.newConfigBuilder(pi4j)
            .bcm(bcm)
            .initial(initial)
            .shutdown(shutDown);
        return pi4j.create(outputConfig3);
    }
}

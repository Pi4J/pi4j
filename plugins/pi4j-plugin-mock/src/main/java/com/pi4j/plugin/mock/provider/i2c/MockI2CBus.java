package com.pi4j.plugin.mock.provider.i2c;

import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CBusBase;
import com.pi4j.io.i2c.I2CConfig;

import java.util.concurrent.Callable;

public class MockI2CBus extends I2CBusBase {

    public MockI2CBus(I2CConfig config) {
        super(config);
    }

    @Override
    public <R> R execute(I2C i2c, Callable<R> action) {
        return _execute(i2c, action);
    }
}

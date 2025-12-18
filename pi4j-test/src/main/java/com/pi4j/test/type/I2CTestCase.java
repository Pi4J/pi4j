package com.pi4j.test.type;

import com.pi4j.io.i2c.I2C;
import com.pi4j.test.ProviderContext;
import com.pi4j.test.TestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class I2CTestCase extends TestCase {

    private static final Logger logger = LoggerFactory.getLogger(I2CTestCase.class);

    private static final int BMP_I2C_BUS = 1;
    private static final int BMP_I2C_ADDR = 0x76;

    public static TestResult run(ProviderContext providerContext) {
        var identifier = Integer.toHexString(BMP_I2C_ADDR) + "-BMP280";

        var i2cDeviceConfig = I2C
            .newConfigBuilder(providerContext.getContext())
            .bus(BMP_I2C_BUS)
            .device(BMP_I2C_ADDR)
            .id(identifier)
            .name(identifier)
            .build();
        var i2c = providerContext.getContext().create(i2cDeviceConfig);

        // read 0xD0 validate data equal 0x58 or 0x60
        int id = i2c.readRegister(0xD0);

        if (id == ID_VALUE_MSK_BMP || id == ID_VALUE_MSK_BME) {
            return new TestResult("I2C", true, "Expected value found");
        } else {
            return new TestResult("I2C", false, "Value is not what was expected: " + id);
        }
    }
}

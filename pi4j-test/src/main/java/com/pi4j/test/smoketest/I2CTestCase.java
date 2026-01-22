package com.pi4j.test.smoketest;

import com.pi4j.io.i2c.I2C;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class I2CTestCase extends TestCase {

    private static final Logger logger = LoggerFactory.getLogger(I2CTestCase.class);

    private static final String TEST_NAME = "I2C";

    private static final int BMP_I2C_BUS = 1;
    private static final int BMP_I2C_ADDR = 0x76;

    public static TestResult run(ProviderContext providerContext) {
        I2C i2c = null;

        try {
            // Initialize the device
            var identifier = Integer.toHexString(BMP_I2C_ADDR) + "-BMP280";

            var i2cDeviceConfig = I2C
                .newConfigBuilder(providerContext.getContext())
                .bus(BMP_I2C_BUS)
                .device(BMP_I2C_ADDR)
                .id(identifier)
                .name(identifier)
                .build();
            i2c = providerContext.getContext().create(i2cDeviceConfig);

            // Read data from 0xD0 and check if the expected value is received
            int id = i2c.readRegister(0xD0);
            logger.info("Device ID read: 0x{}, expected: 0x{} or 0x{}",
                Integer.toHexString(id),
                Integer.toHexString(ID_VALUE_MSK_BMP),
                Integer.toHexString(ID_VALUE_MSK_BME));
            if (id == ID_VALUE_MSK_BMP || id == ID_VALUE_MSK_BME) {
                return new TestResult(TEST_NAME, true, "Expected value found");
            } else {
                return new TestResult(TEST_NAME, false, "Value is not what was expected: 0x" + Integer.toHexString(id));
            }
        } catch (Exception e) {
            return new TestResult(TEST_NAME, false, "Test failure: " + e.getMessage());
        } finally {
            if (i2c != null) {
                i2c.close();
            }
        }
    }
}

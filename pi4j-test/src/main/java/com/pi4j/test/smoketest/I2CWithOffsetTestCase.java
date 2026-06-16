package com.pi4j.test.smoketest;

import com.pi4j.io.i2c.I2C;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class I2CWithOffsetTestCase extends TestCase {

    private static final Logger logger = LoggerFactory.getLogger(I2CWithOffsetTestCase.class);

    private static final String TEST_NAME = "I2C with Offset";

    private static final int BMP_I2C_BUS = 1;
    private static final int BMP_I2C_ADDR = 0x76;

    public static TestResult run(ProviderContext providerContext) {
        logger.info("Starting I2C with offset test");

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

            // Read data from 0xD0 with offset parameter
            byte[] register = new byte[]{0x00, 0x00, (byte) 0xd0};
            byte[] readData = new byte[7];
            i2c.writeThenRead(register, 2, 1, 0, readData, 3, 1);
            byte idFromReadThenWriteWithOffset = readData[3];

            logger.info("Device ID read with offset: 0x{}, expected: 0x{} or 0x{}",
                Integer.toHexString(idFromReadThenWriteWithOffset),
                Integer.toHexString(ID_VALUE_MSK_BMP),
                Integer.toHexString(ID_VALUE_MSK_BME));
            if (idFromReadThenWriteWithOffset == ID_VALUE_MSK_BMP || idFromReadThenWriteWithOffset == ID_VALUE_MSK_BME) {
                return new TestResult(TEST_NAME, true, "Expected value found");
            } else {
                return new TestResult(TEST_NAME, false, "Value is not what was expected: "
                    + Integer.toHexString(idFromReadThenWriteWithOffset) + "/"
                    + Integer.toHexString(ID_VALUE_MSK_BMP) + "/"
                    + Integer.toHexString(ID_VALUE_MSK_BME));
            }

        } catch (Exception e) {
            logger.error("Test failure", e);
            return new TestResult(TEST_NAME, false, "Test failure: " + e.getMessage());
        } finally {
            if (i2c != null) {
                i2c.close();
                providerContext.getContext().shutdown(i2c.id());
            }
        }
    }
}

package com.pi4j.test.smoketest;

import com.pi4j.io.spi.Spi;
import com.pi4j.io.spi.SpiBus;
import com.pi4j.io.spi.SpiMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpiTestCase extends TestCase {

    private static final Logger logger = LoggerFactory.getLogger(SpiTestCase.class);

    private static final String TEST_NAME = "SPI";

    public static TestResult run(ProviderContext providerContext) {
        logger.info("Starting SPI test");

        Spi spi = null;

        try {
            // Initialize the device
            SpiBus bmpSpiBus = SpiBus.BUS_0;
            int chipId = 0xD0;
            var spiConfig = Spi
                .newConfigBuilder(providerContext.getContext())
                .id("SPI" + bmpSpiBus + "_BMP280")
                .name("Sensor")
                .bus(bmpSpiBus)
                .channel(0)
                .baud(Spi.DEFAULT_BAUD)    // Max 10MHz
                .mode(SpiMode.MODE_0)
                .build();
            spi = providerContext.getContext().create(spiConfig);

            Thread.sleep(100);

            // Read data from 0xD0 and check if the expected value is received
             byte idWriteThenRead = readSpiRegisterUsingWriteThenRead(spi, chipId);

            logger.info("Device ID read: 0x{}, expected: 0x{} or 0x{}",
                Integer.toHexString(idWriteThenRead),
                Integer.toHexString(ID_VALUE_MSK_BMP),
                Integer.toHexString(ID_VALUE_MSK_BME));
            if (idWriteThenRead == ID_VALUE_MSK_BMP || idWriteThenRead == ID_VALUE_MSK_BME) {
                return new TestResult(TEST_NAME, true, "Expected value found");
            } else {
                return new TestResult(TEST_NAME, false, "Value is not what was expected: "
                    + Integer.toHexString(idWriteThenRead) + "/"
                    + Integer.toHexString(ID_VALUE_MSK_BMP) + "/"
                    + Integer.toHexString(ID_VALUE_MSK_BME));
            }
        } catch (Exception e) {
            logger.error("Test failure", e);
            return new TestResult(TEST_NAME, false, "Test failure: " + e.getMessage());
        } finally {
            if (spi != null) {
                spi.close();
            }

        }
    }


    private static byte readSpiRegisterUsingWriteThenRead(Spi spi, int register) {
        byte[] data = new byte[]{(byte) (0b10000000 | register)};
        byte[] value = new byte[1];
        spi.writeThenRead(data, 0, value);
        return value[0];
    }
}

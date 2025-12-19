package com.pi4j.test.smoketest;

import com.pi4j.io.spi.Spi;
import com.pi4j.io.spi.SpiBus;
import com.pi4j.io.spi.SpiChipSelect;
import com.pi4j.io.spi.SpiMode;

public class SpiTestCase extends TestCase {

    private static final String TEST_NAME = "SPI";

    public static TestResult run(ProviderContext providerContext) {
        if (providerContext.getTestProvider() == ProviderContext.TestProvider.FFM) {
            return new TestResult("SPI", false, "Not implemented for FFM");
        }

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
                .chipSelect(SpiChipSelect.CS_0)
                .baud(Spi.DEFAULT_BAUD)    // Max 10MHz
                .mode(SpiMode.MODE_0)
                .build();
            spi = providerContext.getContext().create(spiConfig);

            Thread.sleep(100);

            // Read data from 0xD0 and check if the expected value is received
            int id = readSpiRegister(spi, chipId);
            if (id == ID_VALUE_MSK_BMP || id == ID_VALUE_MSK_BME) {
                return new TestResult(TEST_NAME, true, "Expected value found");
            } else {
                return new TestResult(TEST_NAME, false, "Value is not what was expected: " + id);
            }
        } catch (Exception e) {
            return new TestResult(TEST_NAME, false, "Test failure: " + e.getMessage());
        } finally {
            if (spi != null) {
                spi.close();
            }
        }
    }

    private static int readSpiRegister(Spi spi, int register) {
        byte[] data = new byte[]{(byte) (0b10000000 | register)};
        byte[] value = new byte[1];
        spi.writeThenRead(data, value);
        return value[0];
    }
}

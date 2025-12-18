package com.pi4j.test.type;

import com.pi4j.io.spi.Spi;
import com.pi4j.io.spi.SpiBus;
import com.pi4j.io.spi.SpiChipSelect;
import com.pi4j.io.spi.SpiMode;
import com.pi4j.test.ProviderContext;
import com.pi4j.test.TestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpiTestCase extends TestCase {

    private static final Logger logger = LoggerFactory.getLogger(SpiTestCase.class);

    public static TestResult run(ProviderContext providerContext) {
        if (providerContext.getTestProvider() == ProviderContext.TestProvider.FFM) {
            logger.info("SKIP test, no interface in FFM");
            return new TestResult("SPI", false, "Not implemented for FFM");
        }

        // Create the device
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
        var spi = providerContext.getContext().create(spiConfig);

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int id = readSpiRegister(spi, chipId);
        logger.info("Exit: testSpi");
        if (id == ID_VALUE_MSK_BMP || id == ID_VALUE_MSK_BME) {
            return new TestResult("SPI", true, "Expected value found");
        } else {
            return new TestResult("SPI", false, "Value is not what was expected: " + id);
        }
    }

    private static int readSpiRegister(Spi spi, int register) {
        byte[] data = new byte[]{(byte) (0b10000000 | register)};
        byte[] value = new byte[1];
        spi.writeThenRead(data, value);
        return value[0];
    }
}

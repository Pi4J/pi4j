/*
 *
 *
 *  #%L
 *  **********************************************************************
 *  ORGANIZATION  :  Pi4J
 *  PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 *  FILENAME      :  SpiTestCaseWithOffset.java
 *
 *  This file is part of the Pi4J project. More information about
 *  this project can be found here:  https://pi4j.com/
 *  **********************************************************************
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  #L%
 *
 *
 */

package com.pi4j.test.smoketest;

import com.pi4j.io.spi.Spi;
import com.pi4j.io.spi.SpiBus;
import com.pi4j.io.spi.SpiMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpiWithOffsetTestCase  extends TestCase {

    private static final Logger logger = LoggerFactory.getLogger(SpiWithOffsetTestCase.class);

    private static final String TEST_NAME = "SPI with offset";

    public static TestResult run(ProviderContext providerContext) {
        logger.info("Starting SPI with offset test");

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
            byte idWriteThenRead = readSpiRegisterUsingWriteThenReadWithOffset(spi, chipId);

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
                // this is a workaround as spi.close() no longer removes the ID  runtime context
                providerContext.getContext().shutdown(spi.id());
                spi.close();
            }
        }
    }


    private static byte readSpiRegisterUsingWriteThenReadWithOffset(Spi spi, int register) {
         // Read data from 0xD0 with offset parameter
        byte[] writeData = new byte[]{ 0x00, 0x00, (byte) (0b10000000 | register)};
        byte[] readData = new byte[7];
        spi.writeThenRead(writeData, 2, 1, 0, readData, 3, 1);
        return  readData[3];

    }
}

package com.pi4j.test.io.spi;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: TESTING  :: Unit/Integration Tests
 * FILENAME      :  SpiRawDataTest.java
 *
 * This file is part of the Pi4J project. More information about
 * this project can be found here:  https://pi4j.com/
 * **********************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.exception.Pi4JException;
import com.pi4j.io.spi.Spi;
import com.pi4j.io.spi.SpiBus;
import com.pi4j.io.spi.SpiChipSelect;
import com.pi4j.io.spi.SpiMode;
import com.pi4j.util.StringUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(Lifecycle.PER_CLASS)
public class SpiRawDataTest {

    private static final Logger logger = LoggerFactory.getLogger(SpiRawDataTest.class);

    private Context pi4j;

    private static byte SAMPLE_BYTE = 0x0d;
    private static byte[] SAMPLE_BYTE_ARRAY = new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    private static byte[] SAMPLE_BUFFER_ARRAY = new byte[]{10, 11, 12, 13, 14, 15, 16, 17, 18, 19};
    private static ByteBuffer SAMPLE_BUFFER = ByteBuffer.wrap(SAMPLE_BUFFER_ARRAY);
    private static String SAMPLE_STRING = "Hello World!";

    @BeforeEach
    public void beforeTest() throws Pi4JException {
        // Initialize Pi4J with auto context
        // An auto context enabled AUTO-DETECT loading
        // which will load any detected Pi4J extension
        // libraries (Platforms and Providers) from the class path
        pi4j = Pi4J.newContextBuilder().autoDetectMockPlugins().autoDetectPlatforms().build();
    }

    @AfterEach
    public void afterTest() {
        try {
            pi4j.shutdown();
        } catch (Pi4JException e) { /* do nothing */ }
    }

    @Test
    public void testRawDataWriteRead() {

        // create SPI config
        var config = Spi.newConfigBuilder(pi4j)
            .id("my-spi")
            .name("My SPI")
            .channel(0x01)
            .bus(SpiBus.BUS_1)
            .mode(SpiMode.MODE_3)
            .build();

        // use try-with-resources to auto-close SPI when complete
        try (var spi = pi4j.spi().create(config)) {

            // ensure that the SPI instance is not null;
            assertNotNull(spi);

            // write a single byte to the raw SPI device (not to a register)
            spi.write(SAMPLE_BYTE);

            // write an array of data bytes to the raw SPI device (not to a register)
            spi.write(SAMPLE_BYTE_ARRAY);

            // write a buffer of data bytes to the raw SPI device (not to a register)
            spi.write(SAMPLE_BUFFER);

            // write a string of data to the raw SPI device (not to a register)
            spi.write(SAMPLE_STRING);

            // read single byte from the raw SPI device (not from a register)
            byte b = (byte) spi.read();
            assertEquals(SAMPLE_BYTE, b);

            // read an array of data bytes from the raw SPI device (not from a register)
            byte byteArray[] = new byte[SAMPLE_BYTE_ARRAY.length];
            spi.read(byteArray, 0, byteArray.length);
            assertArrayEquals(SAMPLE_BYTE_ARRAY, byteArray);

            // read a buffer of data bytes from the raw SPI device (not from a register)
            ByteBuffer buffer = ByteBuffer.allocate(SAMPLE_BUFFER.capacity());
            spi.read(buffer, 0, buffer.capacity());
            assertArrayEquals(SAMPLE_BUFFER_ARRAY, buffer.array());

            // read a string of data from the raw SPI device (not from a register)
            String testString = spi.readString(SAMPLE_STRING.length());
            assertEquals(SAMPLE_STRING, testString);
        }
    }

    @Test
    public void testRawDataStream() throws IOException {
        // create random set of sample data
        Random rand = new Random();
        byte sample[] = new byte[1024];
        rand.nextBytes(sample);

        // create SPI config
        var config = Spi.newConfigBuilder(pi4j)
            .id("my-spi")
            .name("My SPI")
            .chipSelect(SpiChipSelect.CS_0)
            .bus(SpiBus.BUS_1)
            .mode(SpiMode.MODE_3)
            .build();

        // use try-with-resources to auto-close SPI when complete
        try (var spi = pi4j.spi().create(config)) {

            // write sample data using output stream
            spi.out().write(sample);

            // read sample data using input stream
            byte[] result = spi.in().readNBytes(sample.length);

            logger.info("[SAMPLE DATA] - 0x{}", StringUtil.toHexString(sample));
            logger.info("[READ DATA  ] - 0x{}", StringUtil.toHexString(result));

            // compare sample data against returned read data
            assertArrayEquals(sample, result);
        }
    }
}

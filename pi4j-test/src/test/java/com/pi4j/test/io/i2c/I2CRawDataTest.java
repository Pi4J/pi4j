package com.pi4j.test.io.i2c;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: TESTING  :: Unit/Integration Tests
 * FILENAME      :  I2CRawDataTest.java
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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.exception.Pi4JException;
import com.pi4j.io.i2c.I2C;
import com.pi4j.util.StringUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Random;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@TestInstance(Lifecycle.PER_CLASS)
public class I2CRawDataTest {

    private static final Logger logger = LoggerFactory.getLogger(I2CRawDataTest.class);

    private Context pi4j;

    private static int I2C_BUS = 1;
    private static int I2C_DEVICE = 0x04;
    private static byte SAMPLE_BYTE = 0x0d;
    private static byte[] SAMPLE_BYTE_ARRAY = new byte[] { 0,1,2,3,4,5,6,7,8,9 };
    private static byte[] SAMPLE_BUFFER_ARRAY = new byte[] { 10,11,12,13,14,15,16,17,18,19 };
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

        // create I2C config
        var config  = I2C.newConfigBuilder(pi4j)
                .id("my-i2c-bus")
                .name("My I2C Bus")
                .bus(I2C_BUS)
                .device(I2C_DEVICE)
                .build();

        // use try-with-resources to auto-close I2C when complete
        try (var i2c = pi4j.i2c().create(config)) {

            // ensure that the I2C instance is not null;
            assertNotNull(i2c);

            // write a single byte to the raw I2C device (not to a register)
            i2c.write(SAMPLE_BYTE);

            // write an array of data bytes to the raw I2C device (not to a register)
            i2c.write(SAMPLE_BYTE_ARRAY);

            // write a buffer of data bytes to the raw I2C device (not to a register)
            i2c.write(SAMPLE_BUFFER);

            // write a string of data to the raw I2C device (not to a register)
            i2c.write(SAMPLE_STRING);

            // read single byte from the raw I2C device (not from a register)
            byte b = (byte)i2c.read();
            assertEquals(SAMPLE_BYTE, b);

            // read an array of data bytes from the raw I2C device (not from a register)
            byte byteArray[] = new byte[SAMPLE_BYTE_ARRAY.length];
            i2c.read(byteArray, 0, byteArray.length);
            assertArrayEquals(SAMPLE_BYTE_ARRAY, byteArray);

            // read a buffer of data bytes from the raw I2C device (not from a register)
            ByteBuffer buffer = ByteBuffer.allocate(SAMPLE_BUFFER.capacity());
            i2c.read(buffer, 0, buffer.capacity());
            assertArrayEquals(SAMPLE_BUFFER_ARRAY, buffer.array());

            // read a string of data from the raw I2C device (not from a register)
            String testString = i2c.readString(SAMPLE_STRING.length());
            assertEquals(SAMPLE_STRING, testString);
        }
    }

    @Test
    public void testRawDataStream() throws IOException {
        // create random set of sample data
        Random rand = new Random();
        byte sample[] = new byte[1024];
        rand.nextBytes(sample);

        // create I2C config
        var config  = I2C.newConfigBuilder(pi4j)
                .id("my-i2c-bus")
                .name("My I2C Bus")
                .bus(I2C_BUS)
                .device(I2C_DEVICE)
                .build();

        // use try-with-resources to auto-close I2C when complete
        try (var i2c = pi4j.i2c().create(config)) {

            // write sample data using output stream
            i2c.out().write(sample);

            // read sample data using input stream
            byte[] result = i2c.in().readNBytes(sample.length);

            logger.info("[SAMPLE DATA] - 0x{}", StringUtil.toHexString(sample));
            logger.info("[READ DATA  ] - 0x{}", StringUtil.toHexString(result));

            // copare sample data against returned read data
            assertArrayEquals(sample, result);
        }
    }
}

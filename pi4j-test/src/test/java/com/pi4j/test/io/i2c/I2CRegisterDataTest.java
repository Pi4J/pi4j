package com.pi4j.test.io.i2c;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: TESTING  :: Unit/Integration Tests
 * FILENAME      :  I2CRegisterDataTest.java
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
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CRegister;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(Lifecycle.PER_CLASS)
public class I2CRegisterDataTest {

    private static final int I2C_BUS = 1;
    private static final int I2C_DEVICE = 0x04;
    private static final int NUMBER_OF_REGISTERS = 100;

    private Context pi4j;
    private final List<TestData> samples = new ArrayList<>();
    private final List<I2CRegister> registers = new ArrayList<>();

    private static final byte SAMPLE_BYTE = 0x0d;
    private static final byte[] SAMPLE_BYTE_ARRAY = new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    private static final byte[] SAMPLE_BUFFER_ARRAY = new byte[]{10, 11, 12, 13, 14, 15, 16, 17, 18, 19};
    private static final ByteBuffer SAMPLE_BUFFER = ByteBuffer.wrap(SAMPLE_BUFFER_ARRAY);
    private static final String SAMPLE_STRING = "Hello World!";

    private static final int SIZE_BIG_ARRAY = 0x1ff;


    @BeforeAll
    public void beforeTest() throws Pi4JException {
        // Initialize Pi4J with auto context
        // An auto context enabled AUTO-DETECT loading
        // which will load any detected Pi4J extension
        // libraries (Platforms and Providers) from the class path
        pi4j = Pi4J.newContextBuilder().autoDetectMockPlugins().autoDetectPlatforms().build();
    }

    @AfterAll
    public void afterTest() {
        try {
            pi4j.shutdown();
        } catch (Pi4JException e) { /* do nothing */ }
    }

    public class TestData {
        public final byte byt;
        public final int word;
        public final byte[] array;
        public final ByteBuffer buffer;
        public final String str;

        public final int switchNumber;
        public final byte[] bigArray;

        public TestData(int swtNum) {
            Random rand = new Random();
            byt = (byte) rand.nextInt(255);
            word = rand.nextInt(65536);
            this.array = new byte[rand.nextInt(32)];
            rand.nextBytes(this.array);
            byte[] bufferArray = new byte[rand.nextInt(32)];
            rand.nextBytes(bufferArray);
            this.buffer = ByteBuffer.wrap(bufferArray);
            this.str = UUID.randomUUID().toString();
            // only used testing new 'Read and 'Write Data
            this.bigArray = new byte[SIZE_BIG_ARRAY];
            rand.nextBytes(this.bigArray);
            this.switchNumber = swtNum;
        }
    }

    @Test
    /**   This tests uses the Mock_I2C device. This device uses an ArrayDeque for storage.
     * Each write operation add the written data to the end of the queue. Therefore, in order
     * to 'pop' the corresponding value from the queue, the write and read operation must maintain
     * order    Think FIFO
     * <ol>
     * <li> Write value A to Register X </li>
     * <li> Write value B to Register y </li>
     * <li> Read  value A from Register X </li>
     * <li> Read value  B from Register Y </li>
     * </ol>
     *
     */
    public void testRegisterDataWriteRead() {

        // create I2C config
        var config = I2C.newConfigBuilder(pi4j)
            .id("my-i2c-bus")
            .name("My I2C Bus")
            .bus(I2C_BUS)
            .device(I2C_DEVICE)
            .build();

        // use try-with-resources to auto-close I2C when complete
        try (var i2c = pi4j.i2c().create(config)) {

            // ensure that the I2C instance is not null;
            assertNotNull(i2c);

            // initialize sample data sets and register objects
            for (int n = 0; n < NUMBER_OF_REGISTERS; n++) {
                samples.add(new TestData(n));
                registers.add(i2c.register(n));
            }

            // write randomized sample data to all test registers
            for (int n = 0; n < NUMBER_OF_REGISTERS; n++) {
                writeRegister(registers.get(n), samples.get(n));
            }

            // read and compare sample data from all test registers
            for (int n = 0; n < NUMBER_OF_REGISTERS; n++) {
                readRegister(registers.get(n), samples.get(n));
            }

            // write randomized sample data to all test registers, performing the
            for (int n = 0; n < NUMBER_OF_REGISTERS; n++) {
                writeRegisterData(registers.get(n), samples.get(n));
            }
            // write randomized sample data to all test registers, performing the
            // read data test
            for (int n = 0; n < NUMBER_OF_REGISTERS; n++) {
                readRegisterData(registers.get(n), samples.get(n));
            }


        }

    }


    public void readRegisterData(I2CRegister register, TestData sample) {
        // use imagined chip offset 0x142
        int rc = -1;
        // two byte register value
        byte[] tbReg = new byte[2];
        byte[] readData = null;
        boolean compare = false;

        // one byte register value
        int obReg = 0;


        switch (sample.switchNumber) {
            case 0:
                // do the read back verification
                // read back register contents from offset 0x0142. Compare offset 0x0 in bigArray
                readData = new byte[0x10];
                tbReg[0] = 0x42;
                tbReg[1] = 0x01;
                rc = register.readRegister(tbReg, readData, 0, 0x10);
                if (rc != 0x10) {
                    Assertions.fail("readRegisterData: fail  RC switchNumber : " + sample.switchNumber);
                }
                compare = this.compareArray(sample.bigArray, readData, 0x0, 0x10);
                if (!compare) {
                    Assertions.fail("readRegisterData: fail  switchNumber : " + sample.switchNumber);
                }
                break;
            case 1:
                // do the read back verification
                // read back register contents from offset 0x42. Compare offset 0x0 in bigArray
                readData = new byte[0x10];
                obReg = 0x42;
                rc = register.readRegister(obReg, readData, 0, 0x10);
                if (rc != 0x10) {
                    Assertions.fail("readRegisterData: fail  RC switchNumber : " + sample.switchNumber);
                }
                compare = this.compareArray(sample.bigArray, readData, 0x0, 0x10);
                if (!compare) {
                    Assertions.fail("readRegisterData: fail  switchNumber : " + sample.switchNumber);
                }
                break;
            case 2:
                // do the read back verification
                // read back register contents from offset 0x42. Compare offset 0x05 in bigArray
                readData = new byte[0x10];
                obReg = 0x42;
                rc = register.readRegister(obReg, readData, 0, 0x10);
                if (rc != 0x10) {
                    Assertions.fail("readRegisterData: fail  RC switchNumber : " + sample.switchNumber);
                }
                compare = this.compareArray(sample.bigArray, readData, 0x5, 0x10);
                if (!compare) {
                    Assertions.fail("readRegisterData: fail  switchNumber : " + sample.switchNumber);
                }
                break;
            case 3:
                // do the read back verification
                // read back register contents from offset 0x0142. Compare offset 0x05 in bigArray
                readData = new byte[0x10];
                tbReg[0] = 0x42;
                tbReg[1] = 0x01;
                rc = register.readRegister(tbReg, readData, 0, 0x10);
                if (rc != 0x10) {
                    Assertions.fail("readRegisterData: fail  RC switchNumber : " + sample.switchNumber);
                }
                compare = this.compareArray(sample.bigArray, readData, 0x05, 0x10);
                if (!compare) {
                    Assertions.fail("readRegisterData: fail  switchNumber : " + sample.switchNumber);
                }
                break;
            case 4:
                // do the read back verification
                // read back register contents from offset 0x0142. Compare offset 0x05 in bigArray
                int readDataOffset = 0x05;
                // As the read data will be written starting at offset 0x02,
                // the requested data length must be less to not walk off the buffer end.
                int userBufferLength = 0x10 + readDataOffset;
                readData = new byte[userBufferLength];
                tbReg[0] = 0x42;
                tbReg[1] = 0x01;
                rc = register.readRegister(tbReg, readData, readDataOffset, 0x10);
                if (rc != 0x10) {
                    Assertions.fail("readRegisterData: fail  RC switchNumber : " + sample.switchNumber);
                }
                // offset applied to first parameter, so swap parms
                compare = this.compareArray(readData, sample.bigArray, readDataOffset, 0x10);
                if (!compare) {
                    Assertions.fail("readRegisterData: fail  switchNumber : " + sample.switchNumber);
                }
                break;
            case 5:
                break;
            case 6:
                break;
            case 7:
                break;
            case 8:
                break;
            case 9:
                break;
            case 10:
                break;
            default:
        }
    }


    public void writeRegisterData(I2CRegister register, TestData sample) {
        // use imagined chip offset 0x142
        int rc = -1;
        // two byte register value
        byte[] tbReg = new byte[2];

        // one byte register value
        int obReg = 0;


        switch (sample.switchNumber) {
            case 0:
                // copy first 0x10 bytes of data to chip register offset 0x0142
                tbReg[0] = 0x42;
                tbReg[1] = 0x01;
                rc = register.writeRegister(tbReg, sample.bigArray, 0, 0x10);
                if (rc != 0x10) {
                    Assertions.fail("writeRegisterData: fail RC switchNumber : " + sample.switchNumber);
                }

                break;
            case 1:
                // copy first 0x10 bytes of data to chip register offset 0x42
                obReg = 0x42;
                rc = register.writeRegister(obReg, sample.bigArray, 0, 0x10);
                if (rc != 0x10) {
                    Assertions.fail("writeRegisterData: fail RC switchNumber : " + sample.switchNumber);
                }
                break;
            case 2:
                // copy first 0x10 bytes of data to chip register offset 0x42
                obReg = 0x42;
                rc = register.writeRegister(obReg, sample.bigArray, 0x05, 0x10);
                if (rc != 0x10) {
                    Assertions.fail("writeRegisterData: fail RC switchNumber : " + sample.switchNumber);
                }
                break;
            case 3:
                // copy first 0x10 bytes of data to chip register offset 0x0142
                tbReg[0] = 0x42;
                tbReg[1] = 0x01;
                rc = register.writeRegister(tbReg, sample.bigArray, 0x05, 0x10);
                if (rc != 0x10) {
                    Assertions.fail("writeRegisterData: fail RC switchNumber : " + sample.switchNumber);
                }
                break;
            case 4:
                // copy first 0x10 bytes of data to chip register offset 0x0142
                tbReg[0] = 0x42;
                tbReg[1] = 0x01;
                rc = register.writeRegister(tbReg, sample.bigArray, 0, 0x10);
                if (rc != 0x10) {
                    Assertions.fail("writeRegisterData: fail RC switchNumber : " + sample.switchNumber);
                }
                break;
            case 5:
                break;
            case 6:
                break;
            case 7:
                break;
            case 8:
                break;
            case 9:
                break;
            case 10:
                break;
            default:
                //Assertions.fail("writeRegisterData: invalid switchNumber");
        }
    }


    /**
     * Unit testing, was used for unit testing write to register 0x0142 and read from 0x0143.
     * Not supported using ArrayDeque, required flat multi dimension array
     *
     * @param a        byte array, comparison begins at aOffset
     * @param b        byte array whose contents are compared for numBytes
     * @param aOffset  starting compare offset within 'a'
     * @param numBytes How many bytes to compare
     * @return If comparison equal, true, else false
     */
    public boolean compareArray(byte[] a, byte[] b, int aOffset, int numBytes) {
        boolean rval = true;
        if ((a.length >= (aOffset + numBytes)) && (b.length >= numBytes)) {
            for (int c = 0; c < numBytes; c++) {
                if (a[aOffset + c] != b[c]) {
                    rval = false;
                    break;
                }
            }
        }
        return rval;
    }


    public void writeRegister(I2CRegister register, TestData sample) {

        // write a single byte (8-bit) value to the raw I2C device (not to a register)
        register.write(sample.byt);

        // write a single word (16-bit) value to the raw I2C device (not to a register)
        register.writeWord(sample.word);

        // write an array of data bytes to the raw I2C device (not to a register)
        register.write(sample.array);

        // write a buffer of data bytes to the raw I2C device (not to a register)
        register.write(sample.buffer);

        // write a string of data to the raw I2C device (not to a register)
        register.write(sample.str);

        byte[] reg = new byte[2];
        register.writeRegister(reg, sample.array, 0, sample.array.length);
    }

    public void readRegister(I2CRegister register, TestData sample) {

        // read single byte (8-bit) value from the raw I2C device (not from a register)
        byte b = (byte) register.read();
        assertEquals(sample.byt, b);

        // read single word (16-bit) value from the raw I2C device (not from a register)
        int w = register.readWord();
        assertEquals(sample.word, w);

        // read an array of data bytes from the raw I2C device (not from a register)
        byte[] byteArray = new byte[sample.array.length];
        register.read(byteArray, 0, byteArray.length);
        assertArrayEquals(sample.array, byteArray);

        // read a buffer of data bytes from the raw I2C device (not from a register)
        ByteBuffer buffer = ByteBuffer.allocate(sample.buffer.capacity());
        register.read(buffer, 0, buffer.capacity());
        assertArrayEquals(sample.buffer.array(), buffer.array());

        // read a string of data from the raw I2C device (not from a register)
        String testString = register.readString(sample.str.length());
        assertEquals(sample.str, testString);
    }

}

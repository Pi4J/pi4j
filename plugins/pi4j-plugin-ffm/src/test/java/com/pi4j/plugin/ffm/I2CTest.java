package com.pi4j.plugin.ffm;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.i2c.I2CConfigBuilder;
import com.pi4j.io.i2c.I2CImplementation;
import com.pi4j.plugin.ffm.providers.i2c.I2CFFMProviderImpl;
import com.pi4j.plugin.ffm.providers.i2c.impl.I2CDirect;
import com.pi4j.plugin.ffm.providers.i2c.impl.I2CFile;
import com.pi4j.plugin.ffm.providers.i2c.impl.I2CSMBus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.pi4j.plugin.ffm.MockHelper.*;
import static org.junit.jupiter.api.Assertions.*;

public class I2CTest {
    private static Context pi4j;

    @BeforeAll
    public static void setup() {
        pi4j = Pi4J.newContextBuilder()
            .add(new I2CFFMProviderImpl())
            .build();

    }

    @AfterAll
    public static void teardown() {
        pi4j.shutdown();
    }

    @Test
    public void testCreation() {
        try (var _ = createFileMock(); var _ = createIoctlMock(); var _ = createSMBusMock();
             var smbus = pi4j.i2c().create(I2CConfigBuilder.newInstance(pi4j).bus(1).device(0x1C).i2cImplementation(I2CImplementation.SMBUS));
             var direct = pi4j.i2c().create(I2CConfigBuilder.newInstance(pi4j).bus(2).device(0x1C).i2cImplementation(I2CImplementation.DIRECT));
             var file = pi4j.i2c().create(I2CConfigBuilder.newInstance(pi4j).bus(3).device(0x1C).i2cImplementation(I2CImplementation.FILE))) {

            assertInstanceOf(I2CSMBus.class, smbus);
            assertEquals(1, smbus.bus());
            assertEquals(0x1C, smbus.device());
            assertInstanceOf(I2CDirect.class, direct);
            assertEquals(2, direct.bus());
            assertEquals(0x1C, direct.device());
            assertInstanceOf(I2CFile.class, file);
            assertEquals(3, file.bus());
            assertEquals(0x1C, file.device());
        }
    }

    @Test
    public void testWriteSMBus() {
        try (var _ = createFileMock(); var _ = createIoctlMock(); var _ = createSMBusMock();
             var smbus = pi4j.i2c().create(I2CConfigBuilder.newInstance(pi4j).bus(4).device(0x1C).i2cImplementation(I2CImplementation.SMBUS))) {

            var result = smbus.write((byte) 0x1C);
            assertEquals(42, result);

            assertThrows(UnsupportedOperationException.class, () -> smbus.write(new byte[]{1, 2, 3}, 0, 3));

            result = smbus.writeRegister(0x1C, 0x1C);
            assertEquals(42, result);

            result = smbus.writeRegister(0x1C, new byte[]{1, 2, 3}, 0, 3);
            assertEquals(42, result);

            result = smbus.writeRegister(new byte[]{1, 2, 3}, new byte[]{1, 2, 3}, 0, 3);
            assertEquals(42, result);
        }
    }

    @Test
    public void testReadSMBus() {
        try (var _ = createFileMock(); var _ = createIoctlMock(); var _ = createSMBusMock();
             var smbus = pi4j.i2c().create(I2CConfigBuilder.newInstance(pi4j).bus(5).device(0x1C).i2cImplementation(I2CImplementation.SMBUS))) {
            var result = smbus.readByte();
            assertEquals((byte) 0xff, result);

            var result1 = smbus.read();
            assertEquals(0xff, result1);

            assertThrows(UnsupportedOperationException.class, () -> smbus.read(new byte[]{1, 2, 3}, 0, 3));

            var result2 = smbus.readRegister(0x1C);
            assertEquals(0xff, result2);

            assertThrows(UnsupportedOperationException.class, () -> smbus.readRegister(new byte[]{1, 2, 3}, new byte[3], 0, 3));

            var result3 = new byte[4];
            var count3 = smbus.readRegister(0x1C, result3, 0, 4);
            assertEquals(4, count3);
            assertArrayEquals("Test".getBytes(), result3);

            result3 = new byte[1];
            count3 = smbus.readRegister(0x1C, result3, 0, 1);
            assertEquals(1, count3);
            assertArrayEquals(new byte[]{(byte) -1}, result3);


        }
    }

    @Test
    public void testWriteDirect() {
        try (var _ = createFileMock(); var _ = createIoctlMock();
             var direct = pi4j.i2c().create(I2CConfigBuilder.newInstance(pi4j).bus(6).device(0x1C).i2cImplementation(I2CImplementation.DIRECT))) {

            var result = direct.write((byte) 0x1C);
            assertEquals(1, result);

            result = direct.write("Test".getBytes());
            assertEquals(4, result);

            result = direct.writeRegister(0x1C, 0x1C);
            assertEquals(1, result);

            result = direct.writeRegister(0x1C, "Test".getBytes(), 0, 4);
            assertEquals(4, result);

            result = direct.writeRegister("Test".getBytes(), "Test".getBytes(), 0, 4);
            assertEquals(4, result);
        }
    }

    @Test
    public void testReadDirect() {
        try (var _ = createFileMock(); var _ = createIoctlMock();
             var direct = pi4j.i2c().create(I2CConfigBuilder.newInstance(pi4j).bus(7).device(0x1C).i2cImplementation(I2CImplementation.DIRECT))) {

            var data = direct.read();
            assertEquals((byte) 0xff, data);

            var data1 = new byte[1];
            var count1 = direct.read(data1, 0 , 1);
            assertEquals(1, count1);
            assertArrayEquals(new byte[] {(byte) 0xff}, data1);

            var data2 = new byte[4];
            var count2 = direct.readRegister(0x1C, data2);
            assertEquals(4, count2);
            assertArrayEquals("Test".getBytes(), data2);

            var data3 = new byte[4];
            var count3 = direct.readRegister("Test".getBytes(), data3);
            assertEquals(4, count3);
            assertArrayEquals("Test".getBytes(), data3);

        }
    }

    @Test
    public void testWriteFile() {
        try (var _ = createFileMock(); var _ = createIoctlMock();
             var file = pi4j.i2c().create(I2CConfigBuilder.newInstance(pi4j).bus(8).device(0x1C).i2cImplementation(I2CImplementation.FILE))) {

            var result = file.write((byte) 0x1C);
            assertEquals(42, result);

            result = file.write("Test".getBytes());
            assertEquals(42, result);

            result = file.writeRegister(0x1C, 0x1C);
            assertEquals(42, result);

            result = file.writeRegister(0x1C, "Test".getBytes(), 0, 4);
            assertEquals(42, result);

            result = file.writeRegister("Test".getBytes(), "Test".getBytes(), 0, 4);
            assertEquals(42, result);
        }
    }

    @Test
    public void testReadFile() {
        try (var _ = createFileMock(); var _ = createIoctlMock();
             var file = pi4j.i2c().create(I2CConfigBuilder.newInstance(pi4j).bus(9).device(0x1C).i2cImplementation(I2CImplementation.FILE))) {

            var data = file.read();
            assertEquals("T".getBytes()[0], data);

            var data1 = new byte[4];
            var count1 = file.read(data1, 0 , 4);
            assertEquals(4, count1);
            assertArrayEquals("Test".getBytes(), data1);

            var data2 = new byte[4];
            var count2 = file.readRegister(0x1C, data2);
            assertEquals(4, count2);
            assertArrayEquals("Test".getBytes(), data2);

            var data3 = new byte[4];
            var count3 = file.readRegister("Test".getBytes(), data3);
            assertEquals(4, count3);
            assertArrayEquals("Test".getBytes(), data3);

        }
    }
}

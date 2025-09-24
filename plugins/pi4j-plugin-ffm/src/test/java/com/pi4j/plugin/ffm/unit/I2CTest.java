package com.pi4j.plugin.ffm.unit;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.i2c.I2CConfigBuilder;
import com.pi4j.io.i2c.I2CImplementation;
import com.pi4j.plugin.ffm.common.PermissionHelper;
import com.pi4j.plugin.ffm.common.i2c.rdwr.I2CMessage;
import com.pi4j.plugin.ffm.common.i2c.rdwr.RDWRData;
import com.pi4j.plugin.ffm.mocks.FileDescriptorNativeMock;
import com.pi4j.plugin.ffm.mocks.IoctlNativeMock;
import com.pi4j.plugin.ffm.mocks.PermissionHelperMock;
import com.pi4j.plugin.ffm.mocks.SMBusNativeMock;
import com.pi4j.plugin.ffm.providers.i2c.I2CFFMProviderImpl;
import com.pi4j.plugin.ffm.providers.i2c.I2CFunctionality;
import com.pi4j.plugin.ffm.providers.i2c.impl.I2CDirect;
import com.pi4j.plugin.ffm.providers.i2c.impl.I2CFile;
import com.pi4j.plugin.ffm.providers.i2c.impl.I2CSMBus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.condition.OS.LINUX;

public class I2CTest {
    private static Context pi4j;

    private static final FileDescriptorNativeMock.FileDescriptorTestData I2C_FILE =
        new FileDescriptorNativeMock.FileDescriptorTestData("/dev/i2c-", 1, "Test".getBytes());

    private static final MockedStatic<PermissionHelper> permissionHelperMock = PermissionHelperMock.echo();

    @BeforeAll
    public static void setup() {
        pi4j = Pi4J.newContextBuilder()
            .add(new I2CFFMProviderImpl())
            .build();

    }

    @AfterAll
    public static void teardown() {
        pi4j.shutdown();
        permissionHelperMock.close();
    }

    @Test
    @EnabledOnOs(LINUX)
    public void testCreation() {
        var functionalities = I2CFunctionality.I2C_FUNC_I2C.getValue() | I2CFunctionality.I2C_FUNC_SMBUS_BYTE.getValue()
            | I2CFunctionality.I2C_FUNC_SMBUS_BLOCK_DATA.getValue()
            | I2CFunctionality.I2C_FUNC_SMBUS_BYTE_DATA.getValue() | I2CFunctionality.I2C_FUNC_SMBUS_QUICK.getValue()
            | I2CFunctionality.I2C_FUNC_SMBUS_WORD_DATA.getValue();
        try (var _ = FileDescriptorNativeMock.echo(I2C_FILE); var _ = IoctlNativeMock.i2c(functionalities);
             var _ = SMBusNativeMock.echo();
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
    @EnabledOnOs(LINUX)
    public void testWriteSMBus() {
        var functionalities = I2CFunctionality.I2C_FUNC_SMBUS_BYTE.getValue() | I2CFunctionality.I2C_FUNC_SMBUS_BLOCK_DATA.getValue()
            | I2CFunctionality.I2C_FUNC_SMBUS_BYTE_DATA.getValue() | I2CFunctionality.I2C_FUNC_SMBUS_QUICK.getValue()
            | I2CFunctionality.I2C_FUNC_SMBUS_WORD_DATA.getValue();
        try (var _ = FileDescriptorNativeMock.echo(I2C_FILE);
             var _ = IoctlNativeMock.i2c(functionalities);
             var _ = SMBusNativeMock.echo();
             var smbus = pi4j.i2c().create(I2CConfigBuilder.newInstance(pi4j).bus(4).device(0x1C).i2cImplementation(I2CImplementation.SMBUS))) {

            var result = smbus.write((byte) 0x1C);
            assertEquals(1, result);

            assertThrows(UnsupportedOperationException.class, () -> smbus.write(new byte[]{1, 2, 3}, 0, 3));

            result = smbus.writeRegister(0x1C, 0x1C);
            assertEquals(1, result);

            result = smbus.writeRegister(0x1C, new byte[]{1, 2, 3}, 0, 3);
            assertEquals(3, result);

            result = smbus.writeRegister(new byte[]{1, 2, 3}, new byte[]{1, 2, 3}, 0, 3);
            assertEquals(5, result);
        }
    }

    @Test
    @EnabledOnOs(LINUX)
    public void testReadSMBus() {
        var functionalities = I2CFunctionality.I2C_FUNC_SMBUS_BYTE.getValue() | I2CFunctionality.I2C_FUNC_SMBUS_BLOCK_DATA.getValue()
            | I2CFunctionality.I2C_FUNC_SMBUS_BYTE_DATA.getValue() | I2CFunctionality.I2C_FUNC_SMBUS_QUICK.getValue()
            | I2CFunctionality.I2C_FUNC_SMBUS_WORD_DATA.getValue();
        try (var _ = FileDescriptorNativeMock.echo(I2C_FILE);
             var _ = IoctlNativeMock.i2c(functionalities);
             var _ = SMBusNativeMock.echo();
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
        var functionalities = I2CFunctionality.I2C_FUNC_I2C.getValue();
        var i2cData = new IoctlNativeMock.IoctlTestData(RDWRData.class, (answer) -> answer.<RDWRData>getArgument(2));
        try (var _ = FileDescriptorNativeMock.echo(I2C_FILE);
             var _ = IoctlNativeMock.i2c(functionalities, i2cData);
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
        var functionalities = I2CFunctionality.I2C_FUNC_I2C.getValue();
        var i2cData = new IoctlNativeMock.IoctlTestData(RDWRData.class, (answer) -> {
            RDWRData rdwr = answer.getArgument(2);
            if (rdwr.nmsgs() > 1) {
                return new RDWRData(new I2CMessage[]{
                    rdwr.msgs()[0],
                    new I2CMessage(rdwr.msgs()[1].address(), rdwr.msgs()[1].flags(), 4, "Test".getBytes())
                }, rdwr.nmsgs());
            } else {
                return new RDWRData(new I2CMessage[]{
                    new I2CMessage(rdwr.msgs()[0].address(), rdwr.msgs()[0].flags(), 1, new byte[]{(byte) 0xff})
                }, rdwr.nmsgs());
            }
        });
        try (var _ = FileDescriptorNativeMock.echo(I2C_FILE);
             var _ = IoctlNativeMock.i2c(functionalities, i2cData);
             var direct = pi4j.i2c().create(I2CConfigBuilder.newInstance(pi4j).bus(7).device(0x1C).i2cImplementation(I2CImplementation.DIRECT))) {

            var data = direct.read();
            assertEquals((byte) 0xff, data);

            var data1 = new byte[1];
            var count1 = direct.read(data1, 0, 1);
            assertEquals(1, count1);
            assertArrayEquals(new byte[]{(byte) 0xff}, data1);

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
        var functionalities = I2CFunctionality.I2C_FUNC_I2C.getValue();
        var i2cWriteByte = new FileDescriptorNativeMock.FileDescriptorTestData("/dev/null", 1, new byte[]{0x1C});
        var i2cWriteBytes = new FileDescriptorNativeMock.FileDescriptorTestData("/dev/null", 1, "Test".getBytes());
        var i2cWriteRegister1 = new FileDescriptorNativeMock.FileDescriptorTestData("/dev/null", 1, new byte[]{0x1C, 0x1C});
        var i2cWriteRegister2 = new FileDescriptorNativeMock.FileDescriptorTestData("/dev/null", 1, new byte[]{0x1C, 0x54, 0x65, 0x73, 0x74});
        var i2cWriteRegister3 = new FileDescriptorNativeMock.FileDescriptorTestData("/dev/null", 1, new byte[]{0x54, 0x65, 0x73, 0x74, 0x54, 0x65, 0x73, 0x74});
        try (var _ = FileDescriptorNativeMock.echo(I2C_FILE, i2cWriteByte, i2cWriteBytes, i2cWriteRegister1, i2cWriteRegister2, i2cWriteRegister3);
             var _ = IoctlNativeMock.i2c(functionalities);
             var file = pi4j.i2c().create(I2CConfigBuilder.newInstance(pi4j).bus(8).device(0x1C).i2cImplementation(I2CImplementation.FILE))) {

            var result = file.write((byte) 0x1C);
            assertEquals(1, result);

            result = file.write("Test".getBytes());
            assertEquals(4, result);

            result = file.writeRegister(0x1C, 0x1C);
            assertEquals(2, result);

            result = file.writeRegister(0x1C, "Test".getBytes(), 0, 4);
            assertEquals(5, result);

            result = file.writeRegister("Test".getBytes(), "Test".getBytes(), 0, 4);
            assertEquals(8, result);
        }
    }

    @Test
    public void testReadFile() {
        var functionalities = I2CFunctionality.I2C_FUNC_I2C.getValue();
        var i2cReadByte = new FileDescriptorNativeMock.FileDescriptorTestData("/dev/null", 1, "T".getBytes());
        var i2cReadBytes = new FileDescriptorNativeMock.FileDescriptorTestData("/dev/null", 1, "Test".getBytes());
        try (var _ = FileDescriptorNativeMock.echo(I2C_FILE, i2cReadByte, i2cReadBytes);
             var _ = IoctlNativeMock.i2c(functionalities);
             var file = pi4j.i2c().create(I2CConfigBuilder.newInstance(pi4j).bus(9).device(0x1C).i2cImplementation(I2CImplementation.FILE))) {

            var data = file.read();
            assertEquals("T".getBytes()[0], data);

            var data1 = new byte[4];
            var count1 = file.read(data1, 0, 4);
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

package com.pi4j.plugin.ffm.integration;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfigBuilder;
import com.pi4j.io.i2c.I2CImplementation;
import com.pi4j.plugin.ffm.providers.i2c.I2CFFMProviderImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.condition.OS.LINUX;

@EnabledOnOs(LINUX)
//@Disabled
public class I2CDirectTest {
    private static Context pi4j;
    private static I2C i2c;

    @BeforeAll
    public static void setup() throws InterruptedException, IOException {
        pi4j = Pi4J.newContextBuilder()
            .add(new I2CFFMProviderImpl())
            .build();
        i2c = pi4j.i2c().create(I2CConfigBuilder.newInstance(pi4j).bus(1).device(0x1C).i2cImplementation(I2CImplementation.DIRECT));

    }

    @AfterAll
    public static void shutdown() throws InterruptedException, IOException {
        pi4j.shutdown();
    }

    @Test
    public void testI2CCreate() {
        assertEquals(1, i2c.bus());
    }

    @Test
    public void testI2CWriteByte() {
        var write = i2c.write(0xEE);
        assertEquals(1, write);
    }

    @Test
    public void testI2CWriteBytes() {
        var buf = new byte[] {0x01, 0x02, 0x03, 0x04};
        var write = i2c.write(buf);
        assertEquals(4, write);
    }

    @Test
    public void testI2CWriteReadRegisterData() {
        var writeBuffer = new byte[] {0x0A, 0x0B, 0x0C};
        var write = i2c.writeRegister(0xFF, writeBuffer);
        assertEquals(3, write);
        var readBuffer = new byte[3];
        var read = i2c.readRegister(0xFF, readBuffer);
        assertEquals(3, read);
        assertArrayEquals(writeBuffer, readBuffer);

        var writeBuffer2 = new byte[] {0x0D, 0x0E, 0x0F};
        var write2 = i2c.writeRegister(0x1F, writeBuffer2);
        assertEquals(3, write2);
        var readBuffer2 = new byte[3];
        var read2 = i2c.readRegister(0x1F, readBuffer2);
        assertEquals(3, read2);
        assertArrayEquals(writeBuffer2, readBuffer2);
    }

    @Test
    public void testI2CWriteReadData() {
        var write = i2c.write(0x0A);
        assertEquals(1, write);
        var read = i2c.read();
        assertEquals(0x0A, read);
    }
}

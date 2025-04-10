package com.pi4j.plugin.ffm;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.i2c.I2C;
import com.pi4j.plugin.ffm.providers.i2c.I2CFFMProviderImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;

import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.condition.OS.LINUX;

@EnabledOnOs(LINUX)
public class I2CTest {
    private static Context pi4j;
    private static I2C i2c;

    @BeforeAll
    public static void setup() throws InterruptedException, IOException {
        var scriptPath = Paths.get("src/test/resources/i2c-setup.sh");
        var setupScript = new ProcessBuilder("/bin/bash", "-c", "sudo " + scriptPath.toFile().getAbsolutePath()).start();
        var result = setupScript.waitFor();
        if (result != 0) {
            var username = System.getProperty("user.name");
            var errorOutput =  new String(setupScript.getErrorStream().readAllBytes());
            fail("Failed to cleanup I2C Test: \n" + errorOutput + "\n" +
                "Probably you need to add the I2C bash script to sudoers file " +
                "with visudo: '" + username + " ALL=(ALL) NOPASSWD: " + scriptPath.toFile().getParentFile().getAbsolutePath() + "/'");
        }

        pi4j = Pi4J.newContextBuilder()
            .add(new I2CFFMProviderImpl())
            .build();
        i2c = pi4j.i2c().create(1, 0x1C);

    }

    @AfterAll
    public static void shutdown() throws InterruptedException, IOException {
        pi4j.shutdown();

        var scriptPath = Paths.get("src/test/resources/i2c-clean.sh");
        var setupScript = new ProcessBuilder("/bin/bash", "-c", "sudo " + scriptPath.toFile().getAbsolutePath()).start();
        var result = setupScript.waitFor();
        if (result != 0) {
            var username = System.getProperty("user.name");
            var errorOutput =  new String(setupScript.getErrorStream().readAllBytes());
            fail("Failed to cleanup I2C Test: \n" + errorOutput + "\n" +
                "Probably you need to add the I2C bash script to sudoers file " +
                "with visudo: '" + username + " ALL=(ALL) NOPASSWD: " + scriptPath.toFile().getParentFile().getAbsolutePath() + "/'");
        }
    }

    @Test
    public void testI2CCreation() {
        assertEquals(1, i2c.bus());
    }

    @Test
    public void testI2CWriteByte() throws InterruptedException {
        var write = i2c.write(0xEE);
        assertEquals(0, write);
    }

    @Test
    public void testI2CReadByte() throws InterruptedException {
        var read = i2c.read();
        assertEquals(0, read);
    }

    @Test
    public void testI2CWriteReadBlockData() throws InterruptedException {
        var writeBuffer = new byte[] {0x01, 0x02, 0x03};
        var write = i2c.writeRegister(0xFF, writeBuffer);
        assertEquals(0, write);
        var readBuffer = new byte[3];
        var read = i2c.readRegister(0xFF, readBuffer);
        assertEquals(3, read);
        assertArrayEquals(writeBuffer, readBuffer);
    }

    //@Test
    public void testI2CWriteRead16BitAddress() throws InterruptedException {
        var writeBuffer = new byte[] {0x01};
        var writeRegister = new byte[] {0x00, 0x00};
        var write = i2c.writeRegister(writeRegister, writeBuffer, 0, 1);
        assertEquals(0, write);
        var read = i2c.read();
        assertEquals(0x01, read);
    }

    @Test
    public void testI2CWriteReadRegister() throws InterruptedException {
       var write = i2c.writeRegister(0x324, 0xFF);
       assertEquals(0, write);
       var read = i2c.readRegister(0x324);
       assertEquals(0xFF, read);
    }
}

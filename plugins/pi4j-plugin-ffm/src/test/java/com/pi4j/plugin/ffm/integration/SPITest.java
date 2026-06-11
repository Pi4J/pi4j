package com.pi4j.plugin.ffm.integration;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.spi.Spi;
import com.pi4j.io.spi.SpiBus;
import com.pi4j.io.spi.SpiConfigBuilder;
import com.pi4j.plugin.ffm.providers.spi.FFMSpiProviderImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;

import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.condition.OS.LINUX;

@EnabledOnOs(LINUX)
public class SPITest {
    private static Context pi4j;
    private static Spi spi;

    @BeforeAll
    public static void setup() throws InterruptedException, IOException {
        var scriptPath = Paths.get("src/test/resources").toFile().getAbsoluteFile();
        var setupScript = new ProcessBuilder("/bin/bash", "-c", "sudo " + scriptPath + "/spi-setup.sh");
        setupScript.directory(scriptPath);
        var process = setupScript.start();
        var result = process.waitFor();
        if (result != 0) {
            var username = System.getProperty("user.name");
            var errorOutput = new String(process.getErrorStream().readAllBytes());
            fail("Failed to setup SPI Test: \n" + errorOutput + "\n" +
                "Probably you need to add the SPI bash script to sudoers file " +
                "with visudo: '" + username + " ALL=(ALL) NOPASSWD: " + scriptPath.getParentFile().getAbsolutePath() + "/'");
        }

        pi4j = Pi4J.newContextBuilder()
            .add(new FFMSpiProviderImpl())
            .build();
        var config = SpiConfigBuilder.newInstance(pi4j).bus(SpiBus.BUS_0).channel(0).mode(0).baud(50_000).build();
        spi = pi4j.spi().create(config);
    }

    @AfterAll
    public static void shutdown() throws InterruptedException, IOException {
        pi4j.shutdown();
        var scriptPath = Paths.get("src/test/resources").toFile().getAbsoluteFile();
        var setupScript = new ProcessBuilder("/bin/bash", "-c", "sudo " + scriptPath + "/spi-clean.sh");
        setupScript.directory(scriptPath);
        var process = setupScript.start();
        var result = process.waitFor();
        if (result != 0) {
            var username = System.getProperty("user.name");
            var errorOutput = new String(process.getErrorStream().readAllBytes());
            fail("Failed to cleanup SPI Test: \n" + errorOutput + "\n" +
                "Probably you need to add the SPI bash script to sudoers file " +
                "with visudo: '" + username + " ALL=(ALL) NOPASSWD: " + scriptPath.getParentFile().getAbsolutePath() + "/'");
        }
    }

    @Test
    public void testSPITransfer() {
        var buffer = new byte[4];
        spi.transfer(("Test").getBytes(), 0, buffer, 0, 4);
        assertEquals("Test", new String(buffer));
    }

    @Test
    public void testSPIWrite() {
        var written = spi.write(("Test").getBytes());
        assertEquals(4, written);
    }

    @Test
    public void testSPIRead() {
        var buffer = new byte[4];
        spi.read(buffer);
        assertArrayEquals(new byte[]{0, 0, 0, 0}, buffer);
    }
}

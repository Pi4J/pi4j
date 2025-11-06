package com.pi4j.plugin.ffm.integration;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.spi.Spi;
import com.pi4j.io.spi.SpiBus;
import com.pi4j.io.spi.SpiConfigBuilder;
import com.pi4j.plugin.ffm.providers.spi.SpiFFMProviderImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;

import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.condition.OS.LINUX;

@EnabledOnOs(LINUX)
@Disabled
public class SPITest {
    private static final String IN_CONTAINER = System.getenv("IN_CONTAINER");
    private static Context pi4j;
    private static Spi spi;

    @BeforeAll
    public static void setup() throws InterruptedException, IOException {
        if (IN_CONTAINER == null || !IN_CONTAINER.equals("true")) {
            var scriptPath = Paths.get("src/test/resources/spi-setup.sh");
            var setupScript = new ProcessBuilder("/bin/bash", "-c", "sudo " + scriptPath.toFile().getAbsolutePath()).start();
            var result = setupScript.waitFor();
            if (result != 0) {
                var username = System.getProperty("user.name");
                var errorOutput = new String(setupScript.getErrorStream().readAllBytes());
                fail("Failed to setup SPI Test: \n" + errorOutput + "\n" +
                    "Probably you need to add the SPI bash script to sudoers file " +
                    "with visudo: '" + username + " ALL=(ALL) NOPASSWD: " + scriptPath.toFile().getParentFile().getAbsolutePath() + "/'");
            }
        }

        pi4j = Pi4J.newContextBuilder()
            .add(new SpiFFMProviderImpl())
            .build();
        var config = SpiConfigBuilder.newInstance(pi4j).bus(SpiBus.BUS_0).channel(0).mode(0).baud(50_000).build();
        spi = pi4j.spi().create(config);
    }

    @AfterAll
    public static void shutdown() throws InterruptedException, IOException {
        pi4j.shutdown();

        if (IN_CONTAINER == null || !IN_CONTAINER.equals("true")) {
            var scriptPath = Paths.get("src/test/resources/spi-clean.sh");
            var setupScript = new ProcessBuilder("/bin/bash", "-c", "sudo " + scriptPath.toFile().getAbsolutePath()).start();
            var result = setupScript.waitFor();
            if (result != 0) {
                var username = System.getProperty("user.name");
                var errorOutput = new String(setupScript.getErrorStream().readAllBytes());
                fail("Failed to cleanup SPI Test: \n" + errorOutput + "\n" +
                    "Probably you need to add the SPI bash script to sudoers file " +
                    "with visudo: '" + username + " ALL=(ALL) NOPASSWD: " + scriptPath.toFile().getParentFile().getAbsolutePath() + "/'");
            }
        }
    }

    @Test
    public void testSPITransfer() {
        var buffer = new byte[4];
        spi.transfer("Test".getBytes(), 0, buffer, 0, 4);
        assertEquals("Test", new String(buffer));
    }

    @Test
    public void testSPIWrite() {
        var written = spi.write("Test".getBytes());
        assertEquals(4, written);
    }

    @Test
    public void testSPIRead() {
        var buffer = new byte[4];
        spi.read(buffer);
        assertArrayEquals(new byte[]{1, 1, 1, 1}, buffer);
    }
}

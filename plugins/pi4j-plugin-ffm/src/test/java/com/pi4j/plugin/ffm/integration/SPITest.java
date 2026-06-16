package com.pi4j.plugin.ffm.integration;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.spi.Spi;
import com.pi4j.io.spi.SpiBus;
import com.pi4j.io.spi.SpiConfigBuilder;
import com.pi4j.plugin.BaseSetup;
import com.pi4j.plugin.ffm.providers.spi.FFMSpiProviderImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.condition.OS.LINUX;

@EnabledOnOs(LINUX)
public class SPITest extends BaseSetup {
    private static Context pi4j;
    private static Spi spi;

    @BeforeAll
    public static void setup() throws InterruptedException, IOException {
        setup("spi");
        pi4j = Pi4J.newContextBuilder()
            .add(new FFMSpiProviderImpl())
            .build();
        var config = SpiConfigBuilder.newInstance()
            .bus(SpiBus.BUS_0)
            .channel(0)
            .mode(0)
            .baud(50_000)
            .build();
        spi = pi4j.spi().create(config);
    }

    @AfterAll
    public static void shutdown() throws InterruptedException, IOException {
        pi4j.shutdown();
        tearDown("spi");
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

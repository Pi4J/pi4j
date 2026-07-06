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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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
            .bus(6)
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

    @Test
    public void testSPILargeTransfer() {
        // Transfer more bytes than the spidev 'bufsiz' so the data must be split into several
        // SPI_IOC_MESSAGE chunks. Without chunking the kernel would reject this with EMSGSIZE.
        var bufsiz = readBufsiz();
        var size = bufsiz * 2 + 137;

        var write = new byte[size];
        new Random(42).nextBytes(write);
        var read = new byte[size];

        var transferred = spi.transfer(write, 0, read, 0, size);

        assertEquals(size, transferred);
        // the mock SPI controller echoes the tx buffer straight back into rx
        assertArrayEquals(write, read);
    }

    @Test
    public void testSPIWriteThenRead() {
        // single-chunk write-then-read: the mock controller stores the written bytes and echoes
        // them back on the subsequent read.
        var read = new byte[4];
        spi.writeThenRead("Test".getBytes(), 0, 4, 0, read, 0, 4);
        assertEquals("Test", new String(read));
    }

    @Test
    public void testSPILargeWriteThenRead() {
        // Write and read more bytes than the spidev 'bufsiz' so both phases must be chunked into
        // several SPI_IOC_MESSAGE calls. The assertion is only that the kernel does not reject the
        // request with EMSGSIZE - the mock controller cannot echo back a payload this large, so the
        // received content is not verified here (that is covered by the unit test).
        var bufsiz = readBufsiz();
        var size = bufsiz * 2 + 137;

        var write = new byte[size];
        new Random(7).nextBytes(write);
        var read = new byte[size];

        assertDoesNotThrow(() -> spi.writeThenRead(write, 0, size, 0, read, 0, size));
    }

    private static int readBufsiz() {
        try {
            return Integer.parseInt(Files.readString(Path.of("/sys/module/spidev/parameters/bufsiz")).trim());
        } catch (Exception e) {
            // spidev compile-time default when the parameter cannot be read
            return 4096;
        }
    }
}

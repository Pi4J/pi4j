package com.pi4j.plugin.ffm.unit;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.spi.SpiBus;
import com.pi4j.io.spi.SpiConfigBuilder;
import com.pi4j.plugin.ffm.common.FFMPermissionHelper;
import com.pi4j.plugin.ffm.common.spi.SpiMultipleTransferBuffer;
import com.pi4j.plugin.ffm.common.spi.SpiTransferBuffer;
import com.pi4j.plugin.ffm.mocks.FileDescriptorNativeMock;
import com.pi4j.plugin.ffm.mocks.FileDescriptorNativeMock.FileDescriptorTestData;
import com.pi4j.plugin.ffm.mocks.IoctlNativeMock;
import com.pi4j.plugin.ffm.mocks.PermissionHelperMock;
import com.pi4j.plugin.ffm.providers.spi.FFMSpiProviderImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SPITest {
    private static final String SPIDEV_BUFSIZ_PATH = "/sys/module/spidev/parameters/bufsiz";

    private static Context pi4j;

    private static final MockedStatic<FFMPermissionHelper> permissionHelperMock = PermissionHelperMock.echo();

    @BeforeAll
    public static void setup() {
        pi4j = Pi4J.newContextBuilder()
            .add(new FFMSpiProviderImpl())
            .build();

    }

    @AfterAll
    public static void teardown() {
        pi4j.shutdown();
        permissionHelperMock.close();
    }

    @Test
    public void testCreation() {
        try (var _ = FileDescriptorNativeMock.setup();
             var _ = IoctlNativeMock.setup()) {

            pi4j.spi().create(SpiConfigBuilder.newInstance()
                .bus(SpiBus.BUS_0)
                .channel(0)
                .mode(0)
                .baud(50_000)
                .build());
        }
    }

    @Test
    public void testWrite() {
        var spiTestData = new IoctlNativeMock.IoctlTestData(SpiTransferBuffer.class, (answer) -> {
            SpiTransferBuffer buffer = answer.getArgument(2);
            return new SpiTransferBuffer(buffer.getTxBuffer(), buffer.getTxBuffer(), buffer.getTxBuffer().length);
        });
        try (var _ = FileDescriptorNativeMock.setup();
             var _ = IoctlNativeMock.setup(spiTestData)) {

            var spi = pi4j.spi().create(SpiConfigBuilder.newInstance()
                .bus(SpiBus.BUS_0)
                .channel(1)
                .mode(0)
                .baud(50_000)
                .build());

            var result = spi.write((byte) 0x1C);
            assertEquals(1, result);

            result = spi.write(("Test").getBytes(), 0, 4);
            assertEquals(4, result);
        }
    }

    @Test
    public void testRead() {
        var spiTestData = new IoctlNativeMock.IoctlTestData(SpiTransferBuffer.class, (answer) -> {
            SpiTransferBuffer buffer = answer.getArgument(2);
            if (buffer.getRxBuffer().length == 1) {
                return new SpiTransferBuffer(buffer.getTxBuffer(), ("T").getBytes(), 1);
            } else {
                return new SpiTransferBuffer(buffer.getTxBuffer(), ("Test").getBytes(), 4);
            }
        });
        try (var _ = FileDescriptorNativeMock.setup();
             var _ = IoctlNativeMock.setup(spiTestData)) {

            var spi = pi4j.spi().create(SpiConfigBuilder.newInstance()
                .bus(SpiBus.BUS_0)
                .channel(2)
                .mode(0)
                .baud(50_000)
                .build());

            var result = spi.read();
            assertEquals(("T").getBytes()[0], result);

            var data = new byte[4];
            result = spi.read(data, 0, 4);
            assertEquals(4, result);
            assertArrayEquals(("Test").getBytes(), data);
        }
    }

    @Test
    public void testSPITransfer() {
        var spiTestData = new IoctlNativeMock.IoctlTestData(SpiTransferBuffer.class, (answer) -> {
            SpiTransferBuffer buffer = answer.getArgument(2);
            return new SpiTransferBuffer(buffer.getTxBuffer(), buffer.getTxBuffer(), buffer.getTxBuffer().length);
        });
        try (var _ = FileDescriptorNativeMock.setup();
             var _ = IoctlNativeMock.setup(spiTestData)) {

            var spi = pi4j.spi().create(SpiConfigBuilder.newInstance()
                .bus(SpiBus.BUS_0)
                .channel(3)
                .mode(0)
                .baud(50_000)
                .build());
            var buffer = new byte[4];
            var result = spi.transfer(("Test").getBytes(), 0, buffer, 0, 4);
            assertEquals(4, result);
            assertArrayEquals(("Test").getBytes(), buffer);
        }
    }

    @Test
    public void testWriteThenRead() {
        var spiTestData = new IoctlNativeMock.IoctlTestData(SpiMultipleTransferBuffer.class, (answer) -> {
            SpiMultipleTransferBuffer buffer = answer.getArgument(2);
            var inputBuffer = buffer.transferBuffer()[0];
            // the read transfer is read-only (null tx), so echo the written bytes back as its rx
            var written = inputBuffer.getTxBuffer();
            return new SpiMultipleTransferBuffer(inputBuffer, new SpiTransferBuffer(null, written, written.length, 1000));
        });
        try (var _ = FileDescriptorNativeMock.setup();
             var _ = IoctlNativeMock.setup(spiTestData)) {
            var spi = pi4j.spi().create(SpiConfigBuilder.newInstance()
                .bus(SpiBus.BUS_0)
                .channel(4)
                .mode(0)
                .baud(50_000)
                .build());
            var buffer = new byte[4];
            spi.writeThenRead("Test".getBytes(), 0, 4, 500, buffer, 0, 4);
            assertEquals(4, buffer.length);
            assertArrayEquals("Test".getBytes(), buffer);
        }
    }

    @Test
    public void testChunkedTransfer() {
        // bufsiz is reported as 8 bytes, so a 20 byte transfer must be split into 8 + 8 + 4.
        var callCount = new AtomicInteger();
        var maxChunkSize = new AtomicInteger();
        var spiTestData = new IoctlNativeMock.IoctlTestData(SpiTransferBuffer.class, (answer) -> {
            SpiTransferBuffer buffer = answer.getArgument(2);
            callCount.incrementAndGet();
            maxChunkSize.accumulateAndGet(buffer.getTxBuffer().length, Math::max);
            return new SpiTransferBuffer(buffer.getTxBuffer(), buffer.getTxBuffer(), buffer.getTxBuffer().length);
        });
        var bufsiz = new FileDescriptorTestData(SPIDEV_BUFSIZ_PATH, 99, "8\n".getBytes());
        try (var _ = FileDescriptorNativeMock.setup(bufsiz);
             var _ = IoctlNativeMock.setup(spiTestData)) {

            var spi = pi4j.spi().create(SpiConfigBuilder.newInstance()
                .bus(SpiBus.BUS_0)
                .channel(5)
                .mode(0)
                .baud(50_000)
                .build());

            var write = new byte[20];
            for (var i = 0; i < write.length; i++) {
                write[i] = (byte) i;
            }
            var read = new byte[20];
            var result = spi.transfer(write, 0, read, 0, write.length);

            assertEquals(20, result);
            assertArrayEquals(write, read);
            // 20 bytes split into chunks of at most 8 bytes -> 8 + 8 + 4 = 3 ioctl calls
            assertEquals(3, callCount.get());
            assertTrue(maxChunkSize.get() <= 8, "no chunk may exceed the reported bufsiz");
        }
    }

    @Test
    public void testChunkedTransferWithOffsets() {
        // Verify that read/write offsets are honored while chunking.
        var spiTestData = new IoctlNativeMock.IoctlTestData(SpiTransferBuffer.class, (answer) -> {
            SpiTransferBuffer buffer = answer.getArgument(2);
            return new SpiTransferBuffer(buffer.getTxBuffer(), buffer.getTxBuffer(), buffer.getTxBuffer().length);
        });
        var bufsiz = new FileDescriptorTestData(SPIDEV_BUFSIZ_PATH, 99, "4".getBytes());
        try (var _ = FileDescriptorNativeMock.setup(bufsiz);
             var _ = IoctlNativeMock.setup(spiTestData)) {

            var spi = pi4j.spi().create(SpiConfigBuilder.newInstance()
                .bus(SpiBus.BUS_0)
                .channel(6)
                .mode(0)
                .baud(50_000)
                .build());

            var write = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
            var read = new byte[12];
            // transfer 9 bytes starting at write index 2 into read starting at index 1
            var result = spi.transfer(write, 2, read, 1, 9);

            assertEquals(9, result);
            var expected = new byte[]{0, 3, 4, 5, 6, 7, 8, 9, 10, 11, 0, 0};
            assertArrayEquals(expected, read);
        }
    }

    @Test
    public void testChunkedWriteThenRead() {
        // bufsiz is reported as 4 bytes, so a 10 byte write and 10 byte read must each be split into
        // 4 + 4 + 2 separate SPI_IOC_MESSAGE(1) calls (write phase, then read phase).
        var stored = new ByteArrayOutputStream();
        var readPos = new AtomicInteger();
        var writeChunks = new AtomicInteger();
        var readChunks = new AtomicInteger();
        var maxChunkSize = new AtomicInteger();
        var spiTestData = new IoctlNativeMock.IoctlTestData(SpiTransferBuffer.class, (answer) -> {
            SpiTransferBuffer buffer = answer.getArgument(2);
            var tx = buffer.getTxBuffer();
            var rx = buffer.getRxBuffer();
            if (tx != null && tx.length > 0) {
                // write-only chunk (null rx): remember the bytes that were written
                writeChunks.incrementAndGet();
                maxChunkSize.accumulateAndGet(tx.length, Math::max);
                stored.write(tx, 0, tx.length);
                return new SpiTransferBuffer(tx, null, tx.length);
            }
            // read-only chunk (null tx): echo back the next slice of what was previously written
            readChunks.incrementAndGet();
            maxChunkSize.accumulateAndGet(rx.length, Math::max);
            var all = stored.toByteArray();
            var pos = readPos.getAndAdd(rx.length);
            var out = Arrays.copyOfRange(all, pos, pos + rx.length);
            return new SpiTransferBuffer(null, out, out.length);
        });
        var bufsiz = new FileDescriptorTestData(SPIDEV_BUFSIZ_PATH, 99, "4".getBytes());
        try (var _ = FileDescriptorNativeMock.setup(bufsiz);
             var _ = IoctlNativeMock.setup(spiTestData)) {

            var spi = pi4j.spi().create(SpiConfigBuilder.newInstance()
                .bus(SpiBus.BUS_0)
                .channel(7)
                .mode(0)
                .baud(50_000)
                .build());

            var write = new byte[10];
            for (var i = 0; i < write.length; i++) {
                write[i] = (byte) (i + 1);
            }
            var read = new byte[10];
            spi.writeThenRead(write, 0, write.length, 1000, read, 0, read.length);

            assertArrayEquals(write, read);
            // 10 bytes split into chunks of at most 4 bytes -> 4 + 4 + 2 = 3 calls per phase
            assertEquals(3, writeChunks.get());
            assertEquals(3, readChunks.get());
            assertTrue(maxChunkSize.get() <= 4, "no chunk may exceed the reported bufsiz");
        }
    }
}

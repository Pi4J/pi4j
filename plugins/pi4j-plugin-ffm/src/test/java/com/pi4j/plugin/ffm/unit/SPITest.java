package com.pi4j.plugin.ffm.unit;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.spi.SpiBus;
import com.pi4j.io.spi.SpiConfigBuilder;
import com.pi4j.plugin.ffm.common.PermissionHelper;
import com.pi4j.plugin.ffm.common.spi.SpiTransferBuffer;
import com.pi4j.plugin.ffm.mocks.FileDescriptorNativeMock;
import com.pi4j.plugin.ffm.mocks.IoctlNativeMock;
import com.pi4j.plugin.ffm.mocks.PermissionHelperMock;
import com.pi4j.plugin.ffm.providers.spi.SpiFFMProviderImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SPITest {
    private static Context pi4j;

    private static final MockedStatic<PermissionHelper> permissionHelperMock = PermissionHelperMock.echo();

    @BeforeAll
    public static void setup() {
        pi4j = Pi4J.newContextBuilder()
            .add(new SpiFFMProviderImpl())
            .build();

    }

    @AfterAll
    public static void teardown() {
        pi4j.shutdown();
        permissionHelperMock.close();
    }

    @Test
    public void testCreation() {
        try (var _ = FileDescriptorNativeMock.echo();
             var _ = IoctlNativeMock.echo()) {

            pi4j.spi().create(SpiConfigBuilder.newInstance(pi4j)
                .bus(SpiBus.BUS_0)
                .address(0)
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
        try (var _ = FileDescriptorNativeMock.echo();
             var _ = IoctlNativeMock.echo(spiTestData)) {

            var spi = pi4j.spi().create(SpiConfigBuilder.newInstance(pi4j)
                .bus(SpiBus.BUS_0)
                .address(1)
                .mode(0)
                .baud(50_000)
                .build());

            var result = spi.write((byte) 0x1C);
            assertEquals(1, result);

            result = spi.write("Test".getBytes(), 0, 4);
            assertEquals(4, result);
        }
    }

    @Test
    public void testRead() {
        var spiTestData = new IoctlNativeMock.IoctlTestData(SpiTransferBuffer.class, (answer) -> {
            SpiTransferBuffer buffer = answer.getArgument(2);
            if (buffer.getRxBuffer().length == 1) {
                return new SpiTransferBuffer(buffer.getTxBuffer(), "T".getBytes(), 1);
            } else {
                return new SpiTransferBuffer(buffer.getTxBuffer(), "Test".getBytes(), 4);
            }
        });
        try (var _ = FileDescriptorNativeMock.echo();
             var _ = IoctlNativeMock.echo(spiTestData)) {

            var spi = pi4j.spi().create(SpiConfigBuilder.newInstance(pi4j)
                .bus(SpiBus.BUS_0)
                .address(2)
                .mode(0)
                .baud(50_000)
                .build());

            var result = spi.read();
            assertEquals("T".getBytes()[0], result);

            var data = new byte[4];
            result = spi.read(data, 0, 4);
            assertEquals(4, result);
            assertArrayEquals("Test".getBytes(), data);
        }
    }

    @Test
    public void testSPITransfer() {
        var spiTestData = new IoctlNativeMock.IoctlTestData(SpiTransferBuffer.class, (answer) -> {
            SpiTransferBuffer buffer = answer.getArgument(2);
            return new SpiTransferBuffer(buffer.getTxBuffer(), buffer.getTxBuffer(), buffer.getTxBuffer().length);
        });
        try (var _ = FileDescriptorNativeMock.echo();
             var _ = IoctlNativeMock.echo(spiTestData)) {

            var spi = pi4j.spi().create(SpiConfigBuilder.newInstance(pi4j)
                .bus(SpiBus.BUS_0)
                .address(3)
                .mode(0)
                .baud(50_000)
                .build());
            var buffer = new byte[4];
            var result = spi.transfer("Test".getBytes(), 0, buffer, 0, 4);
            assertEquals(4, result);
            assertArrayEquals("Test".getBytes(), buffer);
        }
    }
}

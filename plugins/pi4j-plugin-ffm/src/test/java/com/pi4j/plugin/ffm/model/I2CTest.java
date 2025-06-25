package com.pi4j.plugin.ffm.model;

import com.pi4j.plugin.ffm.common.i2c.rdwr.I2CMessage;
import com.pi4j.plugin.ffm.common.i2c.rdwr.RDWRData;
import com.pi4j.plugin.ffm.common.i2c.smbus.SMBusData;
import com.pi4j.plugin.ffm.common.i2c.smbus.SMBusIoctlData;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;

import java.lang.foreign.Arena;

import static org.junit.jupiter.api.Assertions.*;

public class I2CTest {

    @Test
    public void testI2CMessage() throws Throwable {
        try (var offheap = Arena.ofConfined()) {
            var bytes = "Test".getBytes();
            var message = new I2CMessage(1,2, bytes.length, bytes);
            var buffer = offheap.allocate(I2CMessage.LAYOUT);
            message.to(buffer);
            var message2 = message.from(buffer);
            assertTrue(new ReflectionEquals(message2).matches(message));
        }
    }

    @Test
    public void testRDWRData() throws Throwable {
        try (var offheap = Arena.ofConfined()) {
            var bytes = "Test".getBytes();
            var message = new I2CMessage(1,2, bytes.length, bytes);
            var rdwrData = new RDWRData(new I2CMessage[] {message}, 1);
            var buffer = offheap.allocate(RDWRData.LAYOUT);
            rdwrData.to(buffer);
            var rdwrData2 = rdwrData.from(buffer);
            assertEquals(rdwrData.nmsgs(), rdwrData2.nmsgs());
            assertTrue(new ReflectionEquals(rdwrData2.msgs()).matches(rdwrData.msgs()));
        }
    }

    @Test
    public void testSMBusData() throws Throwable {
        try (var offheap = Arena.ofConfined()) {
            var bytes = "Test".getBytes();
            var data = new SMBusData((byte) 0,(short) 0, bytes);
            var buffer = offheap.allocate(SMBusData.LAYOUT);
            data.to(buffer);
            var data2 = data.from(buffer);
            assertTrue(new ReflectionEquals(data2).matches(data));
        }
    }

    @Test
    public void testSMBusIoctlData() throws Throwable {
        try (var offheap = Arena.ofConfined()) {
            var bytes = "Test".getBytes();
            var data = new SMBusData((byte) 0,(short) 0, bytes);
            var ioctlData = new SMBusIoctlData((byte) 1, (byte) 2, 1, data);
            var buffer = offheap.allocate(SMBusIoctlData.LAYOUT);
            ioctlData.to(buffer);
            var ioctlData2 = ioctlData.from(buffer);
            assertEquals(ioctlData2.readWrite(), ioctlData.readWrite());
            assertEquals(ioctlData2.command(), ioctlData.command());
            assertEquals(ioctlData2.size(), ioctlData.size());
            assertTrue(new ReflectionEquals(ioctlData2.data()).matches(ioctlData.data()));
        }
    }
}

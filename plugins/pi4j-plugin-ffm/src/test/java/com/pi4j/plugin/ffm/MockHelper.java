package com.pi4j.plugin.ffm;

import com.pi4j.plugin.ffm.common.file.FileDescriptorNative;
import com.pi4j.plugin.ffm.common.gpio.PinEvent;
import com.pi4j.plugin.ffm.common.gpio.PinFlag;
import com.pi4j.plugin.ffm.common.gpio.structs.*;
import com.pi4j.plugin.ffm.common.i2c.I2cConstants;
import com.pi4j.plugin.ffm.common.i2c.SMBusNative;
import com.pi4j.plugin.ffm.common.i2c.rdwr.I2CMessage;
import com.pi4j.plugin.ffm.common.i2c.rdwr.RDWRData;
import com.pi4j.plugin.ffm.common.ioctl.Command;
import com.pi4j.plugin.ffm.common.ioctl.IoctlNative;
import com.pi4j.plugin.ffm.common.poll.PollFlag;
import com.pi4j.plugin.ffm.common.poll.PollNative;
import com.pi4j.plugin.ffm.common.poll.structs.PollingData;
import com.pi4j.plugin.ffm.providers.i2c.I2CFunctionality;
import org.mockito.MockedConstruction;

import java.lang.foreign.Arena;
import java.nio.ByteBuffer;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class MockHelper {

    public static MockedConstruction<FileDescriptorNative> createFileMock() {
        return mockConstruction(FileDescriptorNative.class, (mock, _) -> {
            when(mock.open(anyString(), anyInt())).thenReturn(1);
            when(mock.read(anyInt(), any(byte[].class), anyInt())).thenReturn("Test".getBytes());
            when(mock.write(anyInt(), any(byte[].class))).thenReturn(42);
            doNothing().when(mock).close(anyInt());
        });
    }

    public static MockedConstruction<FileDescriptorNative> createDigitalInputFileMock() {
        return mockConstruction(FileDescriptorNative.class, (mock, _) -> {
            when(mock.open(anyString(), anyInt())).thenReturn(1);
            when(mock.read(anyInt(), any(byte[].class), anyInt())).thenAnswer((answer) -> {
                byte[] buffer = answer.getArgument(1);
                var lineEvent = new LineEvent(1, PinEvent.RISING.getValue(), 3, 4, 5);
                var memoryBuffer = Arena.ofAuto().allocate(LineEvent.LAYOUT);
                lineEvent.to(memoryBuffer);
                var lineBuffer = new byte[(int) LineEvent.LAYOUT.byteSize()];
                ByteBuffer.wrap(lineBuffer).put(memoryBuffer.asByteBuffer());
                System.arraycopy(lineBuffer, 0, buffer, 0, lineBuffer.length);
                return buffer;
            });
            when(mock.write(anyInt(), any(byte[].class))).thenReturn(42);
            doNothing().when(mock).close(anyInt());
        });
    }

    public static MockedConstruction<IoctlNative> createIoctlMock() {
        return mockConstruction(IoctlNative.class ,(mock, _) -> {
            //I2CSMBus
            when(mock.call(anyInt(), eq(Command.getI2CFuncs()), anyInt())).thenReturn(
                I2CFunctionality.I2C_FUNC_I2C.getValue() | I2CFunctionality.I2C_FUNC_SMBUS_BYTE_DATA.getValue()
                | I2CFunctionality.I2C_FUNC_SMBUS_BYTE.getValue() | I2CFunctionality.I2C_FUNC_SMBUS_WORD_DATA.getValue()
                | I2CFunctionality.I2C_FUNC_SMBUS_QUICK.getValue() | I2CFunctionality.I2C_FUNC_SMBUS_BLOCK_DATA.getValue()
            );
            when(mock.callByValue(anyInt(), eq(Command.getI2CSlave()), anyInt())).thenReturn(42);

            //I2CDirect
            when(mock.call(anyInt(), anyLong(), isA(RDWRData.class))).thenAnswer((answer) -> {
                RDWRData rdwr = answer.getArgument(2);
                if (rdwr.nmsgs() > 1) {
                    if (rdwr.msgs()[1].flags() == I2cConstants.I2C_M_RD.getValue()) {
                        return new RDWRData(new I2CMessage[]{
                            rdwr.msgs()[0],
                            new I2CMessage(rdwr.msgs()[1].address(), rdwr.msgs()[1].flags(), 4, "Test".getBytes())
                        }, rdwr.nmsgs());
                    } else {
                        return rdwr;
                    }
                } else {
                    if (rdwr.msgs()[0].flags() == I2cConstants.I2C_M_RD.getValue()) {
                        return new RDWRData(new I2CMessage[]{
                            new I2CMessage(rdwr.msgs()[0].address(), rdwr.msgs()[0].flags(), 1, new byte[] {(byte) 0xff})
                        }, rdwr.nmsgs());
                    } else {
                        return rdwr;
                    }
                }
            });

            //GPIO
            when(mock.call(anyInt(), anyLong(), isA(LineInfo.class))).thenAnswer((answer) -> {
                LineInfo lineInfo = answer.getArgument(2);
                if (lineInfo.offset() == 99) {
                    throw new IllegalStateException();
                }
                return new LineInfo("Test".getBytes(), "FFM-Test".getBytes(),
                    lineInfo.offset(), 0,
                    lineInfo.offset() == 2 ? PinFlag.USED.getValue() : PinFlag.OUTPUT.getValue(),
                    new LineAttribute[0]);
            });
            when(mock.call(anyInt(), anyLong(), isA(LineRequest.class))).thenAnswer((answer) -> {
                LineRequest lineRequest = answer.getArgument(2);
                return new LineRequest(lineRequest.offsets(), lineRequest.consumer(), lineRequest.config(), lineRequest.numLines(), lineRequest.eventBufferSize(), 42);
            });
            when(mock.call(anyInt(), anyLong(), isA(LineValues.class))).thenAnswer((answer) -> answer.<LineValues>getArgument(2));
        });
    }

    public static MockedConstruction<PollNative> createPollMock() {
        return mockConstruction(PollNative.class ,(mock, _) -> {
            when(mock.poll(isA(PollingData.class), anyInt(), anyInt())).thenAnswer((answer) -> {
                PollingData pollingData = answer.getArgument(0);
                return new PollingData(pollingData.fd(), pollingData.events(), (short) PollFlag.POLLIN);
            });
        });
    }

    public static MockedConstruction<SMBusNative> createSMBusMock() {
        return mockConstruction(SMBusNative.class ,(mock, _) -> {
            when(mock.writeByte(anyInt(), anyByte())).thenReturn(42);
            when(mock.writeBlockData(anyInt(), anyByte(), any(byte[].class))).thenReturn(42);
            when(mock.writeByteData(anyInt(), anyByte(), anyByte())).thenReturn(42);
            when(mock.writeWordData(anyInt(), anyByte(), anyInt())).thenReturn(42);

            when(mock.readByte(anyInt())).thenReturn((byte) 0xff);
            when(mock.readBlockData(anyInt(), anyByte(), any(byte[].class))).thenReturn("Test".getBytes());
            when(mock.readByteData(anyInt(), anyByte())).thenReturn((byte) 0xff);
            when(mock.readWordData(anyInt(), anyByte())).thenReturn(42);
        });
    }
}

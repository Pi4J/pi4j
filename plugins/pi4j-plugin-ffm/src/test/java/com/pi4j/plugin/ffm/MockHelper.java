package com.pi4j.plugin.ffm;

import com.pi4j.plugin.ffm.common.file.FileDescriptorNative;
import com.pi4j.plugin.ffm.common.gpio.PinEvent;
import com.pi4j.plugin.ffm.common.gpio.PinFlag;
import com.pi4j.plugin.ffm.common.gpio.structs.*;
import com.pi4j.plugin.ffm.common.ioctl.IoctlNative;
import com.pi4j.plugin.ffm.common.poll.PollFlag;
import com.pi4j.plugin.ffm.common.poll.PollNative;
import com.pi4j.plugin.ffm.common.poll.structs.PollingData;
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
}

package com.pi4j.plugin.ffm;

import com.pi4j.plugin.ffm.common.file.FileDescriptorNative;
import com.pi4j.plugin.ffm.common.gpio.PinFlag;
import com.pi4j.plugin.ffm.common.gpio.structs.LineAttribute;
import com.pi4j.plugin.ffm.common.gpio.structs.LineInfo;
import com.pi4j.plugin.ffm.common.gpio.structs.LineRequest;
import com.pi4j.plugin.ffm.common.gpio.structs.LineValues;
import com.pi4j.plugin.ffm.common.ioctl.IoctlNative;
import org.mockito.MockedConstruction;

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
}

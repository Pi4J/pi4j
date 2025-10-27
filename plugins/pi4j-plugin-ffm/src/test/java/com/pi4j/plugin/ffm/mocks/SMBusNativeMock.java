package com.pi4j.plugin.ffm.mocks;

import com.pi4j.plugin.ffm.common.i2c.SMBusNative;
import org.mockito.Answers;
import org.mockito.MockedConstruction;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class SMBusNativeMock {
    public static MockedConstruction<SMBusNative> setup() {
        return mockConstruction(SMBusNative.class , withSettings().defaultAnswer(Answers.RETURNS_MOCKS),(mock, _) -> {
            when(mock.writeByte(anyInt(), anyByte())).thenReturn(1);
            when(mock.writeBlockData(anyInt(), anyByte(), any(byte[].class))).thenAnswer((answer) -> {
                byte[] result = answer.getArgument(2);
                return result.length;
            });
            when(mock.writeByteData(anyInt(), anyByte(), anyByte())).thenReturn(1);
            when(mock.writeWordData(anyInt(), anyByte(), anyInt())).thenReturn(1);

            when(mock.readByte(anyInt())).thenReturn((byte) 0xff);
            when(mock.readBlockData(anyInt(), anyByte(), any(byte[].class))).thenReturn("Test".getBytes());
            when(mock.readByteData(anyInt(), anyByte())).thenReturn((byte) 0xff);
            when(mock.readWordData(anyInt(), anyByte())).thenReturn(0xff);
        });
    }
}

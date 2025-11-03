package com.pi4j.plugin.ffm.mocks;

import com.pi4j.plugin.ffm.common.Pi4JLayout;
import com.pi4j.plugin.ffm.common.gpio.structs.LineRequest;
import com.pi4j.plugin.ffm.common.gpio.structs.LineValues;
import com.pi4j.plugin.ffm.common.ioctl.Command;
import com.pi4j.plugin.ffm.common.ioctl.IoctlNative;
import org.mockito.MockedConstruction;
import org.mockito.invocation.InvocationOnMock;

import java.util.function.Function;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

public class IoctlNativeMock {

    public record IoctlTestData(Class<? extends Pi4JLayout> objClass, Function<InvocationOnMock, ?> callback) {
    }

    public static MockedConstruction<IoctlNative> setup(IoctlTestData... data) {
        return mockConstruction(IoctlNative.class, (mock, _) -> {
            for (IoctlTestData testData : data) {
                when(mock.call(anyInt(), anyLong(), isA(testData.objClass))).thenAnswer(testData.callback::apply);
            }
            when(mock.call(anyInt(), anyLong(), isA(LineValues.class))).thenAnswer((answer) -> answer.<LineValues>getArgument(2));
            when(mock.call(anyInt(), anyLong(), isA(LineRequest.class))).thenAnswer((answer) -> {
                LineRequest lineRequest = answer.getArgument(2);
                return new LineRequest(lineRequest.offsets(), lineRequest.consumer(), lineRequest.config(), lineRequest.numLines(), lineRequest.eventBufferSize(), 42);
            });
        });
    }

    public static MockedConstruction<IoctlNative> i2c(int i2CFunctionalities, IoctlTestData... data) {
        return mockConstruction(IoctlNative.class, (mock, _) -> {
            for (IoctlTestData testData : data) {
                when(mock.call(anyInt(), anyLong(), isA(testData.objClass))).thenAnswer(testData.callback::apply);
            }
            //I2CSMBus
            when(mock.call(anyInt(), eq(Command.getI2CFuncs()), anyInt())).thenReturn(i2CFunctionalities);
            when(mock.callByValue(anyInt(), eq(Command.getI2CSlave()), anyInt())).thenReturn(42);
        });
    }
}

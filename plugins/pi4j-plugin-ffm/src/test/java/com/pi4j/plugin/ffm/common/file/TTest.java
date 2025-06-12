package com.pi4j.plugin.ffm.common.file;


import com.pi4j.Pi4J;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.plugin.ffm.common.gpio.PinFlag;
import com.pi4j.plugin.ffm.common.gpio.structs.LineAttribute;
import com.pi4j.plugin.ffm.common.gpio.structs.LineInfo;
import com.pi4j.plugin.ffm.common.gpio.structs.LineRequest;
import com.pi4j.plugin.ffm.common.gpio.structs.LineValues;
import com.pi4j.plugin.ffm.common.ioctl.IoctlNative;
import com.pi4j.plugin.ffm.providers.gpio.DigitalInputFFMProviderImpl;
import com.pi4j.plugin.ffm.providers.gpio.DigitalOutputFFM;
import com.pi4j.plugin.ffm.providers.gpio.DigitalOutputFFMProviderImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class TTest {

    @Test
    public void mockFile() {
        var pi4j0 = Pi4J.newContextBuilder()
            .add(new DigitalOutputFFMProviderImpl())
            .setGpioChipName("null")
            .build();
        try (
            var a = mockConstruction(FileDescriptorNative.class, (mock, _) -> {
                when(mock.open(anyString(), anyInt())).thenReturn(1);
                doNothing().when(mock).close(anyInt());
            });
            var b = mockConstruction(IoctlNative.class, withSettings().useConstructor().defaultAnswer(Answers.RETURNS_MOCKS) ,(mock, _) -> {
                when(mock.call(anyInt(), anyLong(), isA(LineInfo.class))).thenAnswer((answer) -> {
                    LineInfo lineInfo = answer.getArgument(2);
                    return new LineInfo("Test".getBytes(), "FFM-Test".getBytes(), lineInfo.offset(), 0, PinFlag.OUTPUT.getValue(), new LineAttribute[0], new int[0]);
                });
                when(mock.call(anyInt(), anyLong(), isA(LineRequest.class))).thenAnswer((answer) -> {
                    LineRequest lineRequest = answer.getArgument(2);
                    return new LineRequest(lineRequest.offsets(), lineRequest.consumer(), lineRequest.config(), lineRequest.numLines(), lineRequest.eventBufferSize(), lineRequest.padding(), 42);
                });
                when(mock.call(anyInt(), anyLong(), isA(LineValues.class))).thenAnswer((answer) -> answer.<LineValues>getArgument(2));
            })
        ) {
            var pin = pi4j0.digitalOutput().create(5);
            pin.state(DigitalState.HIGH);
            assertEquals(DigitalState.HIGH, pin.state());
            pin.state(DigitalState.LOW);
            assertEquals(DigitalState.LOW, pin.state());
            pi4j0.shutdown();
        }
    }

}

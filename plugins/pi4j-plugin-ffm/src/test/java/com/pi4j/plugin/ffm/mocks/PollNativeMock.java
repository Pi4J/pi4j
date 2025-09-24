package com.pi4j.plugin.ffm.mocks;

import com.pi4j.plugin.ffm.common.poll.PollNative;
import com.pi4j.plugin.ffm.common.poll.structs.PollingData;
import org.mockito.MockedConstruction;
import org.mockito.invocation.InvocationOnMock;

import java.util.function.Function;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

public class PollNativeMock {
    public static MockedConstruction<PollNative> echo(Function<InvocationOnMock, PollingData> callback) {
        return mockConstruction(PollNative.class ,(mock, _) -> {
            when(mock.poll(isA(PollingData.class), anyInt(), anyInt())).thenAnswer(callback::apply);
        });
    }
}

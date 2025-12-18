package com.pi4j.plugin.ffm.mocks;

import com.pi4j.plugin.ffm.common.file.FileDescriptorNative;
import org.mockito.AdditionalMatchers;
import org.mockito.Answers;
import org.mockito.MockedConstruction;
import org.mockito.invocation.InvocationOnMock;

import java.io.File;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class FileDescriptorNativeMock {
    public record FileDescriptorTestData(String device, int fd, byte[] data, Function<InvocationOnMock, ?> callback) {
        public FileDescriptorTestData(String device, int fd, byte[] data) {
            this(device, fd, data, null);
        }
    }

    public static MockedConstruction<FileDescriptorNative> echo(FileDescriptorTestData... descriptors) {
        return mockConstruction(FileDescriptorNative.class, (mock, _) -> {
            for (FileDescriptorTestData descriptor : descriptors) {
                when(mock.open(matches(descriptor.device), anyInt())).thenReturn(descriptor.fd);
                if (descriptor.callback != null) {
                    when(mock.read(eq(descriptor.fd), any(byte[].class), anyInt())).thenAnswer(descriptor.callback::apply);
                } else {
                    when(mock.read(eq(descriptor.fd), any(byte[].class), anyInt())).thenReturn(descriptor.data);
                }
                when(mock.write(eq(descriptor.fd), AdditionalMatchers.aryEq(descriptor.data))).thenReturn(descriptor.data.length);
                doNothing().when(mock).close(eq(descriptor.fd));
                when(mock.access(anyString(), anyInt())).thenReturn(0);
            }
        });
    }
}

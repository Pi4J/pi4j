package com.pi4j.plugin.ffm.unit;

import com.pi4j.exception.Pi4JException;
import com.pi4j.extension.PluginService;
import com.pi4j.plugin.ffm.FFMPlugin;
import com.pi4j.plugin.ffm.common.FFMPermissionHelper;
import com.pi4j.plugin.ffm.providers.gpio.FFMDigitalInputProviderImpl;
import com.pi4j.plugin.ffm.providers.gpio.FFMDigitalOutputProviderImpl;
import com.pi4j.plugin.ffm.providers.parallel.FFMParallelPortProvider;
import com.pi4j.plugin.ffm.providers.pwm.FFMPwmProviderImpl;
import com.pi4j.provider.Provider;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/** The plugin registers every provider whose permission check passes, not all or nothing. */
public class FFMPluginTest {

    private static List<String> registeredIds(PluginService service) {
        var captor = ArgumentCaptor.forClass(Provider[].class);
        verify(service).register(captor.capture());
        return Arrays.stream(captor.getValue()).map(Provider::id).toList();
    }

    @Test
    public void registersAllProvidersWhenEveryCheckPasses() {
        try (var permissions = mockStatic(FFMPermissionHelper.class)) {
            permissions.when(() -> FFMPermissionHelper.checkUserPermissions(any())).thenAnswer((_) -> null);
            var service = mock(PluginService.class);

            new FFMPlugin().initialize(service);

            assertEquals(List.of("ffm-digital-input", "ffm-digital-output", "ffm-i2c", "ffm-spi", "ffm-pwm", "ffm-parallel-port"),
                registeredIds(service));
        }
    }

    @Test
    public void keepsTheOtherProvidersWhenOneGroupIsMissing() {
        try (var permissions = mockStatic(FFMPermissionHelper.class)) {
            // the user is in i2c and spi, but not in gpio/dialout
            permissions.when(() -> FFMPermissionHelper.checkUserPermissions(any())).thenAnswer(invocation -> {
                Object provider = invocation.getArgument(0);
                if (provider instanceof FFMDigitalInputProviderImpl || provider instanceof FFMDigitalOutputProviderImpl
                    || provider instanceof FFMPwmProviderImpl || provider instanceof FFMParallelPortProvider) {
                    throw new Pi4JException("Current user 'test' is not member of any of the groups [gpio, dialout]");
                }
                return null;
            });
            var service = mock(PluginService.class);

            new FFMPlugin().initialize(service);

            assertEquals(List.of("ffm-i2c", "ffm-spi"), registeredIds(service));
        }
    }

    @Test
    public void failsAsAWholeOnlyWhenNoProviderCanBeCreated() {
        try (var permissions = mockStatic(FFMPermissionHelper.class)) {
            permissions.when(() -> FFMPermissionHelper.checkUserPermissions(any()))
                .thenThrow(new Pi4JException("Current user 'test' is not member of any of the groups"));
            var service = mock(PluginService.class);

            var failure = assertThrows(Pi4JException.class, () -> new FFMPlugin().initialize(service));

            assertTrue(failure.getMessage().contains("ffm-i2c"), failure.getMessage());
            verify(service, never()).register(any(Provider[].class));
        }
    }
}

package com.pi4j.plugin.ffm.mocks;

import com.pi4j.plugin.ffm.common.FFMPermissionHelper;
import org.mockito.MockedStatic;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;

public class PermissionHelperMock {
    public static MockedStatic<FFMPermissionHelper> echo() {
        var mock = mockStatic(FFMPermissionHelper.class);
        mock.when(() -> FFMPermissionHelper.checkDevicePermissions(anyString(), any())).thenAnswer((_) -> null);
        mock.when(() -> FFMPermissionHelper.checkUserPermissions(any())).thenAnswer((_) -> null);
        return mock;
    }
}

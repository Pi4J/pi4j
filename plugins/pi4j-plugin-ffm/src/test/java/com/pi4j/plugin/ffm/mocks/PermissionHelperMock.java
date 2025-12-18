package com.pi4j.plugin.ffm.mocks;

import com.pi4j.plugin.ffm.common.PermissionHelper;
import org.mockito.MockedStatic;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;

public class PermissionHelperMock {
    public static MockedStatic<PermissionHelper> echo() {
        var mock = mockStatic(PermissionHelper.class);
        mock.when(() -> PermissionHelper.checkDevicePermissions(anyString(), any())).thenAnswer((_) -> null);
        mock.when(() -> PermissionHelper.checkUserPermissions(any())).thenAnswer((_) -> null);
        return mock;
    }
}

package com.pi4j.plugin.ffm.mocks;

import com.pi4j.plugin.ffm.common.PermissionHelper;
import org.mockito.MockedStatic;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;

public class PermissionHelperMock {
    public static MockedStatic<PermissionHelper> echo() {
        var mock = mockStatic(PermissionHelper.class);
        mock.when(() -> PermissionHelper.checkDevice(anyString())).thenAnswer((_) -> null);
        mock.when(PermissionHelper::checkUser).thenAnswer((_) -> null);
        return mock;
    }
}

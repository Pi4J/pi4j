package com.pi4j.plugin.ffm.mocks;

import com.pi4j.plugin.ffm.common.PermissionHelper;
import org.mockito.MockedStatic;

import static org.mockito.Mockito.*;

public class PermissionHelperMock {
    public static MockedStatic<PermissionHelper> echo() {
        return mockStatic(PermissionHelper.class);
    }
}

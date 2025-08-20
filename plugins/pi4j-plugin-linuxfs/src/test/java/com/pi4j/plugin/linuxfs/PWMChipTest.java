package com.pi4j.plugin.linuxfs;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PWMChipTest {

    @Test
    void shouldReturnCorrectPWMChipForRP1Paths() {
        String[] paths = new String[]{"1f00098000"};
        assertEquals(1, LinuxFsPlugin.parsePWMPaths(paths));
    }
}

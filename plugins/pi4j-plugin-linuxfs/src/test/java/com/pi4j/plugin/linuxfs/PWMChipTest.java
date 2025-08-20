package com.pi4j.plugin.linuxfs;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.pi4j.plugin.linuxfs.provider.pwm.LinuxFsPwmUtil.parsePWMPaths;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PWMChipTest {

    @Test
    void shouldReturnCorrectPWMChipForRP1Paths() {
        String[] paths = new String[]{"total 0  ",
            "lrwxrwxrwx 1root root 0Aug 20 07:51 pwmchip42 ->../../devices / platform / axi / 1000120000. pcie / 1f00098000. pwm / pwm / pwmchip0",
        "lrwxrwxrwx 1 root root 0 Aug 20 07:51 pwmchip1 ->../../devices / platform / axi / 1000120000. pcie / 1f 0009c000.pwm / pwm / pwmchip1"};

        Optional<Optional<Integer>> optionalInt = Optional.of( parsePWMPaths(paths));
        assertEquals(42, optionalInt.get().get());

    }
}

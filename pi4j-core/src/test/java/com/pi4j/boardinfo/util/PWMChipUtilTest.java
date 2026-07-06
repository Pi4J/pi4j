package com.pi4j.boardinfo.util;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static com.pi4j.boardinfo.util.PwmChipUtil.parsePWMPaths;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PWMChipUtilTest {

    @Test
    void shouldReturnCorrectPWMChipForRP1Paths() {
        var paths = List.of("total 0  ",
            "../../devices/platform/axi/1000120000.pcie/1f00098000.pwm/pwm/pwmchip42",
            "../../devices/platform/axi/1000120000.pcie/1f0009c000.pwm/pwm/pwmchip1"
        );

        Optional<Optional<Integer>> optionalInt = Optional.of(parsePWMPaths(paths));
        assertEquals(42, optionalInt.get().get());
    }
}

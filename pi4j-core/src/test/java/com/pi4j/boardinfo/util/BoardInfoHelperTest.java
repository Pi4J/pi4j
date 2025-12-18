package com.pi4j.boardinfo.util;

import com.pi4j.boardinfo.definition.BoardModel;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BoardInfoHelperTest {

    @Test
    void shouldReturnCorrectBcmNumbersFromPhysical() {
        BoardInfoHelper.current().setBoardModel(BoardModel.MODEL_5_B);

        assertAll(
            () -> assertEquals(14, BoardInfoHelper.getBcmFromPhysical(8).get()),
            () -> assertEquals(15, BoardInfoHelper.getBcmFromPhysical(10).get()),
            () -> assertEquals(18, BoardInfoHelper.getBcmFromPhysical(12).get()),
            () -> assertEquals(26, BoardInfoHelper.getBcmFromPhysical(37).get()),
            () -> assertEquals(21, BoardInfoHelper.getBcmFromPhysical(40).get()),
            () -> assertEquals(Optional.empty(), BoardInfoHelper.getBcmFromPhysical(30)), // Ground
            () -> assertEquals(Optional.empty(), BoardInfoHelper.getBcmFromPhysical(2)), // 5V
            () -> assertEquals(Optional.empty(), BoardInfoHelper.getBcmFromPhysical(42)) // Not existing
        );
    }

    @Test
    void shouldReturnCorrectBcmNumbersFromWiringPi() {
        BoardInfoHelper.current().setBoardModel(BoardModel.MODEL_5_B);

        assertAll(
            () -> assertEquals(14, BoardInfoHelper.getBcmFromWiringPi(15).get()),
            () -> assertEquals(15, BoardInfoHelper.getBcmFromWiringPi(16).get()),
            () -> assertEquals(18, BoardInfoHelper.getBcmFromWiringPi(1).get()),
            () -> assertEquals(26, BoardInfoHelper.getBcmFromWiringPi(25).get()),
            () -> assertEquals(21, BoardInfoHelper.getBcmFromWiringPi(29).get()),
            () -> assertEquals(Optional.empty(), BoardInfoHelper.getBcmFromWiringPi(99)) // Not existing
        );
    }
}

package com.pi4j.boardinfo.definition;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardModelTest {

    @Test
    void getBoardModelByBoardCode() {
        assertAll(
            () -> assertEquals(BoardModel.MODEL_5_B, BoardModel.getByBoardCode("d04170")),
            () -> assertEquals(BoardModel.MODEL_400, BoardModel.getByBoardCode("c03130")),
            () -> assertEquals(BoardModel.MODEL_4_B, BoardModel.getByBoardCode("a03111")),
            () -> assertEquals(BoardModel.MODEL_4_B, BoardModel.getByBoardCode("c03112")),
            () -> assertEquals(BoardModel.ZERO_V2, BoardModel.getByBoardCode("902120")),
            () -> assertEquals(BoardModel.MODEL_2_B_V1_2, BoardModel.getByBoardCode("a02042")),
            () -> assertEquals(BoardModel.MODEL_2_B, BoardModel.getByBoardCode("a21041"))
        );
    }

    @Test
    void getBoardModelByBoardName() {
        assertAll(
            () -> assertEquals(BoardModel.MODEL_4_B, BoardModel.getByBoardName("Raspberry Pi 4 Model B Rev 1.1"))
        );
    }

    @Test
    void validateInstructionSetPico() {
        assertAll(
            () -> assertEquals(InstructionSet.ARM_V6_M, BoardModel.PICO.getSoc().getInstructionSet()),
            () -> assertEquals(InstructionSet.ARM_V6_M, BoardModel.PICO_2.getSoc().getInstructionSet())
        );
    }

    @Test
    void boardCodesMustBeUnique() {
        var codes = BoardModel.getAllBoardCodes();
        for (String code : codes) {
            assertDoesNotThrow(() -> {
                BoardModel.getByBoardCode(code);
            });
        }
    }
}

package com.pi4j.boardinfo.definition;

import com.pi4j.boardinfo.model.BoardInfo;
import com.pi4j.boardinfo.model.JavaInfo;
import com.pi4j.boardinfo.model.OperatingSystem;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardModelTest {

    @Test
    void getBoardModelByRevisionCode() {
        assertAll(
            () -> assertEquals(BoardModel.MODEL_1_A_PLUS, BoardModel.getByRevisionCode(RevisionCode.of("900021"))),
            () -> assertEquals(BoardModel.MODEL_1_B_PLUS, BoardModel.getByRevisionCode(RevisionCode.of("900032"))),
            () -> assertEquals(BoardModel.MODEL_2_B, BoardModel.getByRevisionCode(RevisionCode.of("a01040"))),
            () -> assertEquals(BoardModel.MODEL_2_B, BoardModel.getByRevisionCode(RevisionCode.of("a01041"))),
            () -> assertEquals(BoardModel.MODEL_2_B, BoardModel.getByRevisionCode(RevisionCode.of("a21041"))),
            () -> assertEquals(BoardModel.MODEL_2_B_V1_2, BoardModel.getByRevisionCode(RevisionCode.of("a02042"))),
            () -> assertEquals(BoardModel.MODEL_2_B_V1_2, BoardModel.getByRevisionCode(RevisionCode.of("a22042"))),
            () -> assertEquals(BoardModel.COMPUTE_1, BoardModel.getByRevisionCode(RevisionCode.of("900061"))),
            () -> assertEquals(BoardModel.MODEL_3_B, BoardModel.getByRevisionCode(RevisionCode.of("a02082"))),
            () -> assertEquals(BoardModel.MODEL_3_B, BoardModel.getByRevisionCode(RevisionCode.of("a22082"))),
            () -> assertEquals(BoardModel.MODEL_3_B, BoardModel.getByRevisionCode(RevisionCode.of("a32082"))),
            () -> assertEquals(BoardModel.MODEL_3_B, BoardModel.getByRevisionCode(RevisionCode.of("a52082"))),
            () -> assertEquals(BoardModel.MODEL_3_B, BoardModel.getByRevisionCode(RevisionCode.of("a22083"))),
            () -> assertEquals(BoardModel.ZERO_PCB_1_2, BoardModel.getByRevisionCode(RevisionCode.of("900092"))),
            () -> assertEquals(BoardModel.ZERO_PCB_1_2, BoardModel.getByRevisionCode(RevisionCode.of("920092"))),
            () -> assertEquals(BoardModel.ZERO_PCB_1_3, BoardModel.getByRevisionCode(RevisionCode.of("900093"))),
            () -> assertEquals(BoardModel.ZERO_PCB_1_3, BoardModel.getByRevisionCode(RevisionCode.of("920093"))),
            () -> assertEquals(BoardModel.COMPUTE_3, BoardModel.getByRevisionCode(RevisionCode.of("a020a0"))),
            () -> assertEquals(BoardModel.COMPUTE_3, BoardModel.getByRevisionCode(RevisionCode.of("a220a0"))),
            () -> assertEquals(BoardModel.ZERO_W, BoardModel.getByRevisionCode(RevisionCode.of("9000c1"))),
            () -> assertEquals(BoardModel.MODEL_3_B_PLUS, BoardModel.getByRevisionCode(RevisionCode.of("a020d3"))),
            () -> assertEquals(BoardModel.MODEL_3_B_PLUS, BoardModel.getByRevisionCode(RevisionCode.of("a020d4"))),
            () -> assertEquals(BoardModel.MODEL_3_A_PLUS, BoardModel.getByRevisionCode(RevisionCode.of("9020e0"))),
            () -> assertEquals(BoardModel.MODEL_3_A_PLUS, BoardModel.getByRevisionCode(RevisionCode.of("9020e1"))),
            () -> assertEquals(BoardModel.COMPUTE_3_PLUS, BoardModel.getByRevisionCode(RevisionCode.of("a02100"))),
            () -> assertEquals(BoardModel.MODEL_4_B, BoardModel.getByRevisionCode(RevisionCode.of("a03111"))),
            () -> assertEquals(BoardModel.MODEL_4_B, BoardModel.getByRevisionCode(RevisionCode.of("b03111"))),
            () -> assertEquals(BoardModel.MODEL_4_B, BoardModel.getByRevisionCode(RevisionCode.of("c03111"))),
            () -> assertEquals(BoardModel.MODEL_4_B, BoardModel.getByRevisionCode(RevisionCode.of("b03112"))),
            () -> assertEquals(BoardModel.MODEL_4_B, BoardModel.getByRevisionCode(RevisionCode.of("c03112"))),
            () -> assertEquals(BoardModel.MODEL_4_B, BoardModel.getByRevisionCode(RevisionCode.of("b03114"))),
            () -> assertEquals(BoardModel.MODEL_4_B, BoardModel.getByRevisionCode(RevisionCode.of("c03114"))),
            () -> assertEquals(BoardModel.MODEL_4_B, BoardModel.getByRevisionCode(RevisionCode.of("d03114"))),
            () -> assertEquals(BoardModel.MODEL_4_B, BoardModel.getByRevisionCode(RevisionCode.of("b03115"))),
            () -> assertEquals(BoardModel.MODEL_4_B, BoardModel.getByRevisionCode(RevisionCode.of("c03115"))),
            () -> assertEquals(BoardModel.MODEL_4_B, BoardModel.getByRevisionCode(RevisionCode.of("d03115"))),
            () -> assertEquals(BoardModel.ZERO_V2, BoardModel.getByRevisionCode(RevisionCode.of("902120"))),
            () -> assertEquals(BoardModel.MODEL_400, BoardModel.getByRevisionCode(RevisionCode.of("c03130"))),
            () -> assertEquals(BoardModel.COMPUTE_4, BoardModel.getByRevisionCode(RevisionCode.of("a03140"))),
            () -> assertEquals(BoardModel.COMPUTE_4, BoardModel.getByRevisionCode(RevisionCode.of("b03140"))),
            () -> assertEquals(BoardModel.COMPUTE_4, BoardModel.getByRevisionCode(RevisionCode.of("c03140"))),
            () -> assertEquals(BoardModel.COMPUTE_4, BoardModel.getByRevisionCode(RevisionCode.of("d03140"))),
            () -> assertEquals(BoardModel.COMPUTE_4, BoardModel.getByRevisionCode(RevisionCode.of("a03141"))),
            () -> assertEquals(BoardModel.COMPUTE_4, BoardModel.getByRevisionCode(RevisionCode.of("b03141"))),
            () -> assertEquals(BoardModel.COMPUTE_4, BoardModel.getByRevisionCode(RevisionCode.of("c03141"))),
            () -> assertEquals(BoardModel.COMPUTE_4, BoardModel.getByRevisionCode(RevisionCode.of("d03141"))),
            () -> assertEquals(BoardModel.MODEL_5_B, BoardModel.getByRevisionCode(RevisionCode.of("b04170"))),
            () -> assertEquals(BoardModel.MODEL_5_B, BoardModel.getByRevisionCode(RevisionCode.of("c04170"))),
            () -> assertEquals(BoardModel.MODEL_5_B, BoardModel.getByRevisionCode(RevisionCode.of("d04170"))),
            () -> assertEquals(BoardModel.MODEL_5_B, BoardModel.getByRevisionCode(RevisionCode.of("a04171"))),
            () -> assertEquals(BoardModel.MODEL_5_B, BoardModel.getByRevisionCode(RevisionCode.of("b04171"))),
            () -> assertEquals(BoardModel.MODEL_5_B, BoardModel.getByRevisionCode(RevisionCode.of("c04171"))),
            () -> assertEquals(BoardModel.MODEL_5_B, BoardModel.getByRevisionCode(RevisionCode.of("d04171"))),
            () -> assertEquals(BoardModel.MODEL_5_B, BoardModel.getByRevisionCode(RevisionCode.of("e04171"))),
            () -> assertEquals(BoardModel.COMPUTE_5, BoardModel.getByRevisionCode(RevisionCode.of("b04180"))),
            () -> assertEquals(BoardModel.COMPUTE_5, BoardModel.getByRevisionCode(RevisionCode.of("c04180"))),
            () -> assertEquals(BoardModel.COMPUTE_5, BoardModel.getByRevisionCode(RevisionCode.of("d04180"))),
            () -> assertEquals(BoardModel.COMPUTE_5, BoardModel.getByRevisionCode(RevisionCode.of("e04180"))),
            () -> assertEquals(BoardModel.MODEL_500, BoardModel.getByRevisionCode(RevisionCode.of("d04190"))),
            () -> assertEquals(BoardModel.MODEL_500_PLUS, BoardModel.getByRevisionCode(RevisionCode.of("e04190"))),
            () -> assertEquals(BoardModel.COMPUTE_5, BoardModel.getByRevisionCode(RevisionCode.of("b041a0"))),
            () -> assertEquals(BoardModel.COMPUTE_5, BoardModel.getByRevisionCode(RevisionCode.of("c041a0"))),
            () -> assertEquals(BoardModel.COMPUTE_5, BoardModel.getByRevisionCode(RevisionCode.of("d041a0"))),
            () -> assertEquals(BoardModel.COMPUTE_5, BoardModel.getByRevisionCode(RevisionCode.of("e041a0"))),
            () -> assertEquals(BoardModel.MODEL_1_B, BoardModel.getByRevisionCode(RevisionCode.of("0002"))),
            () -> assertEquals(BoardModel.MODEL_1_B, BoardModel.getByRevisionCode(RevisionCode.of("0003"))),
            () -> assertEquals(BoardModel.MODEL_1_B, BoardModel.getByRevisionCode(RevisionCode.of("0004"))),
            () -> assertEquals(BoardModel.MODEL_1_B, BoardModel.getByRevisionCode(RevisionCode.of("0005"))),
            () -> assertEquals(BoardModel.MODEL_1_B, BoardModel.getByRevisionCode(RevisionCode.of("0006"))),
            () -> assertEquals(BoardModel.MODEL_1_A, BoardModel.getByRevisionCode(RevisionCode.of("0007"))),
            () -> assertEquals(BoardModel.MODEL_1_A, BoardModel.getByRevisionCode(RevisionCode.of("0008"))),
            () -> assertEquals(BoardModel.MODEL_1_A, BoardModel.getByRevisionCode(RevisionCode.of("0009"))),
            () -> assertEquals(BoardModel.MODEL_1_B, BoardModel.getByRevisionCode(RevisionCode.of("000d"))),
            () -> assertEquals(BoardModel.MODEL_1_B, BoardModel.getByRevisionCode(RevisionCode.of("000e"))),
            () -> assertEquals(BoardModel.MODEL_1_B, BoardModel.getByRevisionCode(RevisionCode.of("000f"))),
            () -> assertEquals(BoardModel.MODEL_1_B_PLUS, BoardModel.getByRevisionCode(RevisionCode.of("0010"))),
            () -> assertEquals(BoardModel.COMPUTE_1, BoardModel.getByRevisionCode(RevisionCode.of("0011"))),
            () -> assertEquals(BoardModel.MODEL_1_A_PLUS, BoardModel.getByRevisionCode(RevisionCode.of("0012"))),
            () -> assertEquals(BoardModel.MODEL_1_B_PLUS, BoardModel.getByRevisionCode(RevisionCode.of("0013"))),
            () -> assertEquals(BoardModel.COMPUTE_1, BoardModel.getByRevisionCode(RevisionCode.of("0014"))),
            () -> assertEquals(BoardModel.MODEL_1_A_PLUS, BoardModel.getByRevisionCode(RevisionCode.of("0015"))),
            () -> assertEquals(BoardModel.UNKNOWN, BoardModel.getByRevisionCode(RevisionCode.of("ffffffff")))
        );
    }

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

    @Test
    void testBoardInfo() {
        var model = BoardModel.MODEL_4_B;
        var boardInfo = new BoardInfo(
            model,
            new OperatingSystem(
                "Linux",
                "5.4.0",
                "arm64"),
            new JavaInfo(
                "11.0.8",
                "OpenJDK",
                "Oracle",
                "11.0.8"
            )
        );

        assertAll(
            () -> assertEquals("Linux", boardInfo.getOperatingSystem().getName()),
            () -> assertEquals("5.4.0", boardInfo.getOperatingSystem().getVersion()),
            () -> assertEquals("arm64", boardInfo.getOperatingSystem().getArchitecture()),
            () -> assertEquals("11.0.8", boardInfo.getJavaInfo().getVersion()),
            () -> assertEquals("OpenJDK", boardInfo.getJavaInfo().getRuntime())
        );
    }
}

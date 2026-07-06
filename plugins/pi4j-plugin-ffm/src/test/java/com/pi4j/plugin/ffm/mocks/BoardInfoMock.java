package com.pi4j.plugin.ffm.mocks;

import com.pi4j.boardinfo.definition.BoardModel;
import com.pi4j.boardinfo.model.BoardInfo;
import org.mockito.MockedConstruction;

import static org.mockito.Mockito.*;

public class BoardInfoMock {
    public static MockedConstruction<BoardInfo> setup(BoardModel boardModel) {
        return mockConstruction(BoardInfo.class, (mock, _) -> {
            when(mock.getBoardModel()).thenReturn(boardModel);
        });
    }
}

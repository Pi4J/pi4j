package com.pi4j.boardinfo.model;

import com.pi4j.boardinfo.definition.BoardModel;

/**
 * Represents information about a specific board, including its model,
 * operating system, and Java environment details.
 */
public class BoardInfo {

    private BoardModel boardModel;
    private final OperatingSystem operatingSystem;
    private final JavaInfo javaInfo;

    /**
     * Constructs a {@link BoardInfo} instance with the specified board model, operating system,
     * and Java environment information.
     *
     * @param boardModel the {@link BoardModel} representing the board's model.
     * @param operatingSystem the {@link OperatingSystem} information for the board.
     * @param javaInfo the {@link JavaInfo} information related to the board's Java environment.
     */
    public BoardInfo(BoardModel boardModel, OperatingSystem operatingSystem, JavaInfo javaInfo) {
        this.boardModel = boardModel;
        this.operatingSystem = operatingSystem;
        this.javaInfo = javaInfo;
    }

    /**
     * Sets the model of the board. To be used when the detected board is not correct,
     * or to force a specific model e.g. on Orange Pi, or during testing.
     *
     * @param boardModel the {@link BoardModel} to use, overriding any automatically detected model.
     */
    public void setBoardModel(BoardModel boardModel) {
        this.boardModel = boardModel;
    }

    /**
     * Gets the model of the board.
     *
     * @return the {@link BoardModel} of the board.
     */
    public BoardModel getBoardModel() {
        return boardModel;
    }

    /**
     * Gets the operating system running on the board.
     *
     * @return the {@link OperatingSystem} of the board.
     */
    public OperatingSystem getOperatingSystem() {
        return operatingSystem;
    }

    /**
     * Gets the Java environment information for the board.
     *
     * @return the {@link JavaInfo} related to the board.
     */
    public JavaInfo getJavaInfo() {
        return javaInfo;
    }
}

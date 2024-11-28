package com.pi4j.boardinfo.model;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  BoardInfo.java
 *
 * This file is part of the Pi4J project. More information about
 * this project can be found here:  https://pi4j.com/
 * **********************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.pi4j.boardinfo.definition.BoardModel;

/**
 * Represents information about a specific board, including its model,
 * operating system, and Java environment details.
 */
public class BoardInfo {

    private final BoardModel boardModel;
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

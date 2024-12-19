package com.pi4j.io.onewire;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  OneWireFileDataReaderWriter.java
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

import com.pi4j.io.exception.IOException;

public interface OneWireFileDataReaderWriter {
    /**
     * Read the entire content of a file on the device.
     *
     * @param fileName The name of the file to read.
     * @return The file content as array.
     * @throws IOException if the file does not exist, is not readable, or another I/O error occurs.
     */
    String[] readFile(String fileName) throws IOException;

    /**
     * Read a single line from a file on the device.
     *
     * @param fileName The name of the file to read.
     * @return The first line of the file content as a string.
     * @throws IOException if the file does not exist, is not readable, or another I/O error occurs.
     */
    String readFileLine(String fileName) throws IOException;

    /**
     * Write a single byte to a file on the device.
     *
     * @param fileName The name of the file to write to.
     * @param value The byte value to write.
     * @throws IOException if the file does not exist, is not writable, or another I/O error occurs.
     */
    void writeFile(String fileName, byte value) throws IOException;

    /**
     * Write multiple bytes to a file on the device.
     *
     * @param fileName The name of the file to write to.
     * @param values The byte array to write.
     * @throws IOException if the file does not exist, is not writable, or another I/O error occurs.
     */
    void writeFile(String fileName, byte[] values) throws IOException;

    /**
     * Write a string to a file on the device.
     *
     * @param fileName The name of the file to write to.
     * @param content The string content to write.
     * @throws IOException if the file does not exist, is not writable, or another I/O error occurs.
     */
    void writeFile(String fileName, String content) throws IOException;
}

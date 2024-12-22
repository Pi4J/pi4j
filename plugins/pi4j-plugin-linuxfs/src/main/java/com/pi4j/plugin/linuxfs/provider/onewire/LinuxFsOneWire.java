package com.pi4j.plugin.linuxfs.provider.onewire;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: PLUGIN   :: LinuxFS I/O Providers
 * FILENAME      :  LinuxFsOneWire.java
 *
 * This file is part of the Pi4J project. More information about
 * this project can be found here:  https://pi4j.com/
 * **********************************************************************
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import com.pi4j.io.exception.IOException;
import com.pi4j.io.onewire.OneWire;
import com.pi4j.io.onewire.OneWireBase;
import com.pi4j.io.onewire.OneWireConfig;
import com.pi4j.io.onewire.OneWireProvider;
import com.pi4j.plugin.linuxfs.internal.LinuxOneWire;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


/**
 * The {@code LinuxFsOneWire} class provides an implementation for interacting with
 * the OneWire filesystem on Linux devices. This class includes methods for reading
 * and writing files in the OneWire device directory, validating file accessibility,
 * and managing configuration through the {@code OneWireBase} superclass.
 */
public class LinuxFsOneWire extends OneWireBase implements OneWire {

    /**
     * Reference to the LinuxOneWire instance managing device-specific functionality.
     */
    protected final LinuxOneWire oneWire;

    /**
     * Logger instance for logging events and debugging information.
     */
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Constructs a new {@code LinuxFsOneWire} instance with the specified OneWire device,
     * provider, and configuration.
     *
     * @param oneWire  the {@link LinuxOneWire} instance representing the OneWire device.
     * @param provider the {@link OneWireProvider} associated with this instance.
     * @param config   the {@link OneWireConfig} containing configuration details.
     */
    public LinuxFsOneWire(LinuxOneWire oneWire, OneWireProvider provider, OneWireConfig config) {
        super(provider, config);
        this.oneWire = oneWire;
    }

    /**
     * Reads the entire content of a file as a list of lines.
     *
     * @param fileName the name of the file to read.
     * @return a list of strings, where each string represents a line in the file.
     * @throws IOException if the file does not exist, is not readable, or another I/O error occurs.
     */
    public List<String> readFile(String fileName) throws IOException {
        Path filePath = Paths.get(oneWire.getDevicePath(), fileName);
        validateFileForRead(filePath);

        try {
            return Files.readAllLines(filePath);
        } catch (java.io.IOException e) {
            throw new IOException(e);
        }
    }

    /**
     * Reads the first line from a file.
     *
     * @param fileName the name of the file to read.
     * @return the first line of the file as a string, or {@code null} if the file is empty.
     * @throws IOException if the file does not exist, is not readable, or another I/O error occurs.
     */
    public String readFileLine(String fileName) throws IOException {
        Path filePath = Paths.get(oneWire.getDevicePath(), fileName);
        validateFileForRead(filePath);

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            return reader.readLine();
        } catch (java.io.IOException e) {
            throw new IOException(e);
        }
    }

    /**
     * Writes a single byte to a file.
     *
     * @param fileName the name of the file to write to.
     * @param value    the byte value to write.
     * @throws IOException if the file does not exist, is not writable, or another I/O error occurs.
     */
    public void writeFile(String fileName, byte value) throws IOException {
        Path filePath = Paths.get(oneWire.getDevicePath(), fileName);
        validateFileForWrite(filePath);

        try {
            Files.write(filePath, new byte[]{value});
        } catch (java.io.IOException e) {
            throw new IOException(e);
        }
    }

    /**
     * Writes a byte array to a file.
     *
     * @param fileName the name of the file to write to.
     * @param values   the byte array to write.
     * @throws IOException if the file does not exist, is not writable, or another I/O error occurs.
     */
    public void writeFile(String fileName, byte[] values) throws IOException {
        Path filePath = Paths.get(oneWire.getDevicePath(), fileName);
        validateFileForWrite(filePath);

        try {
            Files.write(filePath, values);
        } catch (java.io.IOException e) {
            throw new IOException(e);
        }
    }

    /**
     * Writes a string to a file.
     *
     * @param fileName the name of the file to write to.
     * @param content  the string content to write.
     * @throws IOException if the file does not exist, is not writable, or another I/O error occurs.
     */
    public void writeFile(String fileName, String content) throws IOException {
        Path filePath = Paths.get(oneWire.getDevicePath(), fileName);
        validateFileForWrite(filePath);

        try {
            Files.writeString(filePath, content);
        } catch (java.io.IOException e) {
            throw new IOException(e);
        }
    }

    /**
     * Validates that a file exists and is readable.
     *
     * @param filePath the path of the file to validate.
     * @throws IOException if the file does not exist or is not readable.
     */
    private void validateFileForRead(Path filePath) throws IOException {
        if (!Files.exists(filePath)) {
            throw new IOException("File " + filePath + " does not exist.");
        }
        if (!Files.isReadable(filePath)) {
            throw new IOException("File " + filePath + " is not readable.");
        }
    }

    /**
     * Validates that a file exists and is writable.
     *
     * @param filePath the path of the file to validate.
     * @throws IOException if the file does not exist or is not writable.
     */
    private void validateFileForWrite(Path filePath) throws IOException {
        if (!Files.exists(filePath)) {
            throw new IOException("File " + filePath + " does not exist.");
        }
        if (!Files.isWritable(filePath)) {
            throw new IOException("File " + filePath + " is not writable.");
        }
    }
}


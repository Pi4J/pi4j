package com.pi4j.plugin.mock.provider.onewire;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: PLUGIN   :: Mock Platform & Providers
 * FILENAME      :  MockOneWire.java
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Mock implementation of the OneWire interface for testing and simulation purposes.
 * This class provides methods to read and write to mock 1-Wire files.
 */
public class MockOneWire extends OneWireBase implements OneWire {

    private static final Logger logger = LoggerFactory.getLogger(MockOneWire.class);

    // Stores the content of "files" as a map, where each file's content is a Byte array.
    protected final Map<String, Byte[]> content = new HashMap<>();

    /**
     * Constructs a new {@code MockOneWire} instance.
     *
     * @param provider the {@link OneWireProvider} used to manage 1-Wire communication.
     * @param config   the {@link OneWireConfig} specifying configuration settings for this instance.
     */
    public MockOneWire(OneWireProvider provider, OneWireConfig config) {
        super(provider, config);
        logger.debug("[{}::{}] :: CREATE(DEVICE={})",
            provider.name(), this.id(), config.device());
    }

    /**
     * Reads the entire content of a mock file and splits it into lines using the system's line separator.
     *
     * @param fileName the name of the file to read.
     * @return a collection of strings, each representing a line from the file.
     * @throws IOException if the file does not exist or cannot be read.
     */
    @Override
    public List<String> readFile(String fileName) throws IOException {
        byte[] input = getContentFile(fileName);
        String contentString = new String(input, UTF_8);
        logger.debug("[{}::{}] :: READ FILE ({}): {}",
            provider.name(), this.id(), fileName, contentString);
        return Arrays.asList(contentString.split(System.lineSeparator()));
    }

    /**
     * Reads the first line of a mock file.
     *
     * @param fileName the name of the file to read.
     * @return the first line of the file, or an empty string if the file is empty or does not exist.
     * @throws IOException if the file does not exist or cannot be read.
     */
    @Override
    public String readFirstLine(String fileName) throws IOException {
        byte[] input = getContentFile(fileName);
        String contentString = new String(input, UTF_8);
        String[] lines = contentString.split(System.lineSeparator());
        String result = lines.length > 0 ? lines[0] : "";
        logger.debug("[{}::{}] :: READ FILE LINE ({}): {}",
            provider.name(), this.id(), fileName, result);
        return result;
    }

    /**
     * Reads the entire content of a mock file as a byte array.
     *
     * @param fileName the name of the file to read.
     * @return a byte array containing the file's content, or an empty array if the file does not exist.
     * @throws IOException if the file cannot be read.
     */
    @Override
    public byte[] readFileAsBytes(String fileName) throws IOException {
        byte[] input = getContentFile(fileName);
        logger.debug("[{}::{}] :: READ FILE AS BYTES ({}): 0x{}",
            provider.name(), this.id(), fileName, Arrays.toString(input));
        return input;
    }

    /**
     * Writes a single byte to a mock file, overwriting any existing content.
     *
     * @param fileName the name of the file to write.
     * @param value    the byte to write to the file.
     * @throws IOException if the file cannot be written.
     */
    @Override
    public void writeFile(String fileName, byte value) throws IOException {
        content.put(fileName, new Byte[]{value});
        logger.debug("[{}::{}] :: WRITE FILE ({}): 0x{}",
            provider.name(), this.id(), fileName, String.format("%02X", value));
    }

    /**
     * Writes a byte array to a mock file, overwriting any existing content.
     *
     * @param fileName the name of the file to write.
     * @param values   the byte array to write to the file.
     * @throws IOException if the file cannot be written.
     */
    @Override
    public void writeFile(String fileName, byte[] values) throws IOException {
        content.put(fileName, toBoxed(values));
        logger.debug("[{}::{}] :: WRITE FILE ({}): 0x{}",
            provider.name(), this.id(), fileName, Arrays.toString(values));
    }

    /**
     * Writes a string to a mock file as UTF-8 encoded bytes, overwriting any existing content.
     *
     * @param fileName the name of the file to write.
     * @param content  the string content to write to the file.
     * @throws IOException if the file cannot be written.
     */
    @Override
    public void writeFile(String fileName, String content) throws IOException {
        byte[] byteArray = content.getBytes(UTF_8);
        this.content.put(fileName, toBoxed(byteArray));
        logger.debug("[{}::{}] :: WRITE FILE ({}): {}",
            provider.name(), this.id(), fileName, content);
    }

    /**
     * Retrieves the content of a mock file as a primitive byte array.
     *
     * @param fileName the name of the file to retrieve.
     * @return the content of the file as a {@code byte[]} array, or an empty array if the file does not exist.
     */
    private byte[] getContentFile(String fileName) {
        return toPrimitive(content.getOrDefault(fileName, new Byte[0]));
    }

    /**
     * Converts a {@code Byte[]} array to a {@code byte[]} array.
     *
     * @param input the {@code Byte[]} array to convert.
     * @return the converted {@code byte[]} array.
     */
    private static byte[] toPrimitive(Byte[] input) {
        byte[] result = new byte[input.length];
        IntStream
            .range(0, input.length)
            .forEach(i -> result[i] = input[i]);
        return result;
    }

    /**
     * Converts a {@code byte[]} array to a {@code Byte[]} array.
     *
     * @param input the {@code byte[]} array to convert.
     * @return the converted {@code Byte[]} array.
     */
    private static Byte[] toBoxed(byte[] input) {
        Byte[] result = new Byte[input.length];
        IntStream
            .range(0, input.length)
            .forEach(i -> result[i] = input[i]);
        return result;
    }
}

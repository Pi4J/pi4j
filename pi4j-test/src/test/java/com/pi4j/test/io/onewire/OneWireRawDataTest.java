package com.pi4j.test.io.onewire;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: TESTING  :: Unit/Integration Tests
 * FILENAME      :  OneWireRawDataTest.java
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

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.exception.Pi4JException;
import com.pi4j.io.onewire.OneWire;
import com.pi4j.plugin.mock.provider.onewire.MockOneWire;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(Lifecycle.PER_CLASS)
public class OneWireRawDataTest {

    private Context pi4j;

    @BeforeEach
    public void beforeTest() throws Pi4JException {
        pi4j = Pi4J.newContextBuilder()
            .autoDetectMockPlugins()
            .autoDetectPlatforms()
            .build();
    }

    @AfterEach
    public void afterTest() {
        try {
            pi4j.shutdown();
        } catch (Pi4JException ignored) { /* do nothing */ }
    }

    @Test
    public void shouldWriteAndReadStringData() {
        var content = "24000" + System.lineSeparator() + "48000";
        var oneWire = createMockOneWire("28-000008d6bac6");

        oneWire.writeFile("content", content);

        var fileContent = oneWire.readFile("content");

        assertNotNull(fileContent);
        assertEquals(2, fileContent.size());
        assertEquals("24000", fileContent.get(0));
        assertEquals("48000", fileContent.get(1));
        assertEquals(content, String.join(System.lineSeparator(), fileContent));
    }

    @Test
    public void shouldWriteAndReadByteData() {
        var content = new byte[]{0x01, 0x20, 0x30};
        var oneWire = createMockOneWire("28-000008d6bac6");

        oneWire.writeFile("content", content);

        var fileContent = oneWire.readFileAsBytes("content");

        assertNotNull(fileContent);
        assertEquals(3, fileContent.length);
        assertArrayEquals(content, fileContent);
    }

    private MockOneWire createMockOneWire(String deviceId) {
        var config = OneWire.newConfigBuilder(pi4j)
            .id("my-one-wire")
            .name("My 1-Wire")
            .device(deviceId)
            .build();
        return (MockOneWire) pi4j.oneWire().create(config);
    }
}

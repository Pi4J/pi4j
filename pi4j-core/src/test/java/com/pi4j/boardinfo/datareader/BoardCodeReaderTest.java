package com.pi4j.boardinfo.datareader;

import com.pi4j.boardinfo.util.command.CommandResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class BoardCodeReaderTest {

    private File tempModelFile;

    @BeforeEach
    void setUp() throws IOException {
        // Create a temporary file for testing
        tempModelFile = File.createTempFile("model", ".tmp");
        BoardCodeReader.setModelFilePath(tempModelFile.getAbsolutePath());
    }

    @AfterEach
    void tearDown() {
        // Delete the temporary file after each test
        if (tempModelFile.exists()) {
            tempModelFile.delete();
        }
    }

    @Test
    void testGetBoardCode_Success() throws IOException {
        // Write valid content to the temporary model file
        try (FileWriter writer = new FileWriter(tempModelFile)) {
            writer.write("Raspberry Pi 4 Model B Rev 1.1\n");
        }

        // Execute the method
        CommandResult result = BoardCodeReader.getBoardCode();

        // Verify the result
        assertTrue(result.isSuccess());
        assertEquals("Raspberry Pi 4 Model B Rev 1.1", result.getOutputMessage());
        assertEquals("", result.getErrorMessage());
    }

    @Test
    void testGetBoardCode_EmptyFile() throws IOException {
        // Create an empty model file
        try (FileWriter writer = new FileWriter(tempModelFile)) {
            // No content written
        }

        // Execute the method
        CommandResult result = BoardCodeReader.getBoardCode();

        // Verify the result
        assertTrue(result.isSuccess());
        assertEquals("", result.getOutputMessage());
        assertEquals("", result.getErrorMessage());
    }

    @Test
    void testGetBoardCode_FileNotReadable() throws IOException {
        // Write valid content and then make the file unreadable
        try (FileWriter writer = new FileWriter(tempModelFile)) {
            writer.write("Raspberry Pi 4 Model B Rev 1.1\n");
        }
        tempModelFile.setReadable(false);

        // Execute the method
        CommandResult result = BoardCodeReader.getBoardCode();

        // Verify the result
        assertFalse(result.isSuccess());
        assertEquals("", result.getOutputMessage());
        assertTrue(result.getErrorMessage().contains("IOException"));

        // Restore file permissions for cleanup
        tempModelFile.setReadable(true);
    }

    @Test
    void testGetBoardCode_InvalidContent() throws IOException {
        // Write invalid content (whitespace only) to the temporary file
        try (FileWriter writer = new FileWriter(tempModelFile)) {
            writer.write("\n\n   "); // Just whitespace
        }

        // Execute the method
        CommandResult result = BoardCodeReader.getBoardCode();

        // Verify the result
        assertTrue(result.isSuccess());
        assertEquals("", result.getOutputMessage());
        assertEquals("", result.getErrorMessage());
    }
}

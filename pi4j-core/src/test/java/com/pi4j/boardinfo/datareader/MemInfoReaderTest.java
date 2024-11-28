package com.pi4j.boardinfo.datareader;

import com.pi4j.boardinfo.util.command.CommandResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class MemInfoReaderTest {

    private File tempMemInfoFile;

    @BeforeEach
    void setUp() throws IOException {
        // Create a unique temporary file for testing
        tempMemInfoFile = File.createTempFile("meminfo", ".tmp");
        MemInfoReader.setMemInfoFilePath(tempMemInfoFile.getAbsolutePath());
    }

    @AfterEach
    void tearDown() {
        // Delete the temporary file after each test
        if (tempMemInfoFile.exists()) {
            tempMemInfoFile.delete();
        }
    }

    @Test
    void testGetMemTotal_Success() throws IOException {
        // Write valid content to the temporary memory info file
        try (FileWriter writer = new FileWriter(tempMemInfoFile)) {
            writer.write("MemTotal:        8192 kB\n");
        }

        // Execute the method
        CommandResult result = MemInfoReader.getMemTotal();

        // Verify the result
        assertTrue(result.isSuccess());
        assertEquals("MemTotal:        8192 kB", result.getOutputMessage());
        assertEquals("", result.getErrorMessage());
    }

    @Test
    void testGetMemTotal_EmptyFile() throws IOException {
        // Create an empty memory info file
        try (FileWriter writer = new FileWriter(tempMemInfoFile)) {
            // Leave file empty
        }

        // Execute the method
        CommandResult result = MemInfoReader.getMemTotal();

        // Verify the result
        assertFalse(result.isSuccess());
        assertEquals("", result.getOutputMessage());
        assertEquals("MemTotal entry not found in memory information file.", result.getErrorMessage());
    }

    @Test
    void testGetMemTotal_FileNotReadable() {
        // Simulate a file that cannot be read by deleting it
        tempMemInfoFile.delete();

        // Execute the method
        CommandResult result = MemInfoReader.getMemTotal();

        // Verify the result
        assertFalse(result.isSuccess());
        assertEquals("", result.getOutputMessage());
        assertTrue(result.getErrorMessage().contains("IOException"));
    }

    @Test
    void testGetMemTotal_InvalidContent() throws IOException {
        // Write invalid content (whitespace only) to the temporary file
        try (FileWriter writer = new FileWriter(tempMemInfoFile)) {
            writer.write("\n\n   "); // Just whitespace
        }

        // Execute the method
        CommandResult result = MemInfoReader.getMemTotal();

        // Verify the result
        assertFalse(result.isSuccess());
        assertEquals("", result.getOutputMessage());
        assertEquals("MemTotal entry not found in memory information file.", result.getErrorMessage());
    }

    @Test
    void testGetMemTotal_CorruptedContent() throws IOException {
        // Write corrupted content to the temporary memory info file
        try (FileWriter writer = new FileWriter(tempMemInfoFile)) {
            writer.write("!@#$%^&*() Invalid Data\n");
        }

        // Execute the method
        CommandResult result = MemInfoReader.getMemTotal();

        // Verify the result
        assertFalse(result.isSuccess());
        assertEquals("", result.getOutputMessage());
        assertEquals("MemTotal entry not found in memory information file.", result.getErrorMessage());
    }

    @Test
    void testGetMemTotal_NoMemTotalEntry() throws IOException {
        // Write content without a "MemTotal" entry
        try (FileWriter writer = new FileWriter(tempMemInfoFile)) {
            writer.write("MemFree:        4096 kB\n");
            writer.write("Buffers:        1024 kB\n");
        }

        // Execute the method
        CommandResult result = MemInfoReader.getMemTotal();

        // Verify the result
        assertFalse(result.isSuccess());
        assertEquals("", result.getOutputMessage());
        assertEquals("MemTotal entry not found in memory information file.", result.getErrorMessage());
    }
}

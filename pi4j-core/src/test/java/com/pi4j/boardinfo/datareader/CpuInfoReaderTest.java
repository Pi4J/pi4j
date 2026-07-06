package com.pi4j.boardinfo.datareader;

import com.pi4j.boardinfo.util.command.CommandResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class CpuInfoReaderTest {

    private File tempCpuInfoFile;

    @BeforeEach
    void setUp() throws IOException {
        tempCpuInfoFile = File.createTempFile("cpuinfo", ".tmp");
        CpuInfoReader.setCpuInfoFilePath(tempCpuInfoFile.getAbsolutePath());
    }

    @AfterEach
    void tearDown() {
        if (tempCpuInfoFile.exists()) {
            tempCpuInfoFile.delete();
        }
    }

    @Test
    void testGetCpuRevision_Success() throws IOException {
        try (FileWriter writer = new FileWriter(tempCpuInfoFile)) {
            writer.write("Processor\t: ARMv7 Processor\n");
            writer.write("Revision\t: 000e\n");
        }

        CommandResult result = CpuInfoReader.getCpuRevision();

        assertTrue(result.isSuccess());
        assertEquals("000e", result.getOutputMessage());
        assertEquals("", result.getErrorMessage());
    }

    @Test
    void testGetCpuRevision_NoRevision() throws IOException {
        try (FileWriter writer = new FileWriter(tempCpuInfoFile)) {
            writer.write("Processor\t: ARMv7 Processor\n");
        }

        CommandResult result = CpuInfoReader.getCpuRevision();

        assertFalse(result.isSuccess());
        assertEquals("", result.getOutputMessage());
        assertEquals("CPU revision not found in file", result.getErrorMessage());
    }

    @Test
    void testGetCpuRevision_FileNotReadable() throws IOException {
        try (FileWriter writer = new FileWriter(tempCpuInfoFile)) {
            writer.write("Revision\t: 000e\n");
        }
        tempCpuInfoFile.setReadable(false);

        CommandResult result = CpuInfoReader.getCpuRevision();

        assertFalse(result.isSuccess());
        assertEquals("", result.getOutputMessage());
        assertTrue(result.getErrorMessage().contains("IOException"));

        tempCpuInfoFile.setReadable(true);
    }

    @Test
    void testGetCpuRevision_EmptyFile() throws IOException {
        try (FileWriter writer = new FileWriter(tempCpuInfoFile)) {
            // Empty file
        }

        CommandResult result = CpuInfoReader.getCpuRevision();

        assertFalse(result.isSuccess());
        assertEquals("", result.getOutputMessage());
        assertEquals("CPU revision not found in file", result.getErrorMessage());
    }

    @Test
    void testGetCpuRevision_CorruptedFile() throws IOException {
        try (FileWriter writer = new FileWriter(tempCpuInfoFile)) {
            writer.write("This is not valid CPU info\n");
        }

        CommandResult result = CpuInfoReader.getCpuRevision();

        assertFalse(result.isSuccess());
        assertEquals("", result.getOutputMessage());
        assertEquals("CPU revision not found in file", result.getErrorMessage());
    }
}

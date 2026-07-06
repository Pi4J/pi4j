package com.pi4j.boardinfo.util;

import com.pi4j.boardinfo.datareader.BoardCodeReader;
import com.pi4j.boardinfo.datareader.CpuInfoReader;
import com.pi4j.boardinfo.definition.PiModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BoardModelDetectionTest {

    private File tempCpuInfoFile;
    private File tempDeviceTreeModelFile;

    @BeforeEach
    void setUp() throws IOException {
        tempCpuInfoFile = File.createTempFile("cpuinfo", ".tmp");
        tempDeviceTreeModelFile = File.createTempFile("devicetree_model", ".tmp");

        CpuInfoReader.setCpuInfoFilePath(tempCpuInfoFile.getAbsolutePath());
        BoardCodeReader.setModelFilePath(tempDeviceTreeModelFile.getAbsolutePath());
    }

    @AfterEach
    void tearDown() {
        deleteFile(tempCpuInfoFile);
        deleteFile(tempDeviceTreeModelFile);

        // Reset paths to default
        CpuInfoReader.setCpuInfoFilePath("/proc/cpuinfo");
        BoardCodeReader.setModelFilePath("/proc/device-tree/model");
    }

    @Test
    void testGetOperatingSystem() {
        BoardInfoHelper.reinitialize();
        var detectedBoard = BoardInfoHelper.current();

        assertAll(
            () -> assertEquals(System.getProperty("os.name"), detectedBoard.getOperatingSystem().getName()),
            () -> assertEquals(System.getProperty("os.version"), detectedBoard.getOperatingSystem().getVersion()),
            () -> assertEquals(System.getProperty("os.arch"), detectedBoard.getOperatingSystem().getArchitecture())
        );
    }

    @Test
    void testGetJavaInfo() {
        BoardInfoHelper.reinitialize();
        var detectedBoard = BoardInfoHelper.current();

        assertAll(
            () -> assertEquals(System.getProperty("java.version"), detectedBoard.getJavaInfo().getVersion()),
            () -> assertEquals(System.getProperty("java.runtime.version"), detectedBoard.getJavaInfo().getRuntime()),
            () -> assertEquals(System.getProperty("java.vendor"), detectedBoard.getJavaInfo().getVendor()),
            () -> assertEquals(System.getProperty("java.vendor.version"), detectedBoard.getJavaInfo().getVendorVersion())
        );
    }

    @Test
    void testGetBoardModelUsingCpuinfo_Success() throws IOException {
        mockCpuInfoResponse("Processor: ARMv7 Processor\n" +
                            "Revision: a03111\n");

        BoardInfoHelper.reinitialize();
        var detectedBoard = BoardInfoHelper.current();

        assertEquals(PiModel.MODEL_B, detectedBoard.getBoardModel().getModel());
    }

    @Test
    void testGetBoardModelUsingDeviceTreeModel_Success() throws IOException {
        mockCpuInfoResponse("INVALID CONTENT");
        mockDeviceTreeModelResponse("Raspberry Pi 4 Model B Rev 1.1");

        BoardInfoHelper.reinitialize();
        var detectedBoard = BoardInfoHelper.current();

        assertEquals(PiModel.MODEL_B, detectedBoard.getBoardModel().getModel());
    }

    @Test
    void testConflictingData_DeviceTreeTakesPriority() throws IOException {
        mockCpuInfoResponse("Processor: ARMv7 Processor\n" +
                            "Revision: a020d3\n"); // Pi 3 Model B+
        mockDeviceTreeModelResponse("Raspberry Pi 4 Model B Rev 1.1");

        BoardInfoHelper.reinitialize();
        var detectedBoard = BoardInfoHelper.current();

        // Device tree should take priority
        assertEquals(PiModel.MODEL_B, detectedBoard.getBoardModel().getModel());
    }

    @Test
    void testBothSourcesInvalid() throws IOException {
        mockCpuInfoResponse("INVALID DATA");
        mockDeviceTreeModelResponse("UNKNOWN DEVICE");

        BoardInfoHelper.reinitialize();
        var detectedBoard = BoardInfoHelper.current();

        assertEquals(PiModel.UNKNOWN, detectedBoard.getBoardModel().getModel());
    }

    @Test
    void testPartialCpuinfoData() throws IOException {
        mockCpuInfoResponse("Processor: ARMv7 Processor\n");

        BoardInfoHelper.reinitialize();
        var detectedBoard = BoardInfoHelper.current();

        assertEquals(PiModel.UNKNOWN, detectedBoard.getBoardModel().getModel());
    }

    @Test
    void testPartialDeviceTreeData() throws IOException {
        mockDeviceTreeModelResponse("Raspberry");

        BoardInfoHelper.reinitialize();
        var detectedBoard = BoardInfoHelper.current();

        assertEquals(PiModel.UNKNOWN, detectedBoard.getBoardModel().getModel());
    }

    private void mockCpuInfoResponse(String content) throws IOException {
        try (FileWriter writer = new FileWriter(tempCpuInfoFile)) {
            writer.write(content);
        }
    }

    private void mockDeviceTreeModelResponse(String content) throws IOException {
        try (FileWriter writer = new FileWriter(tempDeviceTreeModelFile)) {
            writer.write(content);
        }
    }

    private void deleteFile(File file) {
        if (file != null && file.exists() && !file.delete()) {
            System.err.println("Warning: Failed to delete temporary file: " + file.getAbsolutePath());
        }
    }
}

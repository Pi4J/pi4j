package com.pi4j.plugin.ffm;

import com.pi4j.Pi4J;
import com.pi4j.io.serial.Baud;
import com.pi4j.io.serial.SerialConfigBuilder;
import com.pi4j.plugin.ffm.providers.serial.SerialFFMProviderImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class SerialTest {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private static Process socatProcess;

    @BeforeAll
    public static void setup() throws IOException, InterruptedException {
        socatProcess = new ProcessBuilder("/bin/bash", "-c", "socat -d -d pty,raw,b115200,echo=0 pty,raw,b115200,echo=0").start();
        if (!socatProcess.isAlive()) {
            var errorOutput = new String(socatProcess.getErrorStream().readAllBytes());
            fail(errorOutput);
        }
    }

    @AfterAll
    public static void shutdown() throws InterruptedException {
        socatProcess.destroy();
        socatProcess.waitFor();
    }

    @Test
    public void testSerial() {
        var pi4j = Pi4J.newContextBuilder()
            .add(new SerialFFMProviderImpl())
            .build();

        var serial1 = pi4j.serial().create(SerialConfigBuilder.newInstance(pi4j).device("/dev/pts/1").baud(Baud._230400));
        var serial2 = pi4j.serial().create(SerialConfigBuilder.newInstance(pi4j).device("/dev/pts/2").baud(Baud._230400));
        var writeData = "Test";
        executor.submit(() -> {
            serial1.write(writeData);
        });
        while (true) {
            var available = serial2.available();
            if (available > 0) {
                var buffer = new byte[available];
                var read = serial2.read(buffer);
                var readData = new String(buffer);
                assertEquals(available, read);
                assertEquals(writeData, readData);
                break;
            }
        }
        pi4j.shutdown();
    }
}

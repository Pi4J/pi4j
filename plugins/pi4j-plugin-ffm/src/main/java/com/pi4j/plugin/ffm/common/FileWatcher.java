package com.pi4j.plugin.ffm.common;

import com.pi4j.exception.Pi4JException;

import java.io.IOException;
import java.nio.file.*;
import java.time.Duration;
import java.time.Instant;

/**
 * File watcher wrapper to wait for file is ready with defined timeout.
 */
public class FileWatcher implements AutoCloseable {

    private final WatchService watcher;
    private final Path directory;
    private final String fileToWatch;
    private final int timoutMs;

    public FileWatcher(Path directory, String fileToWatch, int timoutMs) {
        try {
            this.watcher = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            throw new Pi4JException(e);
        }
        this.directory = directory;
        this.fileToWatch = fileToWatch;
        this.timoutMs = timoutMs;
        try {
            this.directory.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);
        } catch (IOException e) {
            throw new Pi4JException(e);
        }
    }

    /**
     * Checks, if file is created in the desired directory with timeout.
     * @return true if file is created and false if timeout occurred
     */
    public boolean waitForCreation() {
        // sanity check if file is already present
        if (Path.of(directory.toString(), fileToWatch).toFile().exists()) {
            return true;
        }
        var now = Instant.now();
        var timeout = Duration.ofMillis(timoutMs);
        while (Duration.between(now, Instant.now()).compareTo(timeout) < 0) {
            var key = watcher.poll();
            if (key == null) {
                continue;
            }
            for (WatchEvent<?> event : key.pollEvents()) {
                if (event.context() instanceof Path path) {
                    if (path.toString().equals(fileToWatch)) {
                        return true;
                    }
                }
            }
            key.reset();
        }
        return false;
    }

    @Override
    public void close() throws IOException {
        this.watcher.close();
    }
}

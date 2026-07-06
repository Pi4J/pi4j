package com.pi4j.plugin.ffm.common;

import com.pi4j.exception.Pi4JException;

import java.io.IOException;
import java.nio.file.*;
import java.time.Duration;
import java.time.Instant;

/**
 * Wraps a {@link WatchService} to block until a specific file appears in a directory, up to a
 * configured timeout. Used by the FFM backend to wait for kernel-created device or sysfs nodes
 * (for example a newly exported PWM channel) to become available before they are opened.
 */
public class FileWatcher implements AutoCloseable {

    private final WatchService watcher;
    private final Path directory;
    private final String fileToWatch;
    private final int timeoutMs;

    /**
     * Creates a watcher that monitors {@code directory} for the creation of {@code fileToWatch} and
     * registers an {@link StandardWatchEventKinds#ENTRY_CREATE} watch on it.
     *
     * @param directory   the directory to monitor for the new file
     * @param fileToWatch the file name (relative to {@code directory}) whose creation is awaited
     * @param timeoutMs   the maximum time, in milliseconds, to wait in {@link #waitForCreation()}
     * @throws Pi4JException if the watch service cannot be created or the directory cannot be registered
     */
    public FileWatcher(Path directory, String fileToWatch, int timeoutMs) {
        try {
            this.watcher = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            throw new Pi4JException(e);
        }
        this.directory = directory;
        this.fileToWatch = fileToWatch;
        this.timeoutMs = timeoutMs;
        try {
            this.directory.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);
        } catch (IOException e) {
            throw new Pi4JException(e);
        }
    }

    /**
     * Checks, if file is created in the desired directory with timeout.
     *
     * @return true if file is created and false if timeout occurred
     */
    public boolean waitForCreation() {
        // sanity check if file is already present
        if (Path.of(directory.toString(), fileToWatch).toFile().exists()) {
            return true;
        }
        var now = Instant.now();
        var timeout = Duration.ofMillis(timeoutMs);
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

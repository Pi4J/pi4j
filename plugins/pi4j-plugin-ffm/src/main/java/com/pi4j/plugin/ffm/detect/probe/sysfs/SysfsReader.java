package com.pi4j.plugin.ffm.detect.probe.sysfs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

/**
 * Small read-only helpers for the sysfs hierarchy ({@code /sys/bus}, {@code /sys/class}, ...).
 * Used by the sysfs bus scanner so the file-reading boilerplate is not repeated.
 */
public final class SysfsReader {

    private static final Logger logger = LoggerFactory.getLogger(SysfsReader.class);

    private SysfsReader() {
    }

    /**
     * @param path a sysfs attribute file
     * @return the stripped first line, or {@code null} if the file is absent/unreadable
     */
    public static String firstLine(Path path) {
        if (!Files.exists(path)) {
            return null;
        }
        try {
            return Files.readString(path).strip();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * @param path a sysfs attribute file holding a {@code 0x}-prefixed hex value (e.g. a PCI {@code class})
     * @return the decoded value, or {@code -1} if absent/unparseable
     */
    public static int readHex(Path path) {
        var text = firstLine(path);
        if (text == null) {
            return -1;
        }
        try {
            return Integer.decode(text);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Lists the children of a sysfs directory whose names start with the given prefix, sorted.
     *
     * @param dir    the directory to list
     * @param prefix required child-name prefix
     * @return matching child paths (empty if {@code dir} is not a directory or cannot be listed)
     */
    public static List<Path> listChildren(Path dir, String prefix) {
        if (!Files.isDirectory(dir)) {
            return List.of();
        }
        try (Stream<Path> children = Files.list(dir)) {
            return children.filter(p -> p.getFileName().toString().startsWith(prefix)).sorted().toList();
        } catch (IOException e) {
            logger.warn("Could not list {}: {}", dir, e.getMessage());
            return List.of();
        }
    }
}

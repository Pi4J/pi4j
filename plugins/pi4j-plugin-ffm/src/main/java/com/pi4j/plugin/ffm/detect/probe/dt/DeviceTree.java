package com.pi4j.plugin.ffm.detect.probe.dt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Read-only access to the flattened device tree, mounted as a filesystem at {@code /proc/device-tree}
 * (an alias of {@code /sys/firmware/devicetree/base}): one directory per node, one file per property.
 * <p>
 * Shared by the board-profile detection and the device-tree scanner so the NUL-separated string parsing
 * lives in exactly one place. Reading it is plain file I/O — no {@code gpiod}, no shell, no FFM.
 */
public final class DeviceTree {

    private static final Logger logger = LoggerFactory.getLogger(DeviceTree.class);

    private static final Path[] ROOTS = {
        Path.of("/proc/device-tree"),
        Path.of("/sys/firmware/devicetree/base")
    };

    private DeviceTree() {
    }

    /**
     * @return {@code true} if this platform exposes a device tree at all
     */
    public static boolean isAvailable() {
        return root() != null;
    }

    /**
     * @return the device-tree root directory, or {@code null} when none is mounted (e.g. x86)
     */
    public static Path root() {
        for (var root : ROOTS) {
            if (Files.isDirectory(root)) {
                return root;
            }
        }
        return null;
    }

    /**
     * @return the board name from {@code <root>/model}, or an empty string if unavailable
     */
    public static String readModel() {
        var strings = readStrings(propertyPath("model"));
        return strings.isEmpty() ? "" : strings.getFirst();
    }

    /**
     * @return the device-tree {@code compatible} list (board entry first), or an empty list
     */
    public static List<String> readCompatible() {
        return readStrings(propertyPath("compatible"));
    }

    /**
     * Reads the first string of a property (e.g. a node's {@code status}).
     *
     * @param path the property file
     * @return the first NUL-delimited string, or {@code null} if the property is absent/empty
     */
    public static String readString(Path path) {
        var strings = readStrings(path);
        return strings.isEmpty() ? null : strings.getFirst();
    }

    /**
     * Reads a device-tree string or string-list property: NUL-separated, NUL-terminated values.
     *
     * @param path the property file
     * @return the parsed strings (empty if the file is missing or unreadable)
     */
    public static List<String> readStrings(Path path) {
        if (path == null || !Files.exists(path)) {
            return List.of();
        }
        try {
            var raw = Files.readAllBytes(path);
            var list = new ArrayList<String>();
            int start = 0;
            for (int i = 0; i < raw.length; i++) {
                if (raw[i] == 0) {
                    if (i > start) {
                        list.add(new String(raw, start, i - start).strip());
                    }
                    start = i + 1;
                }
            }
            if (start < raw.length) {
                list.add(new String(raw, start, raw.length - start).strip());
            }
            return list;
        } catch (IOException e) {
            logger.debug("Could not read device-tree property {}: {}", path, e.getMessage());
            return List.of();
        }
    }

    private static Path propertyPath(String property) {
        var root = root();
        return root == null ? null : root.resolve(property);
    }
}

package com.pi4j.plugin.ffm.common;

import com.pi4j.exception.Pi4JException;

/**
 * Class-helper to detect the right processor architecture.
 * This is needed to determine the right path for library to load.
 */
public class Pi4JArchitectureGuess {

    /**
     * Gets the full absolute system library path from library name.
     *
     * @param libraryName library name, e.g. 'libi2c'
     * @return absolute library path
     */
    public static String getLibraryPath(String libraryName) {
        var os = System.getProperty("os.name");
        if (os.startsWith("Windows") || os.startsWith("Mac")) {
            throw new Pi4JException("Pi4j does not support Windows/MacOS hosts");
        }
        var arch = System.getProperty("os.arch");
        return switch (arch) {
            case "amd64" -> "/usr/lib/x86_64-linux-gnu/" + libraryName + ".so";
            case "aarch64" -> "/usr/lib/aarch64-linux-gnu/" + libraryName + ".so";
            default -> throw new Pi4JException("Unsupported architecture: " + arch);
        };
    }
}

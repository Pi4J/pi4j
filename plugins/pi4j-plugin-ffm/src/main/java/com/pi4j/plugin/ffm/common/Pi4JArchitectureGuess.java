package com.pi4j.plugin.ffm.common;

import com.pi4j.exception.Pi4JException;

/**
 * Resolves the absolute path of a native shared library based on the host CPU architecture, so the
 * FFM backend can load glibc-style libraries (such as {@code libi2c}) via the Foreign Function and
 * Memory API. Only 64-bit Linux on {@code amd64} and {@code aarch64} is supported.
 */
public class Pi4JArchitectureGuess {

    /**
     * Resolves the absolute path of the given shared library for the current Linux architecture,
     * using the standard multiarch library directories ({@code /usr/lib/x86_64-linux-gnu} or
     * {@code /usr/lib/aarch64-linux-gnu}) and appending the {@code .so} suffix.
     *
     * @param libraryName the base library name without path or extension, e.g. {@code libi2c}
     * @return the absolute {@code .so} path for the detected architecture
     * @throws Pi4JException if the host OS is Windows or macOS, or the CPU architecture is neither
     *                       {@code amd64} nor {@code aarch64}
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

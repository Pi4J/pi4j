package com.pi4j.plugin.ffm.common;

import com.pi4j.exception.Pi4JException;

public class Pi4JArchitectureGuess {

    public static String getLibraryPath(String libraryName) {
        var arch = System.getProperty("os.arch");
        return switch (arch) {
            case "amd64" -> "/usr/lib/x86_64-linux-gnu/" + libraryName + ".so";
            case "aarch64" -> "/usr/lib/" + libraryName + ".so";
            default -> throw new Pi4JException("Unsupported architecture: " + arch);
        };
    }
}

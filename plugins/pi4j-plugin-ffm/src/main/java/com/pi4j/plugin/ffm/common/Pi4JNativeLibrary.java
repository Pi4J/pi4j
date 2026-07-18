package com.pi4j.plugin.ffm.common;

import com.pi4j.exception.Pi4JException;

import java.lang.foreign.Arena;
import java.lang.foreign.SymbolLookup;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads the native shared libraries used by the FFM backend (such as {@code libi2c}) by delegating
 * resolution to the operating system's dynamic linker, instead of hardcoding architecture- or
 * distribution-specific paths.
 * <p>
 * {@link SymbolLookup#libraryLookup(String, Arena)} calls into the platform loader ({@code dlopen}
 * on Linux), which already searches {@code ld.so.cache}, {@code LD_LIBRARY_PATH} and the standard
 * (multiarch) library directories. Passing a bare library file name therefore resolves correctly on
 * every CPU architecture ({@code amd64}, {@code aarch64}, {@code riscv64}, ...) and every distribution
 * layout, with no per-architecture switch to maintain.
 */
public final class Pi4JNativeLibrary {

    private Pi4JNativeLibrary() {
    }

    /**
     * Loads a native library by base name using the system dynamic linker and returns a
     * {@link SymbolLookup} for its symbols. The {@linkplain #libraryCandidates(String) candidate file
     * names} are tried in order until one loads: the unversioned developer symlink first (from the
     * {@code -dev} package), then the common runtime sonames so loading still succeeds when only the
     * runtime package is installed.
     *
     * @param baseName the library base name, with or without the {@code lib} prefix (e.g. {@code libi2c}
     *                 or {@code i2c})
     * @param arena    the arena that scopes the loaded library's lifetime
     * @return a symbol lookup over the resolved library
     * @throws Pi4JException if the host OS is not Linux, or none of the candidate names could be loaded
     */
    public static SymbolLookup load(String baseName, Arena arena) {
        assertLinuxHost();
        var candidates = libraryCandidates(baseName);
        var failures = new ArrayList<String>();
        for (var name : candidates) {
            try {
                return SymbolLookup.libraryLookup(name, arena);
            } catch (IllegalArgumentException e) {
                // libraryLookup throws IllegalArgumentException when the loader cannot find/load the
                // name; record it and fall through to the next candidate.
                failures.add(name + " (" + e.getMessage() + ")");
            }
        }
        throw new Pi4JException("Could not load native library '" + baseName + "' via the system dynamic linker. "
            + "Tried, in order: " + candidates + ". Ensure the library is installed and reachable through the "
            + "loader (ld.so.cache / LD_LIBRARY_PATH); the unversioned '.so' additionally requires the matching "
            + "-dev package. Loader errors: " + failures);
    }

    /**
     * Returns the candidate file names for {@code baseName}, in the order they should be tried. The
     * platform-specific file name is derived via {@link System#mapLibraryName(String)} (so {@code i2c}
     * becomes {@code libi2c.so} on Linux), followed by the {@code .0} and {@code .1} versioned sonames
     * that the runtime package typically ships without the {@code -dev} symlink.
     *
     * @param baseName the library base name, with or without a leading {@code lib}
     * @return the ordered, immutable list of candidate library file names
     */
    public static List<String> libraryCandidates(String baseName) {
        var stem = baseName.startsWith("lib") ? baseName.substring(3) : baseName;
        var soName = System.mapLibraryName(stem);
        return List.of(soName, soName + ".0", soName + ".1");
    }

    /**
     * Indicates whether {@code osName} (as reported by the {@code os.name} system property) is a host
     * the FFM plugin supports. Only Linux is supported; Windows and macOS are rejected.
     *
     * @param osName the operating-system name, or {@code null}
     * @return {@code true} if the FFM plugin can run on the given OS
     */
    public static boolean isSupportedHost(String osName) {
        return osName != null && !osName.startsWith("Windows") && !osName.startsWith("Mac");
    }

    private static void assertLinuxHost() {
        var os = System.getProperty("os.name");
        if (!isSupportedHost(os)) {
            throw new Pi4JException("Pi4J FFM plugin supports Linux hosts only (detected OS: '" + os + "')");
        }
    }
}

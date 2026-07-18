package com.pi4j.plugin.ffm.unit;

import com.pi4j.exception.Pi4JException;
import com.pi4j.plugin.ffm.common.Pi4JNativeLibrary;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.foreign.Arena;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Unit tests for {@link Pi4JNativeLibrary}, covering the OS support guard and the system-driven
 * library-name resolution that replaced the previous hardcoded per-architecture paths. These tests
 * do not depend on the CPU architecture: candidate resolution is asserted structurally, and the only
 * live library load targets the C library, which is present on every Linux host.
 */
class Pi4JNativeLibraryTest {

    private static boolean isLinux() {
        var os = System.getProperty("os.name");
        return os != null && os.startsWith("Linux");
    }

    @ParameterizedTest
    @ValueSource(strings = {"Windows 10", "Windows 11", "Mac OS X"})
    void rejectsWindowsAndMacHosts(String osName) {
        assertFalse(Pi4JNativeLibrary.isSupportedHost(osName));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Linux", "Linux 6.17", "FreeBSD"})
    void acceptsNonWindowsNonMacHosts(String osName) {
        assertTrue(Pi4JNativeLibrary.isSupportedHost(osName));
    }

    @Test
    void nullHostIsUnsupported() {
        assertFalse(Pi4JNativeLibrary.isSupportedHost(null));
    }

    @Test
    void candidateListHasVersionedFallbacksInOrder() {
        // The plain mapped name (dev symlink) must come first, then the .0 and .1 runtime sonames so
        // loading still succeeds when only the runtime package - not the -dev package - is installed.
        List<String> candidates = Pi4JNativeLibrary.libraryCandidates("i2c");
        assertEquals(3, candidates.size());
        String base = candidates.get(0);
        assertEquals(base + ".0", candidates.get(1));
        assertEquals(base + ".1", candidates.get(2));
        assertTrue(base.contains("i2c"), "mapped name should contain the library stem: " + base);
    }

    @ParameterizedTest
    @CsvSource({"i2c,i2c", "libi2c,i2c", "c,c", "libc,c"})
    void stripsOptionalLibPrefixBeforeMapping(String input, String stem) {
        // A leading "lib" must be stripped before System.mapLibraryName so it is not doubled up
        // (e.g. "libi2c" must not become "liblibi2c.so").
        assertEquals(System.mapLibraryName(stem), Pi4JNativeLibrary.libraryCandidates(input).get(0));
    }

    @Test
    void mapsToDotSoNamesOnLinux() {
        assumeTrue(isLinux(), "library file names are .so only on Linux");
        assertEquals(List.of("libi2c.so", "libi2c.so.0", "libi2c.so.1"),
            Pi4JNativeLibrary.libraryCandidates("libi2c"));
    }

    @Test
    void throwsPi4JExceptionForUnknownLibrary() {
        try (Arena arena = Arena.ofConfined()) {
            Pi4JException ex = assertThrows(Pi4JException.class,
                () -> Pi4JNativeLibrary.load("pi4j-definitely-not-a-real-library", arena));
            assertTrue(ex.getMessage().contains("pi4j-definitely-not-a-real-library"));
        }
    }
}

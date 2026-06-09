package com.pi4j.plugin.ffm.detect.model;

import com.pi4j.plugin.ffm.detect.probe.dt.DeviceTree;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Supplies platform-appropriate "how to enable this controller" hints.
 * <p>
 * Detecting a controller is identical across single-board computers, but the way you <em>turn an interface
 * on</em> is vendor- and distro-specific. All of that knowledge lives in one data-driven {@link #REGISTRY}:
 * each {@link BoardDefinition} pairs the signals that identify a board (distro boot-config files, device-tree
 * {@code compatible} vendor prefixes, {@code model} substrings — read through {@link DeviceTree}) with the
 * per-subsystem hint strings for that board. {@link #detect()} returns the first matching entry.
 * <p>
 * The enable mechanism follows the running <em>image</em> more than the silicon, so distro signatures
 * (Armbian, Raspberry Pi OS, JetPack, ...) are ordered ahead of the bare silicon vendors (Radxa, Pine64, ...).
 */
public final class BoardOverview {

    private static final String GPIO_HINT =
        "built into the SoC; load the pinctrl/GPIO kernel module if no chip appears";
    private static final String GENERIC_FALLBACK =
        "enable the corresponding device-tree overlay for your board and reboot";
    private static final String GENERIC_LOCATION =
        "  -> enable the matching device-tree overlay for your board/distro and reboot";

    // Raspberry Pi 40-pin header: BCM GPIO number (== main-bank line offset) -> physical header pin.
    private static final Map<Integer, Integer> RPI_40PIN_HEADER = Map.ofEntries(
        Map.entry(2, 3), Map.entry(3, 5), Map.entry(4, 7), Map.entry(14, 8),
        Map.entry(15, 10), Map.entry(17, 11), Map.entry(18, 12), Map.entry(27, 13),
        Map.entry(22, 15), Map.entry(23, 16), Map.entry(24, 18), Map.entry(10, 19),
        Map.entry(9, 21), Map.entry(25, 22), Map.entry(11, 23), Map.entry(8, 24),
        Map.entry(7, 26), Map.entry(0, 27), Map.entry(1, 28), Map.entry(5, 29),
        Map.entry(6, 31), Map.entry(12, 32), Map.entry(13, 33), Map.entry(19, 35),
        Map.entry(16, 36), Map.entry(26, 37), Map.entry(20, 38), Map.entry(21, 40));

    // Ordered most- to least-specific: distro images first (the enable mechanism follows the distro),
    // then silicon vendors, then the catch-all generic entry (no criteria => always matches).
    private static final List<BoardDefinition> REGISTRY = List.of(
        new BoardDefinition("Armbian",
            List.of("/boot/armbianEnv.txt", "/etc/armbian-release"), Set.of(), List.of(),
            overlayHints("  -> add to the 'overlays=' line in /boot/armbianEnv.txt "
                + "(or run armbian-config -> System -> Hardware) and reboot")),
        new BoardDefinition("Raspberry Pi",
            List.of("/boot/firmware/config.txt", "/boot/config.txt"), Set.of("raspberrypi"), List.of("Raspberry Pi"),
            raspberryPiHints()),
        new BoardDefinition("NVIDIA Jetson",
            List.of("/etc/nv_tegra_release", "/opt/nvidia/jetson-io"), Set.of("nvidia"), List.of("Jetson"),
            jetsonHints()),
        new BoardDefinition("Hardkernel ODROID",
            List.of("/boot/config.ini", "/media/boot/boot.ini"), Set.of("hardkernel"), List.of("ODROID"),
            overlayHints("  -> add the overlay to the 'overlays=' line in /boot/config.ini "
                + "(or /media/boot/boot.ini) and reboot")),
        new BoardDefinition("Radxa",
            List.of("/usr/local/sbin/rsetup", "/usr/bin/rsetup"), Set.of("radxa"), List.of("Radxa"),
            overlayHints("  -> run 'rsetup' -> Overlays (Radxa OS), or add to the 'overlays=' line "
                + "in /boot/uEnv.txt, then reboot")),
        new BoardDefinition("Libre Computer",
            List.of("/usr/bin/ldto"), Set.of("libretech"), List.of("Libre Computer"),
            overlayHints("  -> run 'sudo ldto enable <overlay>' (e.g. ldto enable spicc-spidev), then reboot")),
        new BoardDefinition("Orange Pi",
            List.of("/boot/orangepiEnv.txt"), Set.of(), List.of("Orange Pi"),
            overlayHints("  -> add to the 'overlays=' line in /boot/orangepiEnv.txt and reboot")),
        new BoardDefinition("Khadas",
            List.of(), Set.of("khadas"), List.of("Khadas"),
            overlayHints("  -> add the overlay to the 'overlays=' line in /boot/env.txt (Khadas/Fenix) and reboot")),
        new BoardDefinition("Pine64",
            List.of(), Set.of("pine64"), List.of("Pine64"), overlayHints(GENERIC_LOCATION)),
        new BoardDefinition("FriendlyELEC",
            List.of(), Set.of("friendlyarm", "friendlyelec"), List.of("NanoPi"), overlayHints(GENERIC_LOCATION)),
        new BoardDefinition("Banana Pi",
            List.of(), Set.of("sinovoip", "bananapi", "lemaker"), List.of("Banana Pi"), overlayHints(GENERIC_LOCATION)),
        new BoardDefinition("Generic Linux",
            List.of(), Set.of(), List.of(),
            overlayHints("  -> enable it in your board's device tree/overlay and reboot")));

    private final String name;
    private final String kernelVersion;
    private final Map<HWInterfaces, String> enableHints;

    private BoardOverview(String name, String kernelVersion, Map<HWInterfaces, String> enableHints) {
        this.name = name;
        this.kernelVersion = kernelVersion;
        this.enableHints = enableHints;
    }

    /**
     * @return human readable board family name, e.g. {@code "Raspberry Pi"} or {@code "Radxa (ROCK 5B)"}
     */
    public String name() {
        return name;
    }

    /**
     * @return the running Linux kernel release (e.g. {@code "6.8.0-52-generic"}), or {@code "unknown"}
     */
    public String kernelVersion() {
        return kernelVersion;
    }

    /**
     * @param subsystem the subsystem the caller wants to enable
     * @return a board-appropriate instruction describing how to enable it
     */
    public String enableHint(HWInterfaces subsystem) {
        return enableHints.getOrDefault(subsystem, GENERIC_FALLBACK);
    }

    /**
     * Maps a GPIO line offset to the physical board header pin it is exposed on. Only known for the
     * Raspberry Pi 40-pin header today; other boards return {@code null}.
     *
     * @param gpioOffset the line offset on the chip (the BCM GPIO number on a Raspberry Pi main bank)
     * @return the physical header pin (1-40), or {@code null} if unknown for this board/offset
     */
    public Integer physicalPin(int gpioOffset) {
        if (name.startsWith("Raspberry Pi")) {
            return RPI_40PIN_HEADER.get(gpioOffset);
        }
        return null;
    }

    /**
     * Picks the most specific profile for the running machine.
     *
     * @return the detected board profile (never {@code null}; falls back to a generic Linux profile)
     */
    public static BoardOverview detect() {
        return detect(DeviceTree.readModel(), DeviceTree.readCompatible());
    }

    /**
     * Resolves a profile from an explicit device-tree model/compatible (package-private for testing).
     *
     * @param model      the device-tree {@code model} string ({@code ""} if unknown)
     * @param compatible the device-tree {@code compatible} list (board entry first)
     * @return the first matching profile from the registry
     */
    static BoardOverview detect(String model, List<String> compatible) {
        for (var board : REGISTRY) {
            if (board.matches(model, compatible)) {
                return new BoardOverview(board.label(model), readKernelVersion(), board.hints());
            }
        }
        throw new IllegalStateException("REGISTRY must contain a catch-all entry");
    }

    /**
     * Returns the profile for a registry entry by its base family name (package-private for testing).
     *
     * @param baseName the {@link BoardDefinition#name()} to look up
     * @return the matching profile
     */
    static BoardOverview forName(String baseName) {
        for (var board : REGISTRY) {
            if (board.name().equals(baseName)) {
                return new BoardOverview(baseName, readKernelVersion(), board.hints());
            }
        }
        throw new IllegalArgumentException("Unknown board: " + baseName);
    }

    /**
     * Reads the running kernel release, preferring {@code /proc/sys/kernel/osrelease} (the same string as
     * {@code uname -r}) and falling back to the {@code os.version} system property.
     *
     * @return the kernel release string, or {@code "unknown"}
     */
    private static String readKernelVersion() {
        var release = Path.of("/proc/sys/kernel/osrelease");
        if (Files.exists(release)) {
            try {
                return Files.readString(release).strip();
            } catch (IOException e) {
                // fall through to the system property
            }
        }
        return System.getProperty("os.version", "unknown");
    }

    /**
     * One board family: the signals that identify it plus the per-subsystem enable hints.
     *
     * @param name              base display name
     * @param distroFiles       boot-config/tool paths whose presence identifies the distro (any-exists matches)
     * @param compatibleVendors device-tree {@code compatible} vendor prefixes (any match)
     * @param modelSubstrings   device-tree {@code model} substrings (case-insensitive, any match)
     * @param hints             per-subsystem enable hints
     */
    private record BoardDefinition(String name, List<String> distroFiles, Set<String> compatibleVendors,
                                   List<String> modelSubstrings,
                                   Map<HWInterfaces, String> hints) {

        boolean matches(String model, List<String> compatible) {
            // A definition with no criteria is the catch-all (generic) entry.
            if (distroFiles.isEmpty() && compatibleVendors.isEmpty() && modelSubstrings.isEmpty()) {
                return true;
            }
            for (var file : distroFiles) {
                if (Files.exists(Path.of(file))) {
                    return true;
                }
            }
            for (var vendor : compatibleVendors) {
                var prefix = vendor + ",";
                if (compatible.stream().anyMatch(c -> c.startsWith(prefix))) {
                    return true;
                }
            }
            var lower = model.toLowerCase();
            for (var substring : modelSubstrings) {
                if (lower.contains(substring.toLowerCase())) {
                    return true;
                }
            }
            return false;
        }

        String label(String model) {
            if (model == null || model.isBlank() || model.contains(name)) {
                return name;
            }
            return name + " (" + model + ")";
        }
    }

    /**
     * Builds the hint map for boards whose interfaces are toggled by a device-tree overlay; only the
     * {@code location} (where/how the overlay is applied) differs between such boards.
     */
    private static Map<HWInterfaces, String> overlayHints(String location) {
        return Map.of(
            HWInterfaces.I2C, "enable the I2C controller via a device-tree overlay" + location,
            HWInterfaces.SPI, "enable the SPI controller via a device-tree overlay" + location,
            HWInterfaces.PWM, "enable the PWM controller via a device-tree overlay" + location,
            HWInterfaces.GPIO, GPIO_HINT);
    }

    private static Map<HWInterfaces, String> raspberryPiHints() {
        var loc = "  -> add to /boot/firmware/config.txt (or /boot/config.txt) and reboot (or use raspi-config)";
        return Map.of(
            HWInterfaces.I2C, "dtparam=i2c_arm=on" + loc,
            HWInterfaces.SPI, "dtparam=spi=on" + loc,
            HWInterfaces.PWM, "dtoverlay=pwm (or dtoverlay=pwm-2chan)" + loc,
            HWInterfaces.GPIO, GPIO_HINT);
    }

    private static Map<HWInterfaces, String> jetsonHints() {
        var loc = "  -> run 'sudo /opt/nvidia/jetson-io/jetson-io.py' to configure the 40-pin header, then reboot";
        return Map.of(
            HWInterfaces.I2C, "most I2C buses are enabled by default; otherwise reconfigure the header" + loc,
            HWInterfaces.SPI, "enable SPI on the 40-pin header" + loc,
            HWInterfaces.PWM, "enable PWM on the 40-pin header" + loc,
            HWInterfaces.GPIO, GPIO_HINT);
    }


}

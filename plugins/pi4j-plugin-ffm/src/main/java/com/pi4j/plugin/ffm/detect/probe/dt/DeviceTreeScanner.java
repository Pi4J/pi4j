package com.pi4j.plugin.ffm.detect.probe.dt;

import com.pi4j.plugin.ffm.detect.model.BoardOverview;
import com.pi4j.plugin.ffm.detect.model.DetectedHardware;
import com.pi4j.plugin.ffm.detect.model.HWInterfaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Discovers I/O controllers the SoC exposes through the device tree but that have no live {@code /dev}
 * node — controllers that are present but switched off ({@code status = "disabled"}) or whose driver has
 * not bound. Controllers are matched by their DT-spec generic node name (e.g. {@code i2c@…}, {@code spi@…}),
 * which makes the scan vendor-agnostic. File access goes through {@link DeviceTree}.
 */
public final class DeviceTreeScanner {

    private static final Logger logger = LoggerFactory.getLogger(DeviceTreeScanner.class);

    private final BoardOverview profile;

    /**
     * Creates a scanner with an explicit board profile (useful for testing).
     *
     * @param profile supplies the platform-appropriate enable hints
     */
    public DeviceTreeScanner(BoardOverview profile) {
        this.profile = profile;
    }

    /**
     * @return {@code true} if this platform exposes a device tree at all
     */
    public static boolean isAvailable() {
        return DeviceTree.isAvailable();
    }

    /**
     * Walks the device tree and reports controllers for the requested subsystems.
     *
     * @param wanted only return controllers for these subsystems (typically the ones with no live node)
     * @return discovered controllers, each carrying its {@code status} and a board-appropriate enable hint
     */
    public List<DetectedHardware> scan(Set<HWInterfaces> wanted) {
        var root = DeviceTree.root();
        if (root == null || wanted.isEmpty()) {
            return List.of();
        }
        var out = new ArrayList<DetectedHardware>();
        try (Stream<Path> tree = Files.walk(root)) {
            tree.filter(Files::isDirectory).forEach(dir -> {
                var generic = genericName(dir.getFileName().toString());
                var subsystem = HWInterfaces.byDtGenericName(generic).orElse(null);
                if (subsystem == null || !wanted.contains(subsystem)) {
                    return;
                }
                // A real controller node always carries a 'compatible' property.
                var compatibleFile = dir.resolve("compatible");
                if (!Files.exists(compatibleFile)) {
                    return;
                }
                var compatible = DeviceTree.readStrings(compatibleFile);
                var status = DeviceTree.readString(dir.resolve("status")); // absent => "okay" per the DT spec
                var enabled = status == null || status.equals("okay") || status.equals("ok");

                // The first 'compatible' entry is the most specific binding — i.e. the driver match.
                var driver = compatible.isEmpty() ? null : compatible.getFirst();
                var remaining = compatible.size() > 1
                    ? String.join(", ", compatible.subList(1, compatible.size())) + " "
                    : "";
                var description = remaining + "[status=" + (status == null ? "okay" : status) + "]";
                var state = enabled
                    ? DetectedHardware.State.NOT_BOUND // okay in DT but no /dev node => driver not bound
                    : DetectedHardware.State.DISABLED; // explicitly switched off

                out.add(new DetectedHardware(subsystem, DetectedHardware.Source.DEVICE_TREE,
                    state, dir.getFileName().toString(), driver, description, profile.enableHint(subsystem),
                    List.of()));
            });
        } catch (IOException e) {
            logger.warn("Could not walk device tree at {}: {}", root, e.getMessage());
        }
        out.sort(Comparator.comparing(a -> (a.subsystem().getLabel() + a.identifier())));
        return out;
    }
    private static String genericName(String node) {
        var at = node.indexOf('@');
        return at >= 0 ? node.substring(0, at) : node;
    }
}

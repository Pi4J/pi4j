package com.pi4j.plugin.ffm.detect.model;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Structured result of a hardware detection run.
 *
 * @param boardName         the detected board family (e.g. {@code "Raspberry Pi"}, {@code "Radxa (ROCK 5B)"})
 * @param kernelVersion     the running Linux kernel release (e.g. {@code "6.8.0-52-generic"})
 * @param controllers       every controller discovered, in detection order
 * @param deviceTreePresent whether the platform exposes a device tree (false on most x86/closed firmware machines)
 */
public record DetectionReport(String boardName, String kernelVersion, List<DetectedHardware> controllers,
                              boolean deviceTreePresent) {

    /**
     * @param subsystem the subsystem to filter by
     * @return all controllers of the given subsystem
     */
    public List<DetectedHardware> of(HWInterfaces subsystem) {
        return controllers.stream().filter(c -> c.subsystem() == subsystem).toList();
    }

    /**
     * @return the set of subsystems for which at least one live, ready-to-use controller exists
     */
    public EnumSet<HWInterfaces> activeSubsystems() {
        var set = EnumSet.noneOf(HWInterfaces.class);
        for (var c : controllers) {
            if (c.isActive()) {
                set.add(c.subsystem());
            }
        }
        return set;
    }

    /**
     * Renders the report as a tidy, multi-line block intended to be emitted through a logging framework
     * (e.g. logback). It leads with a newline so the first line is not appended to the log prefix, indents
     * the body, and lays out controllers — and each GPIO chip's line map — as aligned, underline-separated
     * tables that survive log wrapping (no full box borders that break on long, variable cells).
     *
     * @return formatted report text
     */
    public String renderSummery() {
        var sb = new StringBuilder();
        sb.append('\n');
        sb.append("Hardware I/O detection\n");
        sb.append("  Board       : ").append(boardName).append('\n');
        sb.append("  Kernel      : ").append(kernelVersion).append('\n');
        sb.append("  Device tree : ")
            .append(deviceTreePresent ? "present" : "absent (x86/ACPI/closed firmware)").append('\n');

        if (controllers.isEmpty()) {
            sb.append("\n  (no I/O controllers discovered)\n");
            return sb.toString();
        }

        // Controllers overview table.
        sb.append('\n');
        var rows = new ArrayList<List<String>>();
        for (var c : controllers) {
            rows.add(List.of(c.state().name(), c.subsystem().getLabel(), c.identifier(),
                blankToDash(c.driver()), blankToDash(c.description())));
        }
        appendTable(sb, "  ", List.of("State", "Subsystem", "Identifier", "Driver", "Details"), rows);

        // Enable hints for anything present but not active.
        var hinted = controllers.stream().filter(c -> !c.isActive() && c.enableHint() != null).toList();
        if (!hinted.isEmpty()) {
            sb.append("\n  Enable hints:\n");
            for (var c : hinted) {
                sb.append("    ").append(c.subsystem().getLabel()).append(' ').append(c.identifier())
                    .append("  →  ").append(c.enableHint()).append('\n');
            }
        }
        return sb.toString();
    }

    public String renderGpioDetails() {
        var sb = new StringBuilder();
        // Per-chip GPIO line maps.
        for (var c : controllers) {
            if (c.subsystem() == HWInterfaces.GPIO && !c.lines().isEmpty()) {
                sb.append("\n  GPIO ").append(c.identifier()).append(" line map:\n");
                var lineRows = new ArrayList<List<String>>();
                for (var line : c.lines()) {
                    lineRows.add(List.of(
                        Integer.toString(line.offset()),
                        line.physicalPin() == null ? "-" : line.physicalPin().toString(),
                        line.name() == null || line.name().isBlank() ? "unnamed" : line.name(),
                        line.used() ? "yes" : "-"));
                }
                appendTable(sb, "    ", List.of("Offset", "Pin", "Name", "Used"), lineRows);
            }
        }
        return sb.toString();
    }

    // Cap column width so a very long cell (e.g. the decoded I2C functionality list) wraps instead of
    // producing an unreadably wide line / separator in the logs.
    private static final int MAX_COLUMN_WIDTH = 150;

    /**
     * Appends an aligned table: a header row, an underline separator, then the data rows. Columns are
     * left-aligned and sized to their widest cell (capped at {@link #MAX_COLUMN_WIDTH}); cells wider than
     * their column are word-wrapped onto continuation lines. Cells are separated by a two-space gutter.
     */
    private static void appendTable(StringBuilder sb, String indent, List<String> headers, List<List<String>> rows) {
        var columns = headers.size();
        var width = new int[columns];
        for (var i = 0; i < columns; i++) {
            width[i] = headers.get(i).length();
        }
        for (var row : rows) {
            for (var i = 0; i < columns; i++) {
                width[i] = Math.max(width[i], row.get(i).length());
            }
        }
        for (var i = 0; i < columns; i++) {
            width[i] = Math.min(width[i], MAX_COLUMN_WIDTH);
        }
        appendRow(sb, indent, headers, width);
        var separators = new ArrayList<String>(columns);
        for (var i = 0; i < columns; i++) {
            separators.add("─".repeat(width[i]));
        }
        appendRow(sb, indent, separators, width);
        for (var row : rows) {
            appendRow(sb, indent, row, width);
        }
    }

    /**
     * Renders one logical row, wrapping each over-long cell to its column width and emitting as many
     * physical lines as the tallest cell needs (other columns left blank on the continuation lines).
     */
    private static void appendRow(StringBuilder sb, String indent, List<String> cells, int[] width) {
        var wrapped = new ArrayList<List<String>>(cells.size());
        var lineCount = 1;
        for (var i = 0; i < cells.size(); i++) {
            var cellLines = wrap(cells.get(i), width[i]);
            wrapped.add(cellLines);
            lineCount = Math.max(lineCount, cellLines.size());
        }
        for (var k = 0; k < lineCount; k++) {
            var line = new StringBuilder(indent);
            for (var i = 0; i < width.length; i++) {
                if (i > 0) {
                    line.append("  ");
                }
                var cellLines = wrapped.get(i);
                var cell = k < cellLines.size() ? cellLines.get(k) : "";
                line.append(cell);
                if (i < width.length - 1) {
                    line.repeat(" ", width[i] - cell.length()); // pad every column except the last
                }
            }
            sb.append(line.toString().stripTrailing()).append('\n');
        }
    }

    /**
     * Word-wraps {@code text} to at most {@code width} characters per line, hard-splitting any single word
     * longer than the column.
     */
    private static List<String> wrap(String text, int width) {
        if (text.length() <= width) {
            return List.of(text);
        }
        var lines = new ArrayList<String>();
        var current = new StringBuilder();
        for (var word : text.split(" ")) {
            while (word.length() > width) {
                if (!current.isEmpty()) {
                    lines.add(current.toString());
                    current.setLength(0);
                }
                lines.add(word.substring(0, width));
                word = word.substring(width);
            }
            if (current.isEmpty()) {
                current.append(word);
            } else if (current.length() + 1 + word.length() <= width) {
                current.append(' ').append(word);
            } else {
                lines.add(current.toString());
                current.setLength(0);
                current.append(word);
            }
        }
        if (!current.isEmpty()) {
            lines.add(current.toString());
        }
        return lines;
    }

    private static String blankToDash(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }
}

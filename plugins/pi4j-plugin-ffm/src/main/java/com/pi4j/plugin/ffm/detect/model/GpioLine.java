package com.pi4j.plugin.ffm.detect.model;

/**
 * One line of a GPIO chip, as reported by the kernel's GPIO v2 line-info ioctl and enriched with the
 * board's physical header-pin mapping.
 *
 * @param offset       the line's local offset on the chip (0-based) — on a Raspberry Pi main bank this equals
 *                     the BCM GPIO number
 * @param physicalPin  the physical pin on the board header this line maps to, or {@code null} when the board
 *                     mapping is unknown (non-Raspberry-Pi, or a line not exposed on the header)
 * @param name         the kernel-provided line name (from the device-tree {@code gpio-line-names}), or
 *                     {@code null} when the line is unnamed
 * @param used         whether the line is currently requested/in use by a consumer
 */
public record GpioLine(int offset, Integer physicalPin, String name, boolean used) {
}

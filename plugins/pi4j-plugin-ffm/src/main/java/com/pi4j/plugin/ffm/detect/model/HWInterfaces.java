package com.pi4j.plugin.ffm.detect.model;

import java.util.List;
import java.util.Optional;

/**
 * The kinds of hardware I/O the detector can discover.
 * <p>
 * Each subsystem carries two pieces of knowledge used during detection:
 * <ul>
 *     <li>the {@code /dev} node prefixes the kernel uses when the controller is live and a driver is bound, and</li>
 *     <li>the generic device-tree node name (the part before {@code @}) as recommended by the DT spec.</li>
 * </ul>
 * How to <em>enable</em> a disabled controller is board-specific and therefore lives in a
 * {@code BoardProfile}, not here.
 */
public enum HWInterfaces {

    GPIO("GPIO", "gpio", List.of("gpiochip")),

    I2C("I2C", "i2c", List.of("i2c-")),

    SPI("SPI", "spi", List.of("spidev")),

    PWM("PWM", "pwm", List.of());

    private final String label;
    private final String dtGenericName;
    private final List<String> devPrefixes;

    HWInterfaces(String label, String dtGenericName, List<String> devPrefixes) {
        this.label = label;
        this.dtGenericName = dtGenericName;
        this.devPrefixes = devPrefixes;
    }

    /**
     * @return short display label, e.g. {@code "GPIO"}
     */
    public String getLabel() {
        return label;
    }

    /**
     * Returns {@code true} if the given {@code /dev} node name belongs to this subsystem.
     *
     * @param devNodeName a file name from {@code /dev}, e.g. {@code "i2c-1"}
     * @return whether the node name matches one of this subsystem's prefixes
     */
    public boolean matchesDevNode(String devNodeName) {
        return devPrefixes.stream().anyMatch(devNodeName::startsWith);
    }

    /**
     * Resolves a subsystem from a device-tree generic node name.
     *
     * @param genericName the node name with any {@code @unit-address} suffix already stripped
     * @return the matching subsystem, if any
     */
    public static Optional<HWInterfaces> byDtGenericName(String genericName) {
        for (var sub : values()) {
            if (sub.dtGenericName.equals(genericName)) {
                return Optional.of(sub);
            }
        }
        return Optional.empty();
    }
}

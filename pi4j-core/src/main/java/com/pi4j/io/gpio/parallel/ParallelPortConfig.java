package com.pi4j.io.gpio.parallel;

import com.pi4j.io.IOConfig;
import com.pi4j.io.gpio.MaskUtils;
import com.pi4j.io.gpio.digital.PullResistance;

import java.util.List;
import java.util.Map;

/**
 * Configuration for a parallel port
 * @param id the user-provided ID
 * @param name the user-provided name
 * @param description the user-provided description
 * @param bus the bus number / GPIO chip
 * @param offsets bit mask representing the pins in the port
 * @param pull the pull resistance setting
 * @param debounce the debounce time in microseconds
 * @param initialValue the initial value of the port
 * @param shutdownValue the value applied to the port on shutdown (can be null)
 * @param initialDirection the initial direction of the port
 */
public record ParallelPortConfig(
    @Override String id,
    @Override String name,
    @Override String description,
    int bus,
    List<Integer> offsets,
    // input
    PullResistance pull,
    long debounce,
    // output
    int initialValue,
    Integer shutdownValue,
    // port
    ParallelPort.Direction initialDirection
) implements IOConfig {

    public long mask() {
        return MaskUtils.mask(offsets);
    }

    @Override
    public int getUniqueIdentifier() {
        return (int) mask();
    }

    @Override
    public String provider() {
        return null;
    }

    @Override
    public Map<String, String> properties() {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public void validate() {
        // this current core codebase doesn't seem to do anything here except check that the ID is non-null
        // `@NonNull` annotations could be used to indicate this requirement, so leaving faults up to the user(?)
        if (id == null) {
            throw new IllegalStateException("ID cannot be null");
        }
    }
}

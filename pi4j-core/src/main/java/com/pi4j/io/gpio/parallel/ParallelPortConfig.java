package com.pi4j.io.gpio.parallel;

import com.pi4j.io.IOConfig;
import com.pi4j.io.gpio.digital.DigitalInputConfig;
import com.pi4j.io.gpio.digital.DigitalInputConfigBuilder;
import com.pi4j.io.gpio.digital.DigitalOutputConfig;
import com.pi4j.io.gpio.digital.DigitalOutputConfigBuilder;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.util.MaskUtils;
import com.pi4j.io.gpio.digital.PullResistance;

import java.util.List;

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
    public void validate() {
        // this current core codebase doesn't seem to do anything here except check that the ID is non-null
        // `@NonNull` annotations could be used to indicate this requirement, so leaving faults up to the user(?)
        if (id == null) {
            throw new IllegalStateException("ID cannot be null");
        }
    }

    public DigitalInputConfig toDigitalInputConfig() {
        return DigitalInputConfigBuilder.newInstance()
                .id(id)
                .name(name)
                .description(description)
                .bcm(offsets.getFirst()) // only the first offset is used for a single digital input
                .pull(pull)
                .debounce(debounce)
                .build();
    }

    public DigitalOutputConfig toDigitalOutputConfig() {
        return DigitalOutputConfigBuilder.newInstance()
            .id(id)
            .name(name)
            .description(description)
            .bcm(offsets.getFirst()) // only the first offset is used for a single digital output
            .initial(DigitalState.getState(initialValue != 0))
            .shutdown(shutdownValue != null ? DigitalState.getState(shutdownValue != 0) : null)
            .build();
    }

    public static ParallelPortConfig fromDigitalInputConfig(DigitalInputConfig config) {
        return ParallelPortConfigBuilder.newInstance()
            .id(config.id())
            .name(config.name())
            .description(config.description())
            .bus(config.bus())
            .bcm(1 << config.bcm())
            .pull(config.pull())
            .debounce(config.debounce())
            .initialDirection(ParallelPort.Direction.INPUT)
            .build();
    }

    public static ParallelPortConfig fromDigitalOutputConfig(DigitalOutputConfig config) {
        return ParallelPortConfigBuilder.newInstance()
            .id(config.id())
            .name(config.name())
            .description(config.description())
            .bus(config.bus())
            .bcm(1 << config.bcm())
            .initialValue(config.initialState().isHigh() ? 1 : 0)
            .shutdownValue(config.shutdownState() != null ? (config.shutdownState().isHigh() ? 1 : 0) : null)
            .initialDirection(ParallelPort.Direction.OUTPUT)
            .build();
    }
}

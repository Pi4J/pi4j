package com.pi4j.io.gpio.digital;


import com.pi4j.context.Context;

/**
 * Configuration contract for a {@link DigitalInput}, extending {@link DigitalConfig} with the input's
 * pull resistance and debounce interval. Instances are typically assembled through a
 * {@link DigitalInputConfigBuilder}.
 */
public interface DigitalInputConfig extends DigitalConfig<DigitalInputConfig> {

    /** Property key under which the pull resistance value is stored in a configuration map. */
    String PULL_RESISTANCE_KEY = "pull";
    /** Property key under which the debounce interval is stored in a configuration map. */
    String DEBOUNCE_RESISTANCE_KEY = "debounce";

    /**
     * Returns the pull resistance (pull-up, pull-down, or none) applied to the input pin.
     *
     * @return the configured {@link PullResistance}
     */
    PullResistance pull();

    /**
     * Bean-style accessor equivalent to {@link #pull()}.
     *
     * @return the configured {@link PullResistance}
     */
    default PullResistance getPull(){
        return pull();
    }

    /**
     * Returns the debounce interval, in microseconds, used to filter spurious state transitions.
     *
     * @return the configured debounce interval in microseconds, or {@code null} if unset
     */
    Long debounce();

    /**
     * Bean-style accessor equivalent to {@link #debounce()}.
     *
     * @return the configured debounce interval in microseconds, or {@code null} if unset
     */
    default Long getDebounce(){ return debounce(); }

    /**
     * Creates a new {@link DigitalInputConfigBuilder}.
     *
     * @param context the Pi4J context
     * @return a new configuration builder instance
     * @deprecated As of version 5, please use {@link #newBuilder()} instead.
     */
    @Deprecated
    static DigitalInputConfigBuilder newBuilder(Context context)  {
        return DigitalInputConfigBuilder.newInstance(context);
    }

    /**
     * Creates a new {@link DigitalInputConfigBuilder} for assembling a digital input configuration.
     *
     * @return a new configuration builder instance
     */
    static DigitalInputConfigBuilder newBuilder()  {
        return DigitalInputConfigBuilder.newInstance();
    }
}

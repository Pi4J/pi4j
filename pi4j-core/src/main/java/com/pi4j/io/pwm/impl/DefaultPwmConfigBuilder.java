package com.pi4j.io.pwm.impl;

import com.pi4j.context.Context;
import com.pi4j.io.impl.IOBcmConfigBuilderBase;
import com.pi4j.io.pwm.*;

import java.util.ArrayList;
import java.util.List;

public class DefaultPwmConfigBuilder
    extends IOBcmConfigBuilderBase<PwmConfigBuilder, PwmConfig>
    implements PwmConfigBuilder {

    /**
     * PRIVATE CONSTRUCTOR
     */
    protected DefaultPwmConfigBuilder() {
    }

    /**
     * @param context
     */
    @Deprecated
    public static PwmConfigBuilder newInstance(Context context) {
        return newInstance();
    }


    public static PwmConfigBuilder newInstance() {
        return new DefaultPwmConfigBuilder();
    }

    /**
     * @deprecated use {@link #channel(Integer)} instead.
     * <p>
     * {@inheritDoc}
     */
    @Override
    @Deprecated(forRemoval = true)
    public PwmConfigBuilder address(Integer address) {
        this.properties.put(PwmConfig.PWM_ADDRESS, address.toString());
        return this;
    }

    @Override
    public PwmConfigBuilder chip(Integer chip) {
        this.properties.put(PwmConfig.PWM_CHIP, chip.toString());
        return this;
    }

    @Override
    public PwmConfigBuilder channel(Integer channel) {
        this.properties.put(PwmConfig.PWM_CHANNEL, channel.toString());
        return this;
    }

    @Override
    public PwmConfigBuilder bcm(Integer bcm) {
        this.properties.put(PwmConfig.PWM_BCM, bcm.toString());
        return this;
    }

    @Override
    public PwmConfigBuilder frequency(Double frequency) {
        this.properties.put(PwmConfig.FREQUENCY_KEY, frequency.toString());
        return this;
    }

    @Override
    public PwmConfigBuilder dutyCycle(Double dutyCycle) {
        // bounds check the duty-cycle value
        double dc = dutyCycle;
        if (dc < 0) dc = 0;
        if (dc > 100) dc = 100;

        this.properties.put(PwmConfig.DUTY_CYCLE_KEY, Double.toString(dc));
        return this;
    }

    @Override
    public PwmConfigBuilder pwmType(PwmType pwmType) {
        this.properties.put(PwmConfig.PWM_TYPE_KEY, pwmType.toString());
        return this;
    }

    @Override
    public PwmConfigBuilder polarity(PwmPolarity polarity) {
        this.properties.put(PwmConfig.POLARITY_KEY, polarity.toString());
        return this;
    }

    @Override
    public PwmConfigBuilder shutdown(Double dutyCycle) {
        // bounds check the duty-cycle value
        Double dc = dutyCycle;
        if (dc < 0) dc = 0.0;
        if (dc > 100) dc = 100.0;

        this.properties.put(PwmConfig.SHUTDOWN_VALUE_KEY, Double.toString(dc));
        return this;
    }

    @Override
    public PwmConfigBuilder initial(Double dutyCycle) {
        // bounds check the duty-cycle value
        Double dc = dutyCycle;
        if (dc < 0) dc = 0.0;
        if (dc > 100) dc = 100.0;

        this.properties.put(PwmConfig.INITIAL_VALUE_KEY, Double.toString(dc));
        return this;
    }

    @Override
    public PwmConfig build() {
        PwmConfig config = new DefaultPwmConfig(getResolvedProperties());
        return config;
    }
}

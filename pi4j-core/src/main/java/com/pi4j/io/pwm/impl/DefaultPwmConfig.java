package com.pi4j.io.pwm.impl;

import com.pi4j.io.gpio.digital.PullResistance;
import com.pi4j.io.impl.IOConfigBase;
import com.pi4j.io.pwm.PwmConfig;
import com.pi4j.io.pwm.PwmPolarity;
import com.pi4j.io.pwm.PwmType;
import com.pi4j.util.StringUtil;

import java.util.Map;

public class DefaultPwmConfig
    extends IOConfigBase<PwmConfig>
    implements PwmConfig {

    protected Integer chip = null;
    protected Integer channel = null;
    protected Integer bcm = null;

    // private configuration properties
    protected Double dutyCycle = null;
    protected Double frequency = null;
    protected PwmType pwmType = PwmType.SOFTWARE;
    protected PwmPolarity polarity = PwmPolarity.NORMAL;
    protected Double shutdownValue = null;
    protected Double initialValue = null;

    /**
     * PRIVATE CONSTRUCTOR
     */
    private DefaultPwmConfig() {
        super();
    }

    // private configuration properties
    protected PullResistance pullResistance = PullResistance.OFF;

    /**
     * PRIVATE CONSTRUCTOR
     */
    protected DefaultPwmConfig(Map<String, String> properties) {
        super(properties);

        if (properties.containsKey(PWM_CHIP)) {
            this.chip = Integer.valueOf(properties.get(PWM_CHIP));
        }

        if (properties.containsKey(PWM_CHANNEL)) {
            this.channel = Integer.valueOf(properties.get(PWM_CHANNEL));
        }

        if (properties.containsKey(PWM_BCM)) {
            this.bcm = Integer.valueOf(properties.get(PWM_BCM));
        }

        // load optional pwm duty-cycle from properties
        if (properties.containsKey(DUTY_CYCLE_KEY)) {
            this.dutyCycle = Double.parseDouble(properties.get(DUTY_CYCLE_KEY));
        }

        // load optional pwm frequency from properties
        if (properties.containsKey(FREQUENCY_KEY)) {
            this.frequency = Double.parseDouble(properties.get(FREQUENCY_KEY));
        }

        // load optional pwm type from properties
        if (properties.containsKey(PWM_TYPE_KEY)) {
            this.pwmType = PwmType.parse(properties.get(PWM_TYPE_KEY));
        }

        // load optional pwm type from properties
        if (properties.containsKey(POLARITY_KEY)) {
            this.polarity = PwmPolarity.parse(properties.get(POLARITY_KEY));
        }

        // load initial value property
        if (properties.containsKey(INITIAL_VALUE_KEY)) {
            this.initialValue = Double.parseDouble(properties.get(INITIAL_VALUE_KEY));
        }

        // load shutdown value property
        if (properties.containsKey(SHUTDOWN_VALUE_KEY)) {
            this.shutdownValue = Double.parseDouble(properties.get(SHUTDOWN_VALUE_KEY));
        }

        // bounds checking
        if (this.dutyCycle != null && this.dutyCycle > 100) {
            this.dutyCycle = 100.0;
        }

        // bounds checking
        if (this.dutyCycle != null && this.dutyCycle < 0) {
            this.dutyCycle = 0.0;
        }

        // define default property values if any are missing (based on the required address value)
        this.id = StringUtil.setIfNullOrEmpty(this.id, "PWM-" + this.channel, true);
        this.name = StringUtil.setIfNullOrEmpty(this.name, "PWM-" + this.channel, true);
        this.description = StringUtil.setIfNullOrEmpty(this.description, "PWM-" + this.channel, true);
    }

    /**
     * @deprecated use {@link #channel()} ()} instead.
     */
    @Override
    @Deprecated(forRemoval = true)
    public Integer address() {
        return this.channel;
    }

    @Override
    public Integer chip() {
        return this.chip;
    }

    @Override
    public Integer channel() {
        return this.channel;
    }

    @Override
    public Integer bcm() {
        return this.bcm;
    }

    @Override
    public int getUniqueIdentifier() {
        return (chip == null ? 0 : (chip << 16))
            + (channel == null ? 0 : (channel << 8))
            + (bcm == null ? 0 : bcm);
    }

    @Override
    public Double dutyCycle() {
        return this.dutyCycle;
    }

    @Override
    public Double frequency() {
        return this.frequency;
    }

    @Override
    public PwmType pwmType() {
        return this.pwmType;
    }

    @Override
    public PwmPolarity polarity() {
        return this.polarity;
    }

    @Override
    public Double shutdownValue() {
        return this.shutdownValue;
    }

    @Override
    public PwmConfig shutdownValue(Double dutyCycle) {

        // bounds check the duty-cycle value
        Double dc = dutyCycle;
        if (dc < 0) dc = 0.0;
        if (dc > 100) dc = 100.0;

        this.shutdownValue = dc;
        return this;
    }

    @Override
    public Double initialValue() {
        return this.initialValue;
    }
}

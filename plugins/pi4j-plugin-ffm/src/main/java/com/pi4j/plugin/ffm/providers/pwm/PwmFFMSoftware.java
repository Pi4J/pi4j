package com.pi4j.plugin.ffm.providers.pwm;

import com.pi4j.io.exception.IOException;
import com.pi4j.io.pwm.Pwm;
import com.pi4j.io.pwm.PwmBase;
import com.pi4j.io.pwm.PwmConfig;
import com.pi4j.io.pwm.PwmProvider;

public class PwmFFMSoftware extends PwmBase implements Pwm {
    /**
     * <p>Constructor for PwmBase.</p>
     *
     * @param provider a {@link PwmProvider} object.
     * @param config   a {@link PwmConfig} object.
     */
    public PwmFFMSoftware(PwmProvider provider, PwmConfig config) {
        super(provider, config);
    }

    @Override
    public Pwm on() throws IOException {
        return null;
    }

    @Override
    public Pwm off() throws IOException {
        return null;
    }
}

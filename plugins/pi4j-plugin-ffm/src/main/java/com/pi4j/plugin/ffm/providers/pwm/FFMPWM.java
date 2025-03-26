package com.pi4j.plugin.ffm.providers.pwm;

import com.pi4j.context.Context;
import com.pi4j.exception.InitializeException;
import com.pi4j.io.exception.IOException;
import com.pi4j.io.pwm.Pwm;
import com.pi4j.io.pwm.PwmBase;
import com.pi4j.io.pwm.PwmConfig;
import com.pi4j.io.pwm.PwmProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FFMPWM extends PwmBase implements Pwm {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());


    public FFMPWM(PwmProvider provider, PwmConfig config, int range){
        super(provider, config);
    }

    @Override
    public Pwm on() throws IOException {
        return this;
    }

    @Override
    public Pwm off() throws IOException {
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public int getActualFrequency() {
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public Pwm initialize(Context context) throws InitializeException {
        return super.initialize(context);
    }
}

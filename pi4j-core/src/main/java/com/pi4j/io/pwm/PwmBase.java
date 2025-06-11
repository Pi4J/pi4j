package com.pi4j.io.pwm;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  PwmBase.java
 *
 * This file is part of the Pi4J project. More information about
 * this project can be found here:  https://pi4j.com/
 * **********************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.pi4j.context.Context;
import com.pi4j.exception.InitializeException;
import com.pi4j.exception.ShutdownException;
import com.pi4j.io.IOBase;
import com.pi4j.io.exception.IOException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>Abstract PwmBase class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public abstract class PwmBase extends IOBase<Pwm, PwmConfig, PwmProvider> implements Pwm {

    protected int frequency = 100;
    protected float dutyCycle = 50;
    protected long period = TimeUnit.NANOSECONDS.convert(1, TimeUnit.SECONDS) / frequency;
    protected boolean onState = false;
    protected PwmPolarity polarity = PwmPolarity.NORMAL;
    protected Map<String, PwmPreset> presets = Collections.synchronizedMap(new HashMap<>());

    /**
     * <p>Constructor for PwmBase.</p>
     *
     * @param provider a {@link com.pi4j.io.pwm.PwmProvider} object.
     * @param config a {@link com.pi4j.io.pwm.PwmConfig} object.
     */
    public PwmBase(PwmProvider provider, PwmConfig config) {
        super(provider, config);
        for(PwmPreset preset : config.presets()){
            this.presets.put(preset.name().toLowerCase().trim(), preset);
        }
    }

    /** {@inheritDoc} */
    @Override
    public float getDutyCycle() throws IOException {
        return this.dutyCycle;
    }

    /** {@inheritDoc} */
    @Override
    public int getFrequency() throws IOException {
        return this.frequency;
    }

    /** {@inheritDoc} */
    @Override
    public int getActualFrequency() throws IOException {
        return this.frequency;
    }

    /** {@inheritDoc} */
    @Override
    public void setDutyCycle(Number dutyCycle) throws IOException {
        float dc = dutyCycle.floatValue();

        // bounds check the duty-cycle value
        if(dc < 0) dc = 0;
        if(dc > 100) dc = 100;

        // update the duty-cycle member
        this.dutyCycle = dc;
    }

    /** {@inheritDoc} */
    @Override
    public void setFrequency(int frequency) throws IOException {
        this.frequency = frequency;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isOn() {
        return this.onState;
    }

    /** {@inheritDoc} */
    @Override
    public Pwm initialize(Context context) throws InitializeException {

        // apply initial frequency value if configured
        if (this.config.frequency() != null) {
            this.frequency = config.frequency();
        }

        // apply initial duty-cycle value if configured
        if (config.dutyCycle() != null) {
            this.dutyCycle = config.dutyCycle();
        } else {
            this.dutyCycle = 50;  // default duty-cycle is 50% of total range
        }

        // apply initial polarity value if configured
        if (config.polarity() != null) {
            this.polarity = config.polarity();
        } else {
            this.polarity = PwmPolarity.NORMAL;
        }

        // apply an initial value if configured
        if(this.config.initialValue() != null){
            try {
                if(this.config.initialValue() <= 0){
                    if(this.isOn()) {
                        this.off();
                    }
                } else {
                    this.on(this.config.initialValue());
                }
            } catch (IOException e) {
                throw new InitializeException(e);
            }
        }

        return this;
    }

    /** {@inheritDoc} */
    @Override
    public Pwm shutdown(Context context) throws ShutdownException {
        // apply a shutdown value if configured
        if(this.config.shutdownValue() != null){
            try {
                if(this.config.shutdownValue() <= 0){
                    this.off();
                } else {
                    this.on(this.config.shutdownValue());
                }
            } catch (IOException e) {
                throw new ShutdownException(e);
            }
        }
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, PwmPreset> getPresets(){
        return Collections.unmodifiableMap(this.presets);
    }

    /** {@inheritDoc} */
    @Override
    public PwmPreset getPreset(String name){
        String key = name.toLowerCase().trim();
        if(presets.containsKey(key)) {
            return presets.get(key);
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public PwmPreset deletePreset(String name){
        String key = name.toLowerCase().trim();
        if(presets.containsKey(key)) {
            return presets.remove(key);
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Pwm addPreset(PwmPreset preset){
        String key = preset.name().toLowerCase().trim();
        presets.put(key, preset);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public Pwm applyPreset(String name) throws IOException {
        String key = name.toLowerCase().trim();
        if(presets.containsKey(key)) {
            PwmPreset preset = presets.get(key);
            if(preset.dutyCycle() != null)
                setDutyCycle(preset.dutyCycle().floatValue());
            if(preset.frequency() != null)
                setFrequency(preset.frequency().intValue());
            on(); // update PWM signal now
        } else{
            throw new IOException("PWM PRESET NOT FOUND: "+ name);
        }
        return this;
    }
}

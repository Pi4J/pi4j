package com.pi4j.plugin.mock.provider.pwm;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: PLUGIN   :: Mock Platform & Providers
 * FILENAME      :  MockPwm.java
 *
 * This file is part of the Pi4J project. More information about
 * this project can be found here:  https://pi4j.com/
 * **********************************************************************
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import com.pi4j.io.exception.IOException;
import com.pi4j.io.pwm.Pwm;
import com.pi4j.io.pwm.PwmBase;
import com.pi4j.io.pwm.PwmConfig;
import com.pi4j.io.pwm.PwmProvider;


/**
 * Mock, in-memory implementation of the pi4j-core {@link Pwm} contract, extending {@link PwmBase}.
 * It tracks the requested on/off state and configured frequency and duty cycle purely in memory,
 * without driving any real PWM hardware, so PWM-based code can be exercised in unit tests.
 */
public class MockPwm extends PwmBase implements Pwm {

    /**
     * Creates a mock PWM instance for the given provider and configuration.
     *
     * @param provider the {@link PwmProvider} that created this instance
     * @param config   the {@link PwmConfig} describing the PWM channel, frequency and duty cycle
     */
    public MockPwm(PwmProvider provider, PwmConfig config){
        super(provider, config);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Simulates enabling the PWM signal by recording the on-state in memory; no hardware output
     * is produced.
     */
    @Override
    public Pwm on() throws IOException {
        this.onState = true;
        return this;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Simulates disabling the PWM signal by recording the off-state in memory; no hardware output
     * is produced.
     */
    @Override
    public Pwm off() throws IOException {
        this.onState = false;
        return this;
    }
}

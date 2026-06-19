package com.pi4j.plugin.mock.provider.pwm;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: PLUGIN   :: Mock Platform & Providers
 * FILENAME      :  MockPwmProvider.java
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

import com.pi4j.io.pwm.PwmProvider;
import com.pi4j.plugin.mock.Mock;

/**
 * Mock, in-memory {@link PwmProvider} used for testing and for running Pi4J on machines
 * without real PWM hardware. It creates {@link MockPwm} instances that record the requested
 * PWM state in memory instead of driving a hardware PWM channel.
 */
public interface MockPwmProvider extends PwmProvider {
    /** The human-readable provider name, {@link Mock#PWM_PROVIDER_NAME}. */
    String NAME = Mock.PWM_PROVIDER_NAME;
    /** The unique provider identifier, {@link Mock#PWM_PROVIDER_ID}. */
    String ID = Mock.PWM_PROVIDER_ID;

    /**
     * Creates a new mock PWM provider instance.
     *
     * @return a new {@link MockPwmProvider} backed by an in-memory implementation
     */
    static MockPwmProvider newInstance() {
        return new MockPwmProviderImpl();
    }
}

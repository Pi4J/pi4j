package com.pi4j.provider.impl;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  DefaultProviders.java
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

import com.pi4j.io.IOType;
import com.pi4j.io.gpio.analog.AnalogInputProvider;
import com.pi4j.io.gpio.analog.AnalogOutputProvider;
import com.pi4j.io.gpio.digital.DigitalInputProvider;
import com.pi4j.io.gpio.digital.DigitalOutputProvider;
import com.pi4j.io.i2c.I2CProvider;
import com.pi4j.io.onewire.OneWireProvider;
import com.pi4j.io.pwm.PwmProvider;
import com.pi4j.io.serial.SerialProvider;
import com.pi4j.io.spi.SpiProvider;
import com.pi4j.provider.Provider;
import com.pi4j.provider.ProviderGroup;
import com.pi4j.provider.Providers;
import com.pi4j.provider.exception.ProviderNotFoundException;

import java.util.Map;

/**
 * <p>
 * </p>
 *
 * @see <a href="http://www.pi4j.com/">http://www.pi4j.com/</a>
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public class DefaultProviders implements Providers {

    private RuntimeProviders providers = null;

    /**
     * <p>newInstance.</p>
     *
     * @param providers a {@link com.pi4j.provider.impl.RuntimeProviders} object.
     * @return a {@link com.pi4j.provider.Providers} object.
     */
    public static Providers newInstance(RuntimeProviders providers){
        return new DefaultProviders(providers);
    }

    // private constructor
    private DefaultProviders(RuntimeProviders providers) {
        // set local reference
        this.providers = providers;
    }

    /** {@inheritDoc} */
    @Override
    public ProviderGroup<AnalogInputProvider> analogInput() {
        return providers.analogInput();
    }

    /** {@inheritDoc} */
    @Override
    public ProviderGroup<AnalogOutputProvider> analogOutput() {
        return providers.analogOutput();
    }

    /** {@inheritDoc} */
    @Override
    public ProviderGroup<DigitalInputProvider> digitalInput() {
        return providers.digitalInput();
    }

    /** {@inheritDoc} */
    @Override
    public ProviderGroup<DigitalOutputProvider> digitalOutput() {
        return providers.digitalOutput();
    }

    /** {@inheritDoc} */
    @Override
    public ProviderGroup<PwmProvider> pwm() {
        return providers.pwm();
    }

    /** {@inheritDoc} */
    @Override
    public ProviderGroup<SpiProvider> spi() {
        return providers.spi();
    }

    /** {@inheritDoc} */
    @Override
    public ProviderGroup<I2CProvider> i2c() {
        return providers.i2c();
    }

    /** {@inheritDoc} */
    @Override
    public ProviderGroup<SerialProvider> serial() {
        return providers.serial();
    }

    /** {@inheritDoc} */
    @Override
    public ProviderGroup<OneWireProvider> oneWire() {
        return providers.oneWire();
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, Provider> all() {
        return providers.all();
    }

    /** {@inheritDoc} */
    @Override
    public <T extends Provider> Map<String, T> all(Class<T> providerClass) throws ProviderNotFoundException {
        return providers.all(providerClass);
    }

    /** {@inheritDoc} */
    @Override
    public <T extends Provider> Map<String, T> all(IOType ioType) throws ProviderNotFoundException {
        return providers.all(ioType);
    }

    /** {@inheritDoc} */
    @Override
    public boolean exists(String providerId) {
        return providers.exists(providerId);
    }

    /** {@inheritDoc} */
    @Override
    public <T extends Provider> T get(String providerId) throws ProviderNotFoundException {
        return providers.get(providerId);
    }
}

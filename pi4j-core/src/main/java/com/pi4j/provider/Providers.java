package com.pi4j.provider;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  Providers.java
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

import java.util.Map;

import com.pi4j.common.Describable;
import com.pi4j.common.Descriptor;
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
import com.pi4j.provider.exception.ProviderException;
import com.pi4j.provider.exception.ProviderIOTypeException;
import com.pi4j.provider.exception.ProviderNotFoundException;
import com.pi4j.provider.exception.ProviderTypeException;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * This class provides static methods to configure the Pi4J library's default
 * platform.  Pi4J supports the following platforms:  RaspberryPi, BananaPi, BananaPro, Odroid.
 * </p>
 *
 * @see <a href="http://www.pi4j.com/">http://www.pi4j.com/</a>
 * @author Robert Savage (<a
 *         href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public interface Providers extends Describable {

    /**
     * <p>analogInput.</p>
     *
     * @return a {@link com.pi4j.provider.ProviderGroup} object.
     */
    ProviderGroup<AnalogInputProvider> analogInput();
    /**
     * <p>analogOutput.</p>
     *
     * @return a {@link com.pi4j.provider.ProviderGroup} object.
     */
    ProviderGroup<AnalogOutputProvider> analogOutput();
    /**
     * <p>digitalInput.</p>
     *
     * @return a {@link com.pi4j.provider.ProviderGroup} object.
     */
    ProviderGroup<DigitalInputProvider> digitalInput();
    /**
     * <p>digitalOutput.</p>
     *
     * @return a {@link com.pi4j.provider.ProviderGroup} object.
     */
    ProviderGroup<DigitalOutputProvider> digitalOutput();
    /**
     * <p>pwm.</p>
     *
     * @return a {@link com.pi4j.provider.ProviderGroup} object.
     */
    ProviderGroup<PwmProvider> pwm();
    /**
     * <p>spi.</p>
     *
     * @return a {@link com.pi4j.provider.ProviderGroup} object.
     */
    ProviderGroup<SpiProvider> spi();
    /**
     * <p>i2c.</p>
     *
     * @return a {@link com.pi4j.provider.ProviderGroup} object.
     */
    ProviderGroup<I2CProvider> i2c();
    /**
     * <p>serial.</p>
     *
     * @return a {@link com.pi4j.provider.ProviderGroup} object.
     */
    ProviderGroup<SerialProvider> serial();
    /**
     * <p>1-Wire.</p>
     *
     * @return a {@link com.pi4j.provider.ProviderGroup} object.
     */
    ProviderGroup<OneWireProvider> oneWire();

    /**
     * Get all providers
     *
     * @return a {@link java.util.Map} object.
     */
    Map<String, Provider> all();

    /**
     * Get all providers of a specified io class/interface.
     *
     * @param providerClass a {@link java.lang.Class} object.
     * @param <T> providers extending the {@link com.pi4j.provider.Provider} interface
     * @return a {@link java.util.Map} object.
     * @throws com.pi4j.provider.exception.ProviderNotFoundException if any.
     */
    <T extends Provider> Map<String, T> all(Class<T> providerClass) throws ProviderNotFoundException;

    /**
     * Get all providers of a specified io type.
     *
     * @param ioType a {@link com.pi4j.io.IOType} object.
     * @param <T> providers extending the {@link com.pi4j.provider.Provider} interface
     * @return a {@link java.util.Map} object.
     * @throws com.pi4j.provider.exception.ProviderNotFoundException if any.
     */
    <T extends Provider> Map<String, T> all(IOType ioType) throws ProviderNotFoundException;

    /**
     * <p>exists.</p>
     *
     * @param providerId a {@link java.lang.String} object.
     * @return a boolean.
     */
    boolean exists(String providerId);

    /**
     * <p>exists.</p>
     *
     * @param providerId a {@link java.lang.String} object.
     * @param providerClass a {@link java.lang.Class} object.
     * @param <T> a T object.
     * @return a boolean.
     */
    default <T extends Provider> boolean exists(String providerId, Class<T> providerClass) {
        // determine if the requested provider exists by ID and PROVIDER CLASS/TYPE
        try {
            return get(providerId, providerClass) != null;
        } catch (ProviderException e) {
            return false;
        }
    }

    /**
     * <p>exists.</p>
     *
     * @param providerId a {@link java.lang.String} object.
     * @param ioType a {@link com.pi4j.io.IOType} object.
     * @param <T> a T object.
     * @return a boolean.
     */
    default <T extends Provider> boolean exists(String providerId, IOType ioType) {
        // determine if the requested provider exists by ID and IO TYPE
        try {
            return get(providerId, ioType) != null;
        } catch (ProviderException e) {
            return false;
        }
    }

    /**
     * <p>exists.</p>
     *
     * @param ioType a {@link com.pi4j.io.IOType} object.
     * @param <T> a T object.
     * @return a boolean.
     */
    default <T extends Provider> boolean exists(IOType ioType){
        // return the provider instance from the managed provider map that contains the given provider-class
        try {
            return !(all(ioType).isEmpty());
        }
        catch (ProviderNotFoundException e){
            return false;
        }
    }

    /**
     * <p>exists.</p>
     *
     * @param providerClass a {@link java.lang.Class} object.
     * @param <T> a T object.
     * @return a boolean.
     */
    default <T extends Provider> boolean exists(Class<T> providerClass) {
        // return the provider instance from the managed provider map that contains the given provider-class
        try {
            return !(all(providerClass).isEmpty());
        }
        catch (ProviderNotFoundException e){
            return false;
        }
    }

    /**
     * <p>get.</p>
     *
     * @param providerId a {@link java.lang.String} object.
     * @param <T> a T object.
     * @return a T object.
     * @throws com.pi4j.provider.exception.ProviderNotFoundException if any.
     */
    <T extends Provider> T get(String providerId) throws ProviderNotFoundException;

    /**
     * <p>get.</p>
     *
     * @param providerId a {@link java.lang.String} object.
     * @param providerClass a {@link java.lang.Class} object.
     * @param <T> a T object.
     * @return a T object.
     * @throws com.pi4j.provider.exception.ProviderNotFoundException if any.
     * @throws com.pi4j.provider.exception.ProviderTypeException if any.
     */
    default <T extends Provider> T get(String providerId, Class<T> providerClass) throws ProviderNotFoundException, ProviderTypeException {
        // object the IO instance by unique instance identifier and validate the IO instance class/interface
        var provider = get(providerId);
        if(providerClass.isAssignableFrom(provider.getClass())){
            return (T)provider;
        }
        throw new ProviderTypeException(provider, providerClass);
    }

    /**
     * <p>get.</p>
     *
     * @param providerId a {@link java.lang.String} object.
     * @param ioType a {@link com.pi4j.io.IOType} object.
     * @param <T> a T object.
     * @return a T object.
     * @throws com.pi4j.provider.exception.ProviderNotFoundException if any.
     * @throws com.pi4j.provider.exception.ProviderIOTypeException if any.
     */
    default <T extends Provider> T get(String providerId, IOType ioType) throws ProviderNotFoundException, ProviderIOTypeException {
        // object the IO instance by unique instance identifier and validate the IO instance IO type
        var provider = get(providerId);
        if(provider.getType().isType(ioType)){
            return (T)provider;
        }
        throw new ProviderIOTypeException(provider, ioType);
    }

    /**
     * <p>get.</p>
     *
     * @param providerClass a {@link java.lang.Class} object.
     * @param <T> a T object.
     * @return a T object.
     * @throws com.pi4j.provider.exception.ProviderNotFoundException if any.
     */
    default <T extends Provider> T get(Class<T> providerClass) throws ProviderNotFoundException {
        // return the provider instance from the managed provider map that contains the given provider-class
        var subset = all(providerClass);
        if(subset.isEmpty()){
            throw new ProviderNotFoundException(providerClass);
        }
        // return first instance found
        return (T)subset.values().iterator().next();
    }

    /**
     * <p>get.</p>
     *
     * @param ioType a {@link com.pi4j.io.IOType} object.
     * @param <T> a T object.
     * @return a T object.
     * @throws com.pi4j.provider.exception.ProviderNotFoundException if any.
     */
    default <T extends Provider> T get(IOType ioType) throws ProviderNotFoundException {
        // return the provider instance from the managed provider map that contains the given provider-class
        var subset = all(ioType);
        if(subset.isEmpty()){
            throw new ProviderNotFoundException(ioType);
        }
        // return first instance found
        return (T)subset.values().iterator().next();
    }


    // DEFAULT METHODS
    /**
     * <p>getAnalogInput.</p>
     *
     * @return a {@link com.pi4j.provider.ProviderGroup} object.
     */
    default ProviderGroup<AnalogInputProvider> getAnalogInputProviders() { return analogInput(); }
    /**
     * <p>getAnalogOutput.</p>
     *
     * @return a {@link com.pi4j.provider.ProviderGroup} object.
     */
    default ProviderGroup<AnalogOutputProvider> getAnalogOutputProviders() { return analogOutput(); }
    /**
     * <p>getDigitalInput.</p>
     *
     * @return a {@link com.pi4j.provider.ProviderGroup} object.
     */
    default ProviderGroup<DigitalInputProvider> getDigitalInputProviders() { return digitalInput(); }
    /**
     * <p>getDigitalOutput.</p>
     *
     * @return a {@link com.pi4j.provider.ProviderGroup} object.
     */
    default ProviderGroup<DigitalOutputProvider> getDigitalOutputProviders() { return digitalOutput(); }
    /**
     * <p>getPwm.</p>
     *
     * @return a {@link com.pi4j.provider.ProviderGroup} object.
     */
    default ProviderGroup<PwmProvider> getPwmProviders() { return pwm(); }
    /**
     * <p>getSpi.</p>
     *
     * @return a {@link com.pi4j.provider.ProviderGroup} object.
     */
    default ProviderGroup<SpiProvider> getSpiProviders() { return spi(); }
    /**
     * <p>getI2C.</p>
     *
     * @return a {@link com.pi4j.provider.ProviderGroup} object.
     */
    default ProviderGroup<I2CProvider> getI2CProviders() { return i2c(); }
    /**
     * <p>getSerial.</p>
     *
     * @return a {@link com.pi4j.provider.ProviderGroup} object.
     */
    default ProviderGroup<SerialProvider> getSerialProviders() { return serial(); }
    /**
     * <p>getAll.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    default Map<String, Provider> getAll() { return all(); }
    /**
     * <p>getAll.</p>
     *
     * @param providerClass a {@link java.lang.Class} object.
     * @param <T> a T object.
     * @return a {@link java.util.Map} object.
     * @throws com.pi4j.provider.exception.ProviderNotFoundException if any.
     */
    default <T extends Provider> Map<String, T> getAll(Class<T> providerClass) throws ProviderNotFoundException {
        return all(providerClass);
    }
    /**
     * <p>getAll.</p>
     *
     * @param ioType a {@link com.pi4j.io.IOType} object.
     * @param <T> a T object.
     * @return a {@link java.util.Map} object.
     * @throws com.pi4j.provider.exception.ProviderNotFoundException if any.
     */
    default <T extends Provider> Map<String, T> getAll(IOType ioType) throws ProviderNotFoundException {
        return all(ioType);
    }

    /**
     * <p>describe.</p>
     *
     * @return a {@link com.pi4j.common.Descriptor} object.
     */
    default Descriptor describe() {
        var providers = all();

        Descriptor descriptor = Descriptor.create()
                .category("PROVIDERS")
                .name("I/O Providers")
                .quantity((providers == null) ? 0 : providers.size())
                .type(this.getClass());

        for(IOType ioType : IOType.values()){

            try {
                Map<String, Provider> providersByType = getAll(ioType);
                Descriptor ioTypeDescriptor = Descriptor.create()
                        .category(ioType.name())
                        .quantity((providers == null) ? 0 : providersByType.size())
                        .type(ioType.getProviderClass());

                if(providersByType != null && !providersByType.isEmpty()) {
                    providersByType.forEach((id, provider) -> {
                        ioTypeDescriptor.add(provider.describe());
                    });
                }
                descriptor.add(ioTypeDescriptor);

            } catch (ProviderNotFoundException e) {
                LoggerFactory.getLogger(this.getClass()).error(e.getMessage(), e);
            }
        }

        return descriptor;
    }

}

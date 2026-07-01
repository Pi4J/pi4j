package com.pi4j.provider;

import com.pi4j.common.Describable;
import com.pi4j.common.Descriptor;
import com.pi4j.io.IOType;
import com.pi4j.io.gpio.digital.DigitalInputProvider;
import com.pi4j.io.gpio.digital.DigitalOutputProvider;
import com.pi4j.io.i2c.I2CProvider;
import com.pi4j.io.pwm.PwmProvider;
import com.pi4j.io.spi.SpiProvider;
import com.pi4j.provider.exception.ProviderException;
import com.pi4j.provider.exception.ProviderIOTypeException;
import com.pi4j.provider.exception.ProviderNotFoundException;
import com.pi4j.provider.exception.ProviderTypeException;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Registry of all {@link Provider} instances known to a Pi4J {@link com.pi4j.context.Context}.
 * It provides lookup of providers by id, by provider class, and by {@link IOType}, together with
 * type-scoped convenience views via {@link ProviderGroup} for each supported I/O category
 * (digital input/output, PWM, SPI and I2C).
 *
 * @see <a href="http://www.pi4j.com/">http://www.pi4j.com/</a>
 */
public interface Providers extends Describable {

    /**
     * Returns the group of registered digital-input providers.
     *
     * @return a type-scoped view of the {@link DigitalInputProvider} instances
     */
    ProviderGroup<DigitalInputProvider> digitalInput();

    /**
     * Returns the group of registered digital-output providers.
     *
     * @return a type-scoped view of the {@link DigitalOutputProvider} instances
     */
    ProviderGroup<DigitalOutputProvider> digitalOutput();

    /**
     * Returns the group of registered PWM providers.
     *
     * @return a type-scoped view of the {@link PwmProvider} instances
     */
    ProviderGroup<PwmProvider> pwm();

    /**
     * Returns the group of registered SPI providers.
     *
     * @return a type-scoped view of the {@link SpiProvider} instances
     */
    ProviderGroup<SpiProvider> spi();

    /**
     * Returns the group of registered I2C providers.
     *
     * @return a type-scoped view of the {@link I2CProvider} instances
     */
    ProviderGroup<I2CProvider> i2c();

    /**
     * Returns every registered provider keyed by its unique id.
     *
     * @return a map of provider id to provider instance
     */
    Map<String, Provider> all();

    /**
     * Returns every registered provider assignable to the given provider class, keyed by id.
     *
     * @param <T>           the {@link Provider} subtype to match
     * @param providerClass the provider class or interface to filter by
     * @return a map of provider id to matching provider instance
     * @throws ProviderNotFoundException if no provider matching the class is registered
     */
    <T extends Provider> Map<String, T> all(Class<T> providerClass) throws ProviderNotFoundException;

    /**
     * Returns every registered provider of the given I/O type, keyed by id.
     *
     * @param <T>    the {@link Provider} subtype to match
     * @param ioType the I/O type to filter by
     * @return a map of provider id to matching provider instance
     * @throws ProviderNotFoundException if no provider of the given I/O type is registered
     */
    <T extends Provider> Map<String, T> all(IOType ioType) throws ProviderNotFoundException;

    /**
     * Indicates whether a provider with the given id is registered.
     *
     * @param providerId the unique identifier of the provider to test
     * @return {@code true} if a provider with the given id exists
     */
    boolean exists(String providerId);

    /**
     * Indicates whether a provider with the given id exists and is assignable to the given class.
     *
     * @param <T>           the {@link Provider} subtype to match
     * @param providerId    the unique identifier of the provider to test
     * @param providerClass the provider class or interface the provider must match
     * @return {@code true} if a matching provider exists
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
     * Indicates whether a provider with the given id exists and is of the given I/O type.
     *
     * @param <T>        the {@link Provider} subtype to match
     * @param providerId the unique identifier of the provider to test
     * @param ioType     the I/O type the provider must match
     * @return {@code true} if a matching provider exists
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
     * Indicates whether at least one provider of the given I/O type is registered.
     *
     * @param <T>    the {@link Provider} subtype to match
     * @param ioType the I/O type to test for
     * @return {@code true} if any provider of the given I/O type exists
     */
    default <T extends Provider> boolean exists(IOType ioType) {
        // return the provider instance from the managed provider map that contains the given provider-class
        try {
            return !(all(ioType).isEmpty());
        } catch (ProviderNotFoundException e) {
            return false;
        }
    }

    /**
     * Indicates whether at least one provider assignable to the given class is registered.
     *
     * @param <T>           the {@link Provider} subtype to match
     * @param providerClass the provider class or interface to test for
     * @return {@code true} if any provider matching the class exists
     */
    default <T extends Provider> boolean exists(Class<T> providerClass) {
        // return the provider instance from the managed provider map that contains the given provider-class
        try {
            return !(all(providerClass).isEmpty());
        } catch (ProviderNotFoundException e) {
            return false;
        }
    }

    /**
     * Returns the registered provider with the given id.
     *
     * @param <T>        the expected {@link Provider} subtype
     * @param providerId the unique identifier of the provider to retrieve
     * @return the matching provider
     * @throws ProviderNotFoundException if no provider with the given id is registered
     */
    <T extends Provider> T get(String providerId) throws ProviderNotFoundException;

    /**
     * Returns the registered provider with the given id, verifying it is assignable to the given class.
     *
     * @param <T>           the expected {@link Provider} subtype
     * @param providerId    the unique identifier of the provider to retrieve
     * @param providerClass the provider class or interface the result must match
     * @return the matching provider, cast to the requested type
     * @throws ProviderNotFoundException if no provider with the given id is registered
     * @throws ProviderTypeException     if the provider is not assignable to the given class
     */
    default <T extends Provider> T get(String providerId, Class<T> providerClass) throws ProviderNotFoundException, ProviderTypeException {
        // object the IO instance by unique instance identifier and validate the IO instance class/interface
        var provider = get(providerId);
        if (providerClass.isAssignableFrom(provider.getClass())) {
            return (T) provider;
        }
        throw new ProviderTypeException(provider, providerClass);
    }

    /**
     * Returns the registered provider with the given id, verifying it is of the given I/O type.
     *
     * @param <T>        the expected {@link Provider} subtype
     * @param providerId the unique identifier of the provider to retrieve
     * @param ioType     the I/O type the result must match
     * @return the matching provider, cast to the requested type
     * @throws ProviderNotFoundException if no provider with the given id is registered
     * @throws ProviderIOTypeException   if the provider is not of the given I/O type
     */
    default <T extends Provider> T get(String providerId, IOType ioType) throws ProviderNotFoundException, ProviderIOTypeException {
        // object the IO instance by unique instance identifier and validate the IO instance IO type
        var provider = get(providerId);
        if (provider.getType().isType(ioType)) {
            return (T) provider;
        }
        throw new ProviderIOTypeException(provider, ioType);
    }

    /**
     * Returns the first registered provider assignable to the given class. Useful for resolving a
     * default provider for a category when the exact id is not known.
     *
     * @param <T>           the expected {@link Provider} subtype
     * @param providerClass the provider class or interface to match
     * @return the first matching provider found
     * @throws ProviderNotFoundException if no provider matching the class is registered
     */
    default <T extends Provider> T get(Class<T> providerClass) throws ProviderNotFoundException {
        // return the provider instance from the managed provider map that contains the given provider-class
        var subset = all(providerClass);
        if (subset.isEmpty()) {
            throw new ProviderNotFoundException(providerClass);
        }
        // return first instance found
        return (T) subset.values().iterator().next();
    }

    /**
     * Returns the first registered provider of the given I/O type. Useful for resolving a default
     * provider for an I/O category.
     *
     * @param <T>    the expected {@link Provider} subtype
     * @param ioType the I/O type to match
     * @return the first matching provider found
     * @throws ProviderNotFoundException if no provider of the given I/O type is registered
     */
    default <T extends Provider> T get(IOType ioType) throws ProviderNotFoundException {
        // return the provider instance from the managed provider map that contains the given provider-class
        var subset = all(ioType);
        if (subset.isEmpty()) {
            throw new ProviderNotFoundException(ioType);
        }
        // return first instance found
        return (T) subset.values().iterator().next();
    }


    // DEFAULT METHODS

    /**
     * Returns the group of registered digital-input providers.
     *
     * @return a type-scoped view of the {@link DigitalInputProvider} instances
     * @see #digitalInput()
     */
    default ProviderGroup<DigitalInputProvider> getDigitalInputProviders() {
        return digitalInput();
    }

    /**
     * Returns the group of registered digital-output providers.
     *
     * @return a type-scoped view of the {@link DigitalOutputProvider} instances
     * @see #digitalOutput()
     */
    default ProviderGroup<DigitalOutputProvider> getDigitalOutputProviders() {
        return digitalOutput();
    }

    /**
     * Returns the group of registered PWM providers.
     *
     * @return a type-scoped view of the {@link PwmProvider} instances
     * @see #pwm()
     */
    default ProviderGroup<PwmProvider> getPwmProviders() {
        return pwm();
    }

    /**
     * Returns the group of registered SPI providers.
     *
     * @return a type-scoped view of the {@link SpiProvider} instances
     * @see #spi()
     */
    default ProviderGroup<SpiProvider> getSpiProviders() {
        return spi();
    }

    /**
     * Returns the group of registered I2C providers.
     *
     * @return a type-scoped view of the {@link I2CProvider} instances
     * @see #i2c()
     */
    default ProviderGroup<I2CProvider> getI2CProviders() {
        return i2c();
    }

    /**
     * Returns every registered provider keyed by its unique id.
     *
     * @return a map of provider id to provider instance
     * @see #all()
     */
    default Map<String, Provider> getAll() {
        return all();
    }

    /**
     * Returns every registered provider assignable to the given provider class, keyed by id.
     *
     * @param <T>           the {@link Provider} subtype to match
     * @param providerClass the provider class or interface to filter by
     * @return a map of provider id to matching provider instance
     * @throws ProviderNotFoundException if no provider matching the class is registered
     * @see #all(Class)
     */
    default <T extends Provider> Map<String, T> getAll(Class<T> providerClass) throws ProviderNotFoundException {
        return all(providerClass);
    }

    /**
     * Returns every registered provider of the given I/O type, keyed by id.
     *
     * @param <T>    the {@link Provider} subtype to match
     * @param ioType the I/O type to filter by
     * @return a map of provider id to matching provider instance
     * @throws ProviderNotFoundException if no provider of the given I/O type is registered
     * @see #all(IOType)
     */
    default <T extends Provider> Map<String, T> getAll(IOType ioType) throws ProviderNotFoundException {
        return all(ioType);
    }

    /**
     * {@inheritDoc}
     * <p>
     * The returned descriptor is categorized as {@code "PROVIDERS"} and includes a nested
     * descriptor per {@link IOType} summarizing the providers registered for each type.
     */
    default Descriptor describe() {
        var providers = all();

        Descriptor descriptor = Descriptor.create()
            .category("PROVIDERS")
            .name("I/O Providers")
            .quantity((providers == null) ? 0 : providers.size())
            .type(this.getClass());

        for (IOType ioType : IOType.values()) {

            try {
                Map<String, Provider> providersByType = getAll(ioType);
                Descriptor ioTypeDescriptor = Descriptor.create()
                    .category(ioType.name())
                    .quantity((providers == null) ? 0 : providersByType.size())
                    .type(ioType.getProviderClass());

                if (providersByType != null && !providersByType.isEmpty()) {
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

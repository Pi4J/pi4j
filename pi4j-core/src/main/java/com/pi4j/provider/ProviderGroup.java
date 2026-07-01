package com.pi4j.provider;

import java.util.Map;

import com.pi4j.common.Describable;
import com.pi4j.common.Descriptor;
import com.pi4j.io.IOType;
import com.pi4j.provider.exception.ProviderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A typed, type-scoped view over the {@link Providers} registry restricted to a single
 * {@link IOType}. It is a convenience facade returned by accessors such as
 * {@link Providers#digitalInput()} that lets callers look up providers of one I/O category by id
 * without repeatedly specifying the type.
 *
 * @param <T> the {@link Provider} subtype contained in this group
 */
public class ProviderGroup<T extends Provider> implements Describable {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    private IOType type = null;
    private Providers providers;

    /**
     * Creates a group that exposes the providers of a single I/O type from the given registry.
     *
     * @param providers the backing provider registry this group delegates to
     * @param type      the I/O type this group is restricted to
     */
    public ProviderGroup(Providers providers, IOType type){
        this.providers = providers;
        this.type = type;
    }
    private Map<String, T> all() throws ProviderException {
        return providers.all(type);
    }

    /**
     * Returns the provider with the given id from this group's I/O type.
     *
     * @param providerId the unique identifier of the provider to retrieve
     * @return the matching provider of this group's I/O type
     * @throws ProviderException if no provider with the given id exists or it is not of this group's type
     */
    public T get(String providerId) throws ProviderException {
        return providers.get(providerId, type);
    }

    /**
     * Indicates whether a provider with the given id exists within this group's I/O type.
     *
     * @param providerId the unique identifier of the provider to test
     * @return {@code true} if a matching provider of this group's I/O type exists
     * @throws ProviderException if the existence check fails
     */
    public boolean exists(String providerId) throws ProviderException {
        return providers.exists(providerId, type);
    }

    @Override
    public Descriptor describe() {
        Descriptor descriptor = Descriptor.create()
                .category("PROVIDER GROUP")
                .name("Provider Group")
                .type(this.getClass());
        Map<String, T> all = null;
        try {
            all = all();
            all.forEach((id, provider)->{
                descriptor.add(provider.describe());
            });
        } catch (ProviderException e) {
            logger.error(e.getMessage(), e);
        }
        return descriptor;
    }
}

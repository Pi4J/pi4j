package com.pi4j.io;

import com.pi4j.common.Descriptor;
import com.pi4j.common.Identity;
import com.pi4j.common.IdentityBase;

import java.io.Closeable;
import java.util.function.Consumer;

/**
 * Abstract base implementation of {@link IO} that the concrete I/O types build upon.
 * <p>
 * It stores the originating {@link IOConfig}, initializes its {@link Identity}
 * (id, name, description) from that configuration, and stores an onClose callback so that {@link #close()} reliably
 * unregisters and shuts the instance down exactly once.
 *
 * @param <CONFIG_TYPE>   the {@link IOConfig} type describing this instance
 */
public abstract class IOBase<CONFIG_TYPE extends IOConfig>
        extends IdentityBase implements IO<CONFIG_TYPE>, Closeable {

    protected final CONFIG_TYPE config;

    // Consumer is contravariant, so we can set the concrete type only in concrete subclasses. There, it should be
    // Consumer<? super MySpecificImplementationType>. The type needs to be as "special" as possible, so all super
    // classes including itself can be used in the consuming method.
    @SuppressWarnings("rawtypes")
    protected final Consumer onClose;
    // close() requires idempotency.
    protected boolean closed = false;

    /**
     * Creates a new I/O instance, copying the id, name and description from the supplied
     * configuration into this instance's identity.
     *
     * @param config the configuration defining this instance's identity and properties
     */
    protected IOBase(CONFIG_TYPE config, Consumer<?> onClose){
        super();
        this.id = config.id();
        this.name = config.name();
        this.description = config.description();
        this.config = config;
        this.onClose = onClose;
    }

    /**
     * Closes the driver and calls this.context().shutdown(this), which removes this IO instance from the context
     * registry.
     * <p>
     * Note for subclass implementations: As context.shoutdown(IO) might be called directly for historical reasons,
     * it needs to call this method again. To prevent an infinite cycles, it's important that the idempotency aspect
     * of the close contract is strictly observed.
     * <p>
     */
    @Override
    public void close() {
        // The closed check ensures idempotency, as required by the close method contract.
        if (closed) {
            return;
        }
        closed = true;
        // The null check accounts for tests without an onClose callback
        if (this.onClose != null) {
            this.onClose.accept(this);
        }
    }

    @Override
    public final boolean isOpen() {
        return !closed;
    }

    @Override
    public CONFIG_TYPE config(){
        return this.config;
    }

    @Override
    public Descriptor describe() {
        return super.describe().category("IO");
    }
}

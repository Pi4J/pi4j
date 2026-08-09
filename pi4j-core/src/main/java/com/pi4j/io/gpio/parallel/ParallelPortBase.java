package com.pi4j.io.gpio.parallel;

import com.pi4j.event.EventManager;
import com.pi4j.io.IOBase;
import com.pi4j.io.gpio.MaskUtils;

/**
 * Abstract base class for parallel port implementations.
 * <p>
 * This base class requires abstract methods to be implemented by its inheritors, while the equivalent
 * public implementations are declared final. The intention of this pattern is for the execution order of each
 * to be predictable and deterministic while reducing the possibility of implementing classes to alter core behaviour.
 * <p>
 * An alternative approach could be to declare this class as non-abstract, final, and for the abstract methods
 * to be provided via composition with an `Operations`-like implementation which is able to focus on a simplified
 * set of runtime responsibilities without having to also account for configuration aspects such as
 * {@link com.pi4j.provider.Provider} references and {@link com.pi4j.common.Identity}, whose implementations don't vary
 * significantly between {@link com.pi4j.io.IO} implementations.
 */
public abstract class ParallelPortBase
    extends IOBase<ParallelPort, ParallelPortConfig, ParallelPortProvider>
    implements ParallelPort {

    private final int mask;

    private Direction direction;

    protected final EventManager<ParallelPort, Listener, ValueChangeEvent> events = new EventManager<>(
        this, Listener::onValueChange
    );

    protected ParallelPortBase(ParallelPortProvider provider, ParallelPortConfig config) {
        super(provider, config);
        this.mask = (int) MaskUtils.packed(config.mask());
        this.direction = config.initialDirection();
    }

    @Override
    public final void write(int value) {
        if (this.direction == Direction.INPUT) {
            throw new IllegalStateException("Parallel port is not configured as an output");
        }

        if ((value &~ this.mask) != 0) {
            throw new IllegalArgumentException("Value " + value + " is not valid for mask " + this.mask);
        }

        handleWrite(value);
        fireEventWithValue(value);
    }

    @Override
    public final int read() {
        return handleRead() & this.mask;
    }

    @Override
    public final void setDirection(Direction direction) {
        this.direction = handleSetDirection(direction);
    }

    @Override
    public final Direction getDirection() {
        direction = handleGetDirection();
        return direction;
    }

    @Override
    public ParallelPort addListener(Listener listener) {
        return events.add(listener);
    }

    @Override
    public ParallelPort removeListener(Listener listener) {
        return events.remove(listener);
    }

    public void fireEventWithValue(int value) {
        events.dispatch(new ValueChangeEvent(this, value));
    }

    /**
     * Implementation-specific write handler to perform the actual write operation.
     * <p>
     * Requiring this method to be implemented rather than the {@link #write(int)} allows the
     * execution order to be deterministic, as {@link #write(int)} is responsible for orchestration and
     * the implementing class is not required to make calls to `super`
     *
     * @param value the value to write
     */
    protected abstract void handleWrite(int value);

    /**
     * Implementation-specific read handler to perform the actual read operation.
     * <p>
     * Requiring this method to be implemented rather than the {@link #read()} allows the
     * execution order to be deterministic, as {@link #read()} is responsible for orchestration and
     * the implementing class is not required to make calls to `super`
     *
     * @return the value read
     */
    protected abstract int handleRead();

    /**
     * Implementation-specific set direction handler to perform the actual set direction operation.
     * <p>
     * This method can be overridden by subclasses to provide custom behavior when setting the direction.
     * <p>
     * Requiring this method to be implemented rather than the {@link #setDirection(Direction)} allows the
     * execution order to be deterministic, as {@link #setDirection(Direction)} is responsible for orchestration and
     * the implementing class is not required to make calls to `super`
     *
     * @param direction the direction to set
     * @return the direction which will be set
     */
    protected Direction handleSetDirection(Direction direction) {
        return direction;
    }
    /**
     * Implementation-specific get direction handler to perform the actual get direction operation.
     * <p>
     * This method can be overridden by subclasses to provide custom behaviour when getting the direction.
     * <p>
     * Requiring this method to be implemented rather than the {@link #getDirection()} allows the
     * execution order to be deterministic, as {@link #getDirection()} is responsible for orchestration and
     * the implementing class is not required to make calls to `super`
     *
     * @return the current direction
     */
    protected ParallelPort.Direction handleGetDirection() {
        return direction;
    }
}

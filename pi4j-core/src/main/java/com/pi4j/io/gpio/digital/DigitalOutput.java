package com.pi4j.io.gpio.digital;

import com.pi4j.context.Context;
import com.pi4j.io.OnOff;
import com.pi4j.io.Output;
import com.pi4j.io.exception.IOException;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Represents a digital output I/O instance, typically a single GPIO pin driven {@link DigitalState#HIGH} or
 * {@link DigitalState#LOW}. In addition to the basic {@code state(...)} operations it offers higher-level
 * convenience operations such as {@link #toggle()}, timed {@link #pulse(int, TimeUnit)} and repeating
 * {@link #blink(int, TimeUnit)}, each with blocking and asynchronous variants. Instances are obtained from a
 * {@link DigitalOutputProvider} and configured via {@link DigitalOutputConfig}.
 */
public interface DigitalOutput extends Digital<DigitalOutput, DigitalOutputConfig, DigitalOutputProvider>,
        Output,
        OnOff<DigitalOutput> {

    /**
     * Creates a new {@link DigitalOutputConfigBuilder} for assembling a {@link DigitalOutputConfig}.
     *
     * @param context the Pi4J runtime context the configuration will be associated with
     * @return a new configuration builder instance
     * @deprecated the context argument is no longer required; use {@link #newConfigBuilder()} instead
     */
    @Deprecated
    static DigitalOutputConfigBuilder newConfigBuilder(Context context){
        return DigitalOutputConfigBuilder.newInstance(context);
    }

    /**
     * Creates a new {@link DigitalOutputConfigBuilder} for assembling a {@link DigitalOutputConfig}.
     *
     * @return a new configuration builder instance
     */
    static DigitalOutputConfigBuilder newConfigBuilder(){
        return DigitalOutputConfigBuilder.newInstance();
    }

    /**
     * Creates a new {@link DigitalOutputBuilder} that resolves a provider and builds a ready-to-use
     * {@link DigitalOutput} instance.
     *
     * @param context the Pi4J runtime context used to resolve the provider and register the instance
     * @return a new digital output builder instance
     */
    static DigitalOutputBuilder newBuilder(Context context){
        return DigitalOutputBuilder.newInstance(context);
    }

    /**
     * Sets the output to the given digital state, driving the underlying pin accordingly.
     *
     * @param state the digital state to drive the output to
     * @return this instance for method chaining
     * @throws IOException if the underlying hardware cannot be written
     */
    DigitalOutput state(DigitalState state) throws IOException;
    /**
     * Drives the output to the given state for the specified duration, then drives it to the inverse state,
     * blocking the calling thread for the duration and finally invoking the optional callback.
     *
     * @param interval the pulse duration; must be greater than zero
     * @param unit     the time unit of {@code interval}
     * @param state    the state to hold during the pulse
     * @param callback an optional task invoked once the pulse completes, or {@code null} for none
     * @return this instance for method chaining
     * @throws IOException if the underlying hardware cannot be written
     */
    DigitalOutput pulse(int interval, TimeUnit unit, DigitalState state, Callable<Void> callback) throws IOException;
    /**
     * Performs the same operation as {@link #pulse(int, TimeUnit, DigitalState, Callable)} but on a background
     * thread without blocking the caller.
     *
     * @param interval the pulse duration; must be greater than zero
     * @param unit     the time unit of {@code interval}
     * @param state    the state to hold during the pulse
     * @param callback an optional task invoked once the pulse completes, or {@code null} for none
     * @return a {@link Future} that can be used to cancel or await completion of the pulse
     */
    Future<?> pulseAsync(int interval, TimeUnit unit, DigitalState state, Callable<Void> callback);
    /**
     * Drives the output to the given initial state and then toggles it repeatedly, producing a square wave,
     * blocking the calling thread until the requested number of toggles completes.
     *
     * @param delay    the time each state is held before toggling
     * @param duration the number of on/off cycles to perform
     * @param unit     the time unit of {@code delay}
     * @param state    the initial state before toggling begins
     * @param callback an optional task invoked once the blinking completes, or {@code null} for none
     * @return this instance for method chaining
     */
    DigitalOutput blink(int delay, int duration, TimeUnit unit, DigitalState state, Callable<Void> callback);
    /**
     * Performs the same operation as {@link #blink(int, int, TimeUnit, DigitalState, Callable)} but on a
     * background thread without blocking the caller.
     *
     * @param delay    the time each state is held before toggling
     * @param duration the number of on/off cycles to perform
     * @param unit     the time unit of {@code delay}
     * @param state    the initial state before toggling begins
     * @param callback an optional task invoked once the blinking completes, or {@code null} for none
     * @return a {@link Future} that can be used to cancel or await completion of the blink
     */
    Future<?> blinkAsync(int delay, int duration, TimeUnit unit, DigitalState state, Callable<Void> callback);

    /**
     * Sets the output state from a numeric value, mapping it to a {@link DigitalState} via
     * {@link DigitalState#getState(Number)}.
     *
     * @param state {@code 0} for {@link DigitalState#LOW}, otherwise {@link DigitalState#HIGH}
     * @return this instance for method chaining
     * @throws IOException if the underlying hardware cannot be written
     */
    default DigitalOutput setState(byte state) throws IOException {
        return this.state(DigitalState.getState(state));
    }
    /**
     * Sets the output state from a numeric value, mapping it to a {@link DigitalState} via
     * {@link DigitalState#getState(Number)}.
     *
     * @param state {@code 0} for {@link DigitalState#LOW}, otherwise {@link DigitalState#HIGH}
     * @return this instance for method chaining
     * @throws IOException if the underlying hardware cannot be written
     */
    default DigitalOutput setState(short state) throws IOException {
        return this.state(DigitalState.getState(state));
    }
    /**
     * Sets the output state from a numeric value, mapping it to a {@link DigitalState} via
     * {@link DigitalState#getState(Number)}.
     *
     * @param state {@code 0} for {@link DigitalState#LOW}, otherwise {@link DigitalState#HIGH}
     * @return this instance for method chaining
     * @throws IOException if the underlying hardware cannot be written
     */
    default DigitalOutput setState(int state) throws IOException {
        return this.state(DigitalState.getState(state));
    }
    /**
     * Sets the output state from a numeric value, mapping it to a {@link DigitalState} via
     * {@link DigitalState#getState(Number)}.
     *
     * @param state {@code 0} for {@link DigitalState#LOW}, otherwise {@link DigitalState#HIGH}
     * @return this instance for method chaining
     * @throws IOException if the underlying hardware cannot be written
     */
    default DigitalOutput setState(long state) throws IOException {
        return this.state(DigitalState.getState(state));
    }
    /**
     * Sets the output state from a numeric value, mapping it to a {@link DigitalState} via
     * {@link DigitalState#getState(Number)}.
     *
     * @param state {@code 0} for {@link DigitalState#LOW}, otherwise {@link DigitalState#HIGH}
     * @return this instance for method chaining
     * @throws IOException if the underlying hardware cannot be written
     */
    default DigitalOutput setState(float state) throws IOException {
        return this.state(DigitalState.getState(state));
    }
    /**
     * Sets the output state from a numeric value, mapping it to a {@link DigitalState} via
     * {@link DigitalState#getState(Number)}.
     *
     * @param state {@code 0} for {@link DigitalState#LOW}, otherwise {@link DigitalState#HIGH}
     * @return this instance for method chaining
     * @throws IOException if the underlying hardware cannot be written
     */
    default DigitalOutput setState(double state) throws IOException {
        return this.state(DigitalState.getState(state));
    }
    /**
     * Drives the output to {@link DigitalState#HIGH}.
     *
     * @return this instance for method chaining
     * @throws IOException if the underlying hardware cannot be written
     */
    default DigitalOutput high() throws IOException {
        return this.state(DigitalState.HIGH);
    }
    /**
     * Drives the output to {@link DigitalState#LOW}.
     *
     * @return this instance for method chaining
     * @throws IOException if the underlying hardware cannot be written
     */
    default DigitalOutput low() throws IOException {
        return this.state(DigitalState.LOW);
    }
    /**
     * Inverts the current output state, switching {@link DigitalState#HIGH} to {@link DigitalState#LOW} and
     * vice versa.
     *
     * @return this instance for method chaining
     * @throws IOException if the underlying hardware cannot be written
     */
    default DigitalOutput toggle() throws IOException {
        return this.state(DigitalState.getInverseState(this.state()));
    }

    /**
     * Pulses the output to {@link DigitalState#HIGH} for the given duration, then returns it to
     * {@link DigitalState#LOW}.
     *
     * @param interval the pulse duration; must be greater than zero
     * @param unit     the time unit of {@code interval}
     * @return this instance for method chaining
     * @throws IOException if the underlying hardware cannot be written
     */
    default DigitalOutput pulseHigh(int interval, TimeUnit unit) throws IOException {
        return pulse(interval, unit, DigitalState.HIGH);
    }
    /**
     * Pulses the output to {@link DigitalState#LOW} for the given duration, then returns it to
     * {@link DigitalState#HIGH}.
     *
     * @param interval the pulse duration; must be greater than zero
     * @param unit     the time unit of {@code interval}
     * @return this instance for method chaining
     * @throws com.pi4j.io.exception.IOException if the underlying hardware cannot be written
     */
    default DigitalOutput pulseLow(int interval, TimeUnit unit) throws IOException {
        return pulse(interval, unit, DigitalState.LOW);
    }

    /**
     * Asynchronously pulses the output to {@link DigitalState#HIGH} for the given duration.
     *
     * @param interval the pulse duration; must be greater than zero
     * @param unit     the time unit of {@code interval}
     * @param callback an optional task invoked once the pulse completes, or {@code null} for none
     * @return a {@link Future} that can be used to cancel or await completion of the pulse
     */
    default Future<?> pulseHighAsync(int interval, TimeUnit unit, Callable<Void> callback){
        return pulseAsync(interval, unit, DigitalState.HIGH, callback);
    }

    /**
     * Asynchronously pulses the output to {@link DigitalState#LOW} for the given duration.
     *
     * @param interval the pulse duration; must be greater than zero
     * @param unit     the time unit of {@code interval}
     * @param callback an optional task invoked once the pulse completes, or {@code null} for none
     * @return a {@link Future} that can be used to cancel or await completion of the pulse
     */
    default Future<?> pulseLowAsync(int interval, TimeUnit unit, Callable<Void> callback){
        return pulseAsync(interval, unit, DigitalState.LOW, callback);
    }

    /**
     * Pulses the output to {@link DigitalState#HIGH} for the given duration, then returns it to
     * {@link DigitalState#LOW}.
     *
     * @param interval the pulse duration; must be greater than zero
     * @param unit     the time unit of {@code interval}
     * @return this instance for method chaining
     * @throws com.pi4j.io.exception.IOException if the underlying hardware cannot be written
     */
    default DigitalOutput pulse(int interval, TimeUnit unit) throws IOException {
        return pulse(interval, unit, DigitalState.HIGH);
    }
    /**
     * Pulses the output to the given state for the given duration, then returns it to the inverse state.
     *
     * @param interval the pulse duration; must be greater than zero
     * @param unit     the time unit of {@code interval}
     * @param state    the state to hold during the pulse
     * @return this instance for method chaining
     * @throws com.pi4j.io.exception.IOException if the underlying hardware cannot be written
     */
    default DigitalOutput pulse(int interval, TimeUnit unit, DigitalState state) throws IOException {
        return pulse(interval, unit, state, null);
    }

    /**
     * Asynchronously pulses the output to {@link DigitalState#HIGH} for the given duration.
     *
     * @param interval the pulse duration; must be greater than zero
     * @param unit     the time unit of {@code interval}
     * @return a {@link Future} that can be used to cancel or await completion of the pulse
     */
    default Future<?> pulseAsync(int interval, TimeUnit unit){
        return pulseAsync(interval, unit, DigitalState.HIGH);
    }
    /**
     * Asynchronously pulses the output to the given state for the given duration.
     *
     * @param interval the pulse duration; must be greater than zero
     * @param unit     the time unit of {@code interval}
     * @param state    the state to hold during the pulse
     * @return a {@link Future} that can be used to cancel or await completion of the pulse
     */
    default Future<?> pulseAsync(int interval, TimeUnit unit, DigitalState state){
        return pulseAsync(interval, unit, state, null);
    }

    /**
     * Blinks the output starting {@link DigitalState#HIGH}, using the same value as both the on and off delay
     * and the number of cycles.
     *
     * @param interval the time each state is held before toggling, also used as the number of cycles
     * @param unit     the time unit of {@code interval}
     * @return this instance for method chaining
     */
    default DigitalOutput blink(int interval, TimeUnit unit){
        return this.blink(interval, interval, unit);
    }
    /**
     * Blinks the output starting {@link DigitalState#HIGH} for the given number of cycles.
     *
     * @param delay    the time each state is held before toggling
     * @param duration the number of on/off cycles to perform
     * @param unit     the time unit of {@code delay}
     * @return this instance for method chaining
     */
    default DigitalOutput blink(int delay, int duration, TimeUnit unit){
        return this.blink(delay, duration, unit, DigitalState.HIGH);
    }
    /**
     * Blinks the output starting from the given state for the given number of cycles.
     *
     * @param delay    the time each state is held before toggling
     * @param duration the number of on/off cycles to perform
     * @param unit     the time unit of {@code delay}
     * @param state    the initial state before toggling begins
     * @return this instance for method chaining
     */
    default DigitalOutput blink(int delay, int duration, TimeUnit unit, DigitalState state){
        return this.blink(delay, duration, unit, state, null);
    }

    /**
     * Asynchronously blinks the output starting {@link DigitalState#HIGH}, using the same value as both the
     * delay and the number of cycles.
     *
     * @param interval the time each state is held before toggling, also used as the number of cycles
     * @param unit     the time unit of {@code interval}
     * @return a {@link Future} that can be used to cancel or await completion of the blink
     */
    default Future<?> blinkAsync(int interval, TimeUnit unit){
        return this.blinkAsync(interval, interval, unit, DigitalState.HIGH);
    }
    /**
     * Asynchronously blinks the output starting {@link DigitalState#HIGH} for the given number of cycles.
     *
     * @param delay    the time each state is held before toggling
     * @param duration the number of on/off cycles to perform
     * @param unit     the time unit of {@code delay}
     * @return a {@link Future} that can be used to cancel or await completion of the blink
     */
    default Future<?> blinkAsync(int delay, int duration, TimeUnit unit){
        return this.blinkAsync(delay, duration, unit, DigitalState.HIGH);
    }
    /**
     * Asynchronously blinks the output starting from the given state for the given number of cycles.
     *
     * @param delay    the time each state is held before toggling
     * @param duration the number of on/off cycles to perform
     * @param unit     the time unit of {@code delay}
     * @param state    the initial state before toggling begins
     * @return a {@link Future} that can be used to cancel or await completion of the blink
     */
    default Future<?> blinkAsync(int delay, int duration, TimeUnit unit, DigitalState state){
        return this.blinkAsync(delay, duration, unit, state, null);
    }
}

package com.pi4j.io.gpio.digital;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  DigitalOutput.java
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
import com.pi4j.io.OnOff;
import com.pi4j.io.Output;
import com.pi4j.io.exception.IOException;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * <p>DigitalOutput interface.</p>
 */
public interface DigitalOutput extends Digital<DigitalOutput, DigitalOutputConfig, DigitalOutputProvider>,
        Output,
        OnOff<DigitalOutput> {

    @Deprecated
    static DigitalOutputConfigBuilder newConfigBuilder(Context context){
        return DigitalOutputConfigBuilder.newInstance(context);
    }

    static DigitalOutputConfigBuilder newConfigBuilder(){
        return DigitalOutputConfigBuilder.newInstance();
    }

    static DigitalOutputBuilder newBuilder(Context context){
        return DigitalOutputBuilder.newInstance(context);
    }


    /**
     * @throws IOException if any.
     */
    DigitalOutput state(DigitalState state) throws IOException;
    /**
     * @throws IOException if any.
     */
    DigitalOutput pulse(int interval, TimeUnit unit, DigitalState state, Callable<Void> callback) throws IOException;
    Future<?> pulseAsync(int interval, TimeUnit unit, DigitalState state, Callable<Void> callback);
    DigitalOutput blink(int delay, int duration, TimeUnit unit, DigitalState state, Callable<Void> callback);
    Future<?> blinkAsync(int delay, int duration, TimeUnit unit, DigitalState state, Callable<Void> callback);

    /**
     * @throws IOException if any.
     */
    default DigitalOutput setState(byte state) throws IOException {
        return this.state(DigitalState.getState(state));
    }
    /**
     * @throws IOException if any.
     */
    default DigitalOutput setState(short state) throws IOException {
        return this.state(DigitalState.getState(state));
    }
    /**
     * @throws IOException if any.
     */
    default DigitalOutput setState(int state) throws IOException {
        return this.state(DigitalState.getState(state));
    }
    /**
     * @throws IOException if any.
     */
    default DigitalOutput setState(long state) throws IOException {
        return this.state(DigitalState.getState(state));
    }
    /**
     * @throws IOException if any.
     */
    default DigitalOutput setState(float state) throws IOException {
        return this.state(DigitalState.getState(state));
    }
    /**
     * @throws IOException if any.
     */
    default DigitalOutput setState(double state) throws IOException {
        return this.state(DigitalState.getState(state));
    }
    /**
     * @throws IOException if any.
     */
    default DigitalOutput high() throws IOException {
        return this.state(DigitalState.HIGH);
    }
    /**
     * @throws IOException if any.
     */
    default DigitalOutput low() throws IOException {
        return this.state(DigitalState.LOW);
    }
    /**
     * @throws IOException if any.
     */
    default DigitalOutput toggle() throws IOException {
        return this.state(DigitalState.getInverseState(this.state()));
    }

    /**
     * @throws IOException if any.
     */
    default DigitalOutput pulseHigh(int interval, TimeUnit unit) throws IOException {
        return pulse(interval, unit, DigitalState.HIGH);
    }
    /**
     * @throws com.pi4j.io.exception.IOException if any.
     */
    default DigitalOutput pulseLow(int interval, TimeUnit unit) throws IOException {
        return pulse(interval, unit, DigitalState.LOW);
    }

    default Future<?> pulseHighAsync(int interval, TimeUnit unit, Callable<Void> callback){
        return pulseAsync(interval, unit, DigitalState.HIGH, callback);
    }

    default Future<?> pulseLowAsync(int interval, TimeUnit unit, Callable<Void> callback){
        return pulseAsync(interval, unit, DigitalState.LOW, callback);
    }

    /**
     * @throws com.pi4j.io.exception.IOException if any.
     */
    default DigitalOutput pulse(int interval, TimeUnit unit) throws IOException {
        return pulse(interval, unit, DigitalState.HIGH);
    }
    /**
     * @throws com.pi4j.io.exception.IOException if any.
     */
    default DigitalOutput pulse(int interval, TimeUnit unit, DigitalState state) throws IOException {
        return pulse(interval, unit, state, null);
    }

    default Future<?> pulseAsync(int interval, TimeUnit unit){
        return pulseAsync(interval, unit, DigitalState.HIGH);
    }
    default Future<?> pulseAsync(int interval, TimeUnit unit, DigitalState state){
        return pulseAsync(interval, unit, state, null);
    }

    default DigitalOutput blink(int interval, TimeUnit unit){
        return this.blink(interval, interval, unit);
    }
    default DigitalOutput blink(int delay, int duration, TimeUnit unit){
        return this.blink(delay, duration, unit, DigitalState.HIGH);
    }
    default DigitalOutput blink(int delay, int duration, TimeUnit unit, DigitalState state){
        return this.blink(delay, duration, unit, state, null);
    }

    default Future<?> blinkAsync(int interval, TimeUnit unit){
        return this.blinkAsync(interval, interval, unit, DigitalState.HIGH);
    }
    default Future<?> blinkAsync(int delay, int duration, TimeUnit unit){
        return this.blinkAsync(delay, duration, unit, DigitalState.HIGH);
    }
    default Future<?> blinkAsync(int delay, int duration, TimeUnit unit, DigitalState state){
        return this.blinkAsync(delay, duration, unit, state, null);
    }
}

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
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public interface DigitalOutput extends Digital<DigitalOutput, DigitalOutputConfig, DigitalOutputProvider>,
        Output,
        OnOff<DigitalOutput> {

    /**
     * <p>newConfigBuilder.</p>
     *
     * @param context {@link Context}
     * @return a {@link com.pi4j.io.gpio.digital.DigitalOutputConfigBuilder} object.
     */
    static DigitalOutputConfigBuilder newConfigBuilder(Context context){
        return DigitalOutputConfigBuilder.newInstance(context);
    }
    /**
     * <p>newBuilder.</p>
     *
     * @param context a {@link com.pi4j.context.Context} object.
     * @return a {@link com.pi4j.io.gpio.digital.DigitalOutputBuilder} object.
     */
    static DigitalOutputBuilder newBuilder(Context context){
        return DigitalOutputBuilder.newInstance(context);
    }

    /**
     * <p>state.</p>
     *
     * @param state a {@link com.pi4j.io.gpio.digital.DigitalState} object.
     * @return a {@link com.pi4j.io.gpio.digital.DigitalOutput} object.
     * @throws IOException if any.
     */
    DigitalOutput state(DigitalState state) throws IOException;
    /**
     * <p>pulse.</p>
     *
     * @param interval a int.
     * @param unit a {@link java.util.concurrent.TimeUnit} object.
     * @param state a {@link com.pi4j.io.gpio.digital.DigitalState} object.
     * @param callback a {@link java.util.concurrent.Callable} object.
     * @return a {@link com.pi4j.io.gpio.digital.DigitalOutput} object.
     * @throws IOException if any.
     */
    DigitalOutput pulse(int interval, TimeUnit unit, DigitalState state, Callable<Void> callback) throws IOException;
    /**
     * <p>pulseAsync.</p>
     *
     * @param interval a int.
     * @param unit a {@link java.util.concurrent.TimeUnit} object.
     * @param state a {@link com.pi4j.io.gpio.digital.DigitalState} object.
     * @param callback a {@link java.util.concurrent.Callable} object.
     * @return a {@link java.util.concurrent.Future} object.
     */
    Future<?> pulseAsync(int interval, TimeUnit unit, DigitalState state, Callable<Void> callback);
    /**
     * <p>blink.</p>
     *
     * @param delay a int.
     * @param duration a int.
     * @param unit a {@link java.util.concurrent.TimeUnit} object.
     * @param state a {@link com.pi4j.io.gpio.digital.DigitalState} object.
     * @param callback a {@link java.util.concurrent.Callable} object.
     * @return a {@link com.pi4j.io.gpio.digital.DigitalOutput} object.
     */
    DigitalOutput blink(int delay, int duration, TimeUnit unit, DigitalState state, Callable<Void> callback);
    /**
     * <p>blinkAsync.</p>
     *
     * @param delay a int.
     * @param duration a int.
     * @param unit a {@link java.util.concurrent.TimeUnit} object.
     * @param state a {@link com.pi4j.io.gpio.digital.DigitalState} object.
     * @param callback a {@link java.util.concurrent.Callable} object.
     * @return a {@link java.util.concurrent.Future} object.
     */
    Future<?> blinkAsync(int delay, int duration, TimeUnit unit, DigitalState state, Callable<Void> callback);

    /**
     * <p>setState.</p>
     *
     * @param state a byte.
     * @return a {@link com.pi4j.io.gpio.digital.DigitalOutput} object.
     * @throws IOException if any.
     */
    default DigitalOutput setState(byte state) throws IOException {
        return this.state(DigitalState.getState(state));
    }
    /**
     * <p>setState.</p>
     *
     * @param state a short.
     * @return a {@link com.pi4j.io.gpio.digital.DigitalOutput} object.
     * @throws IOException if any.
     */
    default DigitalOutput setState(short state) throws IOException {
        return this.state(DigitalState.getState(state));
    }
    /**
     * <p>setState.</p>
     *
     * @param state a int.
     * @return a {@link com.pi4j.io.gpio.digital.DigitalOutput} object.
     * @throws IOException if any.
     */
    default DigitalOutput setState(int state) throws IOException {
        return this.state(DigitalState.getState(state));
    }
    /**
     * <p>setState.</p>
     *
     * @param state a long.
     * @return a {@link com.pi4j.io.gpio.digital.DigitalOutput} object.
     * @throws IOException if any.
     */
    default DigitalOutput setState(long state) throws IOException {
        return this.state(DigitalState.getState(state));
    }
    /**
     * <p>setState.</p>
     *
     * @param state a float.
     * @return a {@link com.pi4j.io.gpio.digital.DigitalOutput} object.
     * @throws IOException if any.
     */
    default DigitalOutput setState(float state) throws IOException {
        return this.state(DigitalState.getState(state));
    }
    /**
     * <p>setState.</p>
     *
     * @param state a double.
     * @return a {@link com.pi4j.io.gpio.digital.DigitalOutput} object.
     * @throws IOException if any.
     */
    default DigitalOutput setState(double state) throws IOException {
        return this.state(DigitalState.getState(state));
    }
    /**
     * <p>high.</p>
     *
     * @return a {@link com.pi4j.io.gpio.digital.DigitalOutput} object.
     * @throws IOException if any.
     */
    default DigitalOutput high() throws IOException {
        return this.state(DigitalState.HIGH);
    }
    /**
     * <p>low.</p>
     *
     * @return a {@link com.pi4j.io.gpio.digital.DigitalOutput} object.
     * @throws IOException if any.
     */
    default DigitalOutput low() throws IOException {
        return this.state(DigitalState.LOW);
    }
    /**
     * <p>toggle.</p>
     *
     * @return a {@link com.pi4j.io.gpio.digital.DigitalOutput} object.
     * @throws IOException if any.
     */
    default DigitalOutput toggle() throws IOException {
        return this.state(DigitalState.getInverseState(this.state()));
    }

    /**
     * <p>pulseHigh.</p>
     *
     * @param interval a int.
     * @param unit a {@link java.util.concurrent.TimeUnit} object.
     * @return a {@link com.pi4j.io.gpio.digital.DigitalOutput} object.
     * @throws IOException if any.
     */
    default DigitalOutput pulseHigh(int interval, TimeUnit unit) throws IOException {
        return pulse(interval, unit, DigitalState.HIGH);
    }
    /**
     * <p>pulseLow.</p>
     *
     * @param interval a int.
     * @param unit a {@link java.util.concurrent.TimeUnit} object.
     * @return a {@link com.pi4j.io.gpio.digital.DigitalOutput} object.
     * @throws com.pi4j.io.exception.IOException if any.
     */
    default DigitalOutput pulseLow(int interval, TimeUnit unit) throws IOException {
        return pulse(interval, unit, DigitalState.LOW);
    }

    /**
     * <p>pulseHighAsync.</p>
     *
     * @param interval a int.
     * @param unit a {@link java.util.concurrent.TimeUnit} object.
     * @param callback a {@link java.util.concurrent.Callable} object.
     * @return a {@link java.util.concurrent.Future} object.
     */
    default Future<?> pulseHighAsync(int interval, TimeUnit unit, Callable<Void> callback){
        return pulseAsync(interval, unit, DigitalState.HIGH, callback);
    }

    /**
     * <p>pulseLowAsync.</p>
     *
     * @param interval a int.
     * @param unit a {@link java.util.concurrent.TimeUnit} object.
     * @param callback a {@link java.util.concurrent.Callable} object.
     * @return a {@link java.util.concurrent.Future} object.
     */
    default Future<?> pulseLowAsync(int interval, TimeUnit unit, Callable<Void> callback){
        return pulseAsync(interval, unit, DigitalState.LOW, callback);
    }

    /**
     * <p>pulse.</p>
     *
     * @param interval a int.
     * @param unit a {@link java.util.concurrent.TimeUnit} object.
     * @return a {@link com.pi4j.io.gpio.digital.DigitalOutput} object.
     * @throws com.pi4j.io.exception.IOException if any.
     */
    default DigitalOutput pulse(int interval, TimeUnit unit) throws IOException {
        return pulse(interval, unit, DigitalState.HIGH);
    }
    /**
     * <p>pulse.</p>
     *
     * @param interval a int.
     * @param unit a {@link java.util.concurrent.TimeUnit} object.
     * @param state a {@link com.pi4j.io.gpio.digital.DigitalState} object.
     * @return a {@link com.pi4j.io.gpio.digital.DigitalOutput} object.
     * @throws com.pi4j.io.exception.IOException if any.
     */
    default DigitalOutput pulse(int interval, TimeUnit unit, DigitalState state) throws IOException {
        return pulse(interval, unit, state, null);
    }

    /**
     * <p>pulseAsync.</p>
     *
     * @param interval a int.
     * @param unit a {@link java.util.concurrent.TimeUnit} object.
     * @return a {@link java.util.concurrent.Future} object.
     */
    default Future<?> pulseAsync(int interval, TimeUnit unit){
        return pulseAsync(interval, unit, DigitalState.HIGH);
    }
    /**
     * <p>pulseAsync.</p>
     *
     * @param interval a int.
     * @param unit a {@link java.util.concurrent.TimeUnit} object.
     * @param state a {@link com.pi4j.io.gpio.digital.DigitalState} object.
     * @return a {@link java.util.concurrent.Future} object.
     */
    default Future<?> pulseAsync(int interval, TimeUnit unit, DigitalState state){
        return pulseAsync(interval, unit, state, null);
    }

    /**
     * <p>blink.</p>
     *
     * @param interval a int.
     * @param unit a {@link java.util.concurrent.TimeUnit} object.
     * @return a {@link com.pi4j.io.gpio.digital.DigitalOutput} object.
     */
    default DigitalOutput blink(int interval, TimeUnit unit){
        return this.blink(interval, interval, unit);
    }
    /**
     * <p>blink.</p>
     *
     * @param delay a int.
     * @param duration a int.
     * @param unit a {@link java.util.concurrent.TimeUnit} object.
     * @return a {@link com.pi4j.io.gpio.digital.DigitalOutput} object.
     */
    default DigitalOutput blink(int delay, int duration, TimeUnit unit){
        return this.blink(delay, duration, unit, DigitalState.HIGH);
    }
    /**
     * <p>blink.</p>
     *
     * @param delay a int.
     * @param duration a int.
     * @param unit a {@link java.util.concurrent.TimeUnit} object.
     * @param state a {@link com.pi4j.io.gpio.digital.DigitalState} object.
     * @return a {@link com.pi4j.io.gpio.digital.DigitalOutput} object.
     */
    default DigitalOutput blink(int delay, int duration, TimeUnit unit, DigitalState state){
        return this.blink(delay, duration, unit, state, null);
    }

    /**
     * <p>blinkAsync.</p>
     *
     * @param interval a int.
     * @param unit a {@link java.util.concurrent.TimeUnit} object.
     * @return a {@link java.util.concurrent.Future} object.
     */
    default Future<?> blinkAsync(int interval, TimeUnit unit){
        return this.blinkAsync(interval, interval, unit, DigitalState.HIGH);
    }
    /**
     * <p>blinkAsync.</p>
     *
     * @param delay a int.
     * @param duration a int.
     * @param unit a {@link java.util.concurrent.TimeUnit} object.
     * @return a {@link java.util.concurrent.Future} object.
     */
    default Future<?> blinkAsync(int delay, int duration, TimeUnit unit){
        return this.blinkAsync(delay, duration, unit, DigitalState.HIGH);
    }
    /**
     * <p>blinkAsync.</p>
     *
     * @param delay a int.
     * @param duration a int.
     * @param unit a {@link java.util.concurrent.TimeUnit} object.
     * @param state a {@link com.pi4j.io.gpio.digital.DigitalState} object.
     * @return a {@link java.util.concurrent.Future} object.
     */
    default Future<?> blinkAsync(int delay, int duration, TimeUnit unit, DigitalState state){
        return this.blinkAsync(delay, duration, unit, state, null);
    }
}

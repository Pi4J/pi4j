package com.pi4j.io.serial;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  SerialConfigBuilder.java
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
import com.pi4j.io.IODeviceConfigBuilder;
import com.pi4j.io.serial.impl.DefaultSerialConfigBuilder;

/**
 * <p>SerialConfigBuilder interface.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public interface SerialConfigBuilder extends
    IODeviceConfigBuilder<SerialConfigBuilder, SerialConfig> {
    /**
     * <p>newInstance.</p>
     *
     * @param context {@link Context}
     * @return a {@link com.pi4j.io.serial.SerialConfigBuilder} object.
     */
    static SerialConfigBuilder newInstance(Context context) {
        return DefaultSerialConfigBuilder.newInstance(context);
    }

    /**
     * <p>baud.</p>
     *
     * @param port a {@link java.lang.String} object.
     * @return a {@link com.pi4j.io.serial.SerialConfigBuilder} object.
     */
    SerialConfigBuilder port(String port);

    /**
     * <p>baud.</p>
     *
     * @param rate a {@link java.lang.Integer} object.
     * @return a {@link com.pi4j.io.serial.SerialConfigBuilder} object.
     */
    SerialConfigBuilder baud(Integer rate);

    /**
     * <p>baud.</p>
     *
     * @param rate a {@link com.pi4j.io.serial.Baud} object.
     * @return a {@link com.pi4j.io.serial.SerialConfigBuilder} object.
     */
    default SerialConfigBuilder baud(Baud rate) {
        return baud(rate.value());
    }

    /**
     * <p>dataBits.</p>
     *
     * @param bits a {@link com.pi4j.io.serial.DataBits} object.
     * @return a {@link com.pi4j.io.serial.SerialConfigBuilder} object.
     */
    SerialConfigBuilder dataBits(DataBits bits);

    /**
     * <p>dataBits.</p>
     *
     * @param bits a {@link java.lang.Integer} object.
     * @return a {@link com.pi4j.io.serial.SerialConfigBuilder} object.
     */
    default SerialConfigBuilder dataBits(Integer bits) {
        return dataBits(DataBits.getInstance(bits));
    }

    /**
     * <p>parity.</p>
     *
     * @param parity a {@link com.pi4j.io.serial.Parity} object.
     * @return a {@link com.pi4j.io.serial.SerialConfigBuilder} object.
     */
    SerialConfigBuilder parity(Parity parity);

    /**
     * <p>parity.</p>
     *
     * @param parity a int.
     * @return a {@link com.pi4j.io.serial.SerialConfigBuilder} object.
     */
    default SerialConfigBuilder parity(int parity) {
        return parity(Parity.getInstance(parity));
    }

    /**
     * <p>stopBits.</p>
     *
     * @param bits a {@link com.pi4j.io.serial.StopBits} object.
     * @return a {@link com.pi4j.io.serial.SerialConfigBuilder} object.
     */
    SerialConfigBuilder stopBits(StopBits bits);

    /**
     * <p>stopBits.</p>
     *
     * @param bits a {@link java.lang.Integer} object.
     * @return a {@link com.pi4j.io.serial.SerialConfigBuilder} object.
     */
    default SerialConfigBuilder stopBits(Integer bits) {
        return stopBits(StopBits.getInstance(bits));
    }

    /**
     * <p>flowControl.</p>
     *
     * @param control a {@link com.pi4j.io.serial.FlowControl} object.
     * @return a {@link com.pi4j.io.serial.SerialConfigBuilder} object.
     */
    SerialConfigBuilder flowControl(FlowControl control);

    /**
     * <p>flowControl.</p>
     *
     * @param control a {@link java.lang.Integer} object.
     * @return a {@link com.pi4j.io.serial.SerialConfigBuilder} object.
     */
    default SerialConfigBuilder flowControl(Integer control) {
        return flowControl(FlowControl.getInstance(control));
    }

    /**
     * <p>baud_50.</p>
     *
     * @return a {@link com.pi4j.io.serial.SerialConfigBuilder} object.
     */
    default SerialConfigBuilder baud_50() {
        return baud(Baud._50);
    }

    /**
     * <p>baud_75.</p>
     *
     * @return a {@link com.pi4j.io.serial.SerialConfigBuilder} object.
     */
    default SerialConfigBuilder baud_75() {
        return baud(Baud._75);
    }

    /**
     * <p>baud_110.</p>
     *
     * @return a {@link com.pi4j.io.serial.SerialConfigBuilder} object.
     */
    default SerialConfigBuilder baud_110() {
        return baud(Baud._110);
    }

    /**
     * <p>baud_134.</p>
     *
     * @return a {@link com.pi4j.io.serial.SerialConfigBuilder} object.
     */
    default SerialConfigBuilder baud_134() {
        return baud(Baud._134);
    }

    /**
     * <p>baud_150.</p>
     *
     * @return a {@link com.pi4j.io.serial.SerialConfigBuilder} object.
     */
    default SerialConfigBuilder baud_150() {
        return baud(Baud._150);
    }

    /**
     * <p>baud_200.</p>
     *
     * @return a {@link com.pi4j.io.serial.SerialConfigBuilder} object.
     */
    default SerialConfigBuilder baud_200() {
        return baud(Baud._200);
    }

    /**
     * <p>baud_300.</p>
     *
     * @return a {@link com.pi4j.io.serial.SerialConfigBuilder} object.
     */
    default SerialConfigBuilder baud_300() {
        return baud(Baud._300);
    }

    /**
     * <p>baud_600.</p>
     *
     * @return a {@link com.pi4j.io.serial.SerialConfigBuilder} object.
     */
    default SerialConfigBuilder baud_600() {
        return baud(Baud._600);
    }

    /**
     * <p>baud_1200.</p>
     *
     * @return a {@link com.pi4j.io.serial.SerialConfigBuilder} object.
     */
    default SerialConfigBuilder baud_1200() {
        return baud(Baud._1200);
    }

    /**
     * <p>baud_1800.</p>
     *
     * @return a {@link com.pi4j.io.serial.SerialConfigBuilder} object.
     */
    default SerialConfigBuilder baud_1800() {
        return baud(Baud._1800);
    }

    /**
     * <p>baud_2400.</p>
     *
     * @return a {@link com.pi4j.io.serial.SerialConfigBuilder} object.
     */
    default SerialConfigBuilder baud_2400() {
        return baud(Baud._2400);
    }

    /**
     * <p>baud_4800.</p>
     *
     * @return a {@link com.pi4j.io.serial.SerialConfigBuilder} object.
     */
    default SerialConfigBuilder baud_4800() {
        return baud(Baud._4800);
    }

    /**
     * <p>baud_9600.</p>
     *
     * @return a {@link com.pi4j.io.serial.SerialConfigBuilder} object.
     */
    default SerialConfigBuilder baud_9600() {
        return baud(Baud._9600);
    }

    /**
     * <p>baud_19200.</p>
     *
     * @return a {@link com.pi4j.io.serial.SerialConfigBuilder} object.
     */
    default SerialConfigBuilder baud_19200() {
        return baud(Baud._19200);
    }

    /**
     * <p>baud_38400.</p>
     *
     * @return a {@link com.pi4j.io.serial.SerialConfigBuilder} object.
     */
    default SerialConfigBuilder baud_38400() {
        return baud(Baud._38400);
    }

    /**
     * <p>baud_57600.</p>
     *
     * @return a {@link com.pi4j.io.serial.SerialConfigBuilder} object.
     */
    default SerialConfigBuilder baud_57600() {
        return baud(Baud._57600);
    }

    /**
     * <p>baud_115200.</p>
     *
     * @return a {@link com.pi4j.io.serial.SerialConfigBuilder} object.
     */
    default SerialConfigBuilder baud_115200() {
        return baud(Baud._115200);
    }

    /**
     * <p>baud_230400.</p>
     *
     * @return a {@link com.pi4j.io.serial.SerialConfigBuilder} object.
     */
    default SerialConfigBuilder baud_230400() {
        return baud(Baud._230400);
    }

    /**
     * <p>dataBits_5.</p>
     *
     * @return a {@link com.pi4j.io.serial.SerialConfigBuilder} object.
     */
    default SerialConfigBuilder dataBits_5() {
        return dataBits(DataBits._5);
    }

    /**
     * <p>dataBits_6.</p>
     *
     * @return a {@link com.pi4j.io.serial.SerialConfigBuilder} object.
     */
    default SerialConfigBuilder dataBits_6() {
        return dataBits(DataBits._6);
    }

    /**
     * <p>dataBits_7.</p>
     *
     * @return a {@link com.pi4j.io.serial.SerialConfigBuilder} object.
     */
    default SerialConfigBuilder dataBits_7() {
        return dataBits(DataBits._7);
    }

    /**
     * <p>dataBits_8.</p>
     *
     * @return a {@link com.pi4j.io.serial.SerialConfigBuilder} object.
     */
    default SerialConfigBuilder dataBits_8() {
        return dataBits(DataBits._8);
    }

    /**
     * <p>parityNone.</p>
     *
     * @return a {@link com.pi4j.io.serial.SerialConfigBuilder} object.
     */
    default SerialConfigBuilder parityNone() {
        return parity(Parity.NONE);
    }

    /**
     * <p>parityOdd.</p>
     *
     * @return a {@link com.pi4j.io.serial.SerialConfigBuilder} object.
     */
    default SerialConfigBuilder parityOdd() {
        return parity(Parity.ODD);
    }

    /**
     * <p>parityEven.</p>
     *
     * @return a {@link com.pi4j.io.serial.SerialConfigBuilder} object.
     */
    default SerialConfigBuilder parityEven() {
        return parity(Parity.EVEN);
    }

    /**
     * <p>parityMark.</p>
     *
     * @return a {@link com.pi4j.io.serial.SerialConfigBuilder} object.
     */
    default SerialConfigBuilder parityMark() {
        return parity(Parity.MARK);
    }

    /**
     * <p>paritySpace.</p>
     *
     * @return a {@link com.pi4j.io.serial.SerialConfigBuilder} object.
     */
    default SerialConfigBuilder paritySpace() {
        return parity(Parity.SPACE);
    }

    /**
     * <p>flowNone.</p>
     *
     * @return a {@link com.pi4j.io.serial.SerialConfigBuilder} object.
     */
    default SerialConfigBuilder flowNone() {
        return flowControl(FlowControl.NONE);
    }

    /**
     * <p>flowHardware.</p>
     *
     * @return a {@link com.pi4j.io.serial.SerialConfigBuilder} object.
     */
    default SerialConfigBuilder flowHardware() {
        return flowControl(FlowControl.HARDWARE);
    }

    /**
     * <p>flowSoftware.</p>
     *
     * @return a {@link com.pi4j.io.serial.SerialConfigBuilder} object.
     */
    default SerialConfigBuilder flowSoftware() {
        return flowControl(FlowControl.SOFTWARE);
    }

    /**
     * <p>stopBits_1.</p>
     *
     * @return a {@link com.pi4j.io.serial.SerialConfigBuilder} object.
     */
    default SerialConfigBuilder stopBits_1() {
        return stopBits(StopBits._1);
    }

    /**
     * <p>stopBits_2.</p>
     *
     * @return a {@link com.pi4j.io.serial.SerialConfigBuilder} object.
     */
    default SerialConfigBuilder stopBits_2() {
        return stopBits(StopBits._2);
    }

    /**
     * <p>baud8N1.</p>
     *
     * @param baud a {@link com.pi4j.io.serial.Baud} object.
     * @return a {@link com.pi4j.io.serial.SerialConfigBuilder} object.
     */
    default SerialConfigBuilder baud8N1(Baud baud) {
        return baud8N1(baud.value());
    }

    /**
     * <p>baud8N1.</p>
     *
     * @param baud a {@link java.lang.Integer} object.
     * @return a {@link com.pi4j.io.serial.SerialConfigBuilder} object.
     */
    default SerialConfigBuilder baud8N1(Integer baud) {
        baud(baud);
        dataBits(DataBits._8);
        parity(Parity.NONE);
        stopBits(StopBits._1);
        flowControl(FlowControl.NONE);
        return this;
    }

    /**
     * <p>use_9600_N81.</p>
     *
     * @return a {@link com.pi4j.io.serial.SerialConfigBuilder} object.
     */
    default SerialConfigBuilder use_9600_N81() {
        return baud8N1(Baud._9600);
    }

    /**
     * <p>use_38400_N81.</p>
     *
     * @return a {@link com.pi4j.io.serial.SerialConfigBuilder} object.
     */
    default SerialConfigBuilder use_38400_N81() {
        return baud8N1(Baud._38400);
    }

    /**
     * <p>use_57600_N81.</p>
     *
     * @return a {@link com.pi4j.io.serial.SerialConfigBuilder} object.
     */
    default SerialConfigBuilder use_57600_N81() {
        return baud8N1(Baud._57600);
    }

    /**
     * <p>use_115200_N81.</p>
     *
     * @return a {@link com.pi4j.io.serial.SerialConfigBuilder} object.
     */
    default SerialConfigBuilder use_115200_N81() {
        return baud8N1(Baud._115200);
    }
}

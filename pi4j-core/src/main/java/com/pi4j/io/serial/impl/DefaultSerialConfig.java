package com.pi4j.io.serial.impl;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  DefaultSerialConfig.java
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

import com.pi4j.io.gpio.digital.PullResistance;
import com.pi4j.io.impl.IODeviceConfigBase;
import com.pi4j.io.serial.*;
import com.pi4j.util.StringUtil;

import java.util.Map;

/**
 * <p>DefaultSerialConfig class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public class DefaultSerialConfig
    extends IODeviceConfigBase<SerialConfig>
    implements SerialConfig {

    // private configuration properties
    protected final Integer baud;
    protected final StopBits stopBits;
    protected final DataBits dataBits;
    protected final Parity parity;
    protected final FlowControl flowControl;

    // private configuration properties
    protected PullResistance pullResistance = PullResistance.OFF;

    /**
     * PRIVATE CONSTRUCTOR
     *
     * @param properties a {@link java.util.Map} object.
     */
    protected DefaultSerialConfig(Map<String, String> properties) {
        super(properties);

        // define default property values if any are missing (based on the required address value)
        this.id = StringUtil.setIfNullOrEmpty(this.id, "SERIAL-" + this.device, true);
        this.name = StringUtil.setIfNullOrEmpty(this.name, "SERIAL-" + this.device, true);
        this.description = StringUtil.setIfNullOrEmpty(this.description, "SERIAL-" + this.device, true);

        // load optional BAUD RATE from properties
        if (properties.containsKey(BAUD_KEY)) {
            this.baud = StringUtil.parseInteger(properties.get(BAUD_KEY), Serial.DEFAULT_BAUD);
        } else {
            this.baud = Serial.DEFAULT_BAUD;
        }

        // load optional DATA BITS from properties
        if (properties.containsKey(DATA_BITS_KEY)) {
            this.dataBits = DataBits.parse(properties.get(DATA_BITS_KEY));
        } else {
            this.dataBits = Serial.DEFAULT_DATA_BITS;
        }

        // load optional PARITY from properties
        if (properties.containsKey(PARITY_KEY)) {
            this.parity = Parity.parse(properties.get(PARITY_KEY));
        } else {
            this.parity = Serial.DEFAULT_PARITY;
        }

        // load optional STOP BITS from properties
        if (properties.containsKey(STOP_BITS_KEY)) {
            this.stopBits = StopBits.parse(properties.get(STOP_BITS_KEY));
        } else {
            this.stopBits = Serial.DEFAULT_STOP_BITS;
        }

        // load optional FLOW CONTROL from properties
        if (properties.containsKey(FLOW_CONTROL_KEY)) {
            this.flowControl = FlowControl.parse(properties.get(FLOW_CONTROL_KEY));
        } else {
            this.flowControl = Serial.DEFAULT_FLOW_CONTROL;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer baud() {
        return this.baud;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StopBits stopBits() {
        return this.stopBits;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataBits dataBits() {
        return this.dataBits;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Parity parity() {
        return this.parity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FlowControl flowControl() {
        return this.flowControl;
    }
}

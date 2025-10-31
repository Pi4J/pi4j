package com.pi4j.io.serial;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  SerialConfig.java
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

import com.pi4j.config.PortConfig;
import com.pi4j.io.IOConfig;

/**
 * <p>SerialConfig interface.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public interface SerialConfig extends PortConfig<SerialConfig>, IOConfig<SerialConfig> {
    /**
     * Constant <code>BAUD_KEY="baud"</code>
     */
    String BAUD_KEY = "baud";
    /**
     * Constant <code>STOP_BITS_KEY="stop-bits"</code>
     */
    String STOP_BITS_KEY = "stop-bits";
    /**
     * Constant <code>DATA_BITS_KEY="data-bits"</code>
     */
    String DATA_BITS_KEY = "data-bits";
    /**
     * Constant <code>PARITY_KEY="parity"</code>
     */
    String PARITY_KEY = "parity";
    /**
     * Constant <code>FLOW_CONTROL_KEY="flow-control"</code>
     */
    String FLOW_CONTROL_KEY = "flow-control";

    /**
     * <p>baud.</p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    Integer baud();

    /**
     * <p>getBaud.</p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    default Integer getBaud() {
        return baud();
    }

    /**
     * <p>stopBits.</p>
     *
     * @return a {@link com.pi4j.io.serial.StopBits} object.
     */
    StopBits stopBits();

    /**
     * <p>getStopBits.</p>
     *
     * @return a {@link com.pi4j.io.serial.StopBits} object.
     */
    default StopBits getStopBits() {
        return stopBits();
    }

    /**
     * <p>dataBits.</p>
     *
     * @return a {@link com.pi4j.io.serial.DataBits} object.
     */
    DataBits dataBits();

    /**
     * <p>getDataBits.</p>
     *
     * @return a {@link com.pi4j.io.serial.DataBits} object.
     */
    default DataBits getDataBits() {
        return dataBits();
    }

    /**
     * <p>parity.</p>
     *
     * @return a {@link com.pi4j.io.serial.Parity} object.
     */
    Parity parity();

    /**
     * <p>getParity.</p>
     *
     * @return a {@link com.pi4j.io.serial.Parity} object.
     */
    default Parity getParity() {
        return parity();
    }

    /**
     * <p>flowControl.</p>
     *
     * @return a {@link com.pi4j.io.serial.FlowControl} object.
     */
    FlowControl flowControl();

    /**
     * <p>getFlowControl.</p>
     *
     * @return a {@link com.pi4j.io.serial.FlowControl} object.
     */
    default FlowControl getFlowControl() {
        return flowControl();
    }
}

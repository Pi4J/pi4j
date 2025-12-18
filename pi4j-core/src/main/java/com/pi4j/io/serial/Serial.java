package com.pi4j.io.serial;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  Serial.java
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
import com.pi4j.io.IO;
import com.pi4j.io.IODataReader;
import com.pi4j.io.IODataWriter;

/**
 * <p>Serial interface.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public interface Serial extends IO<Serial, SerialConfig, SerialProvider>, AutoCloseable, IODataWriter, IODataReader {

    /**
     * Constant <code>DEFAULT_BAUD=9600</code>
     */
    int DEFAULT_BAUD = 9600;
    /**
     * Constant <code>DEFAULT_DATA_BITS</code>
     */
    DataBits DEFAULT_DATA_BITS = DataBits._8;
    /**
     * Constant <code>DEFAULT_PARITY</code>
     */
    Parity DEFAULT_PARITY = Parity.NONE;
    /**
     * Constant <code>DEFAULT_STOP_BITS</code>
     */
    StopBits DEFAULT_STOP_BITS = StopBits._1;
    /**
     * Constant <code>DEFAULT_FLOW_CONTROL</code>
     */
    FlowControl DEFAULT_FLOW_CONTROL = FlowControl.NONE;

    /**
     * <p>newConfigBuilder.</p>
     *
     * @param context {@link Context}
     * @return a {@link com.pi4j.io.serial.SerialConfigBuilder} object.
     */
    static SerialConfigBuilder newConfigBuilder(Context context) {
        return SerialConfigBuilder.newInstance(context);
    }

    /**
     * Serial Device Communication State is OPEN
     *
     * @return The Serial device communication state
     */
    boolean isOpen();


    /**
     * Get the number of data bytes available in the serial receive buffer
     *
     * @return a int.
     */
    int available();

    /**
     * This function will drain the current serial receive buffer of any lingering bytes.
     *
     * @return Returns the number of bytes of data drained (&gt;=0) if OK, otherwise a negative error code.
     */
    default int drain() {
        // get number of bytes available
        int avail = this.available();
        if (avail > 0) {
            byte[] temp = new byte[avail];
            return this.read(temp);
        } else {
            return 0;
        }
    }

    /**
     * <p>open.</p>
     */
    void open();

    /**
     * <p>close.</p>
     */
    void close();
}

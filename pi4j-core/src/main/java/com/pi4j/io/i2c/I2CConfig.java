package com.pi4j.io.i2c;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  I2CConfig.java
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
import com.pi4j.io.IOConfig;

/**
 * <p>I2CConfig interface.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public interface I2CConfig extends IOConfig<I2CConfig> {

    /** Constant <code>BUS_KEY="bus"</code> */
    String BUS_KEY = "bus";
    /** Constant <code>DEVICE_KEY="device"</code> */
    String DEVICE_KEY = "device";

    String I2C_IMPLEMENTATION = "i2c_implementation";

    /**
     * <p>bus.</p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    Integer bus();
    /**
     * <p>getBus.</p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    default Integer getBus() {
        return bus();
    }

    /**
     * <p>device.</p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    Integer device();
    /**
     * <p>getDevice.</p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    default Integer getDevice() {
        return device();
    }

    I2CImplementation i2cImplementation();

    default I2CImplementation getI2CImplementation() {
        return i2cImplementation();
    }

    /**
     * <p>newBuilder.</p>
     *
     * @param context {@link Context}
     * @return a {@link com.pi4j.io.i2c.I2CConfigBuilder} object.
     */
    static I2CConfigBuilder newBuilder(Context context)  {
        return I2CConfigBuilder.newInstance(context);
    }
}

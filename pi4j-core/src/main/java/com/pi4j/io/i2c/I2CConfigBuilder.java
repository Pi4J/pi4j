package com.pi4j.io.i2c;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  I2CConfigBuilder.java
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

import com.pi4j.config.ConfigBuilder;
import com.pi4j.context.Context;
import com.pi4j.io.IOConfigBuilder;
import com.pi4j.io.i2c.impl.DefaultI2CConfigBuilder;

/**
 * <p>I2CConfigBuilder interface.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public interface I2CConfigBuilder extends
        IOConfigBuilder<I2CConfigBuilder, I2CConfig>,
        ConfigBuilder<I2CConfigBuilder, I2CConfig> {
    /**
     * <p>newInstance.</p>
     *
     * @param context {@link Context}
     * @return a {@link com.pi4j.io.i2c.I2CConfigBuilder} object.
     */
    static I2CConfigBuilder newInstance(Context context)  {
        return DefaultI2CConfigBuilder.newInstance(context);
    }

    /**
     * <p>bus.</p>
     *
     * @param bus a {@link java.lang.Integer} object.
     * @return a {@link com.pi4j.io.i2c.I2CConfigBuilder} object.
     */
    I2CConfigBuilder bus(Integer bus);
    /**
     * <p>device.</p>
     *
     * @param device a {@link java.lang.Integer} object.
     * @return a {@link com.pi4j.io.i2c.I2CConfigBuilder} object.
     */
    I2CConfigBuilder device(Integer device);

    I2CConfigBuilder i2cImplementation(I2CImplementation i2CImplementation);
}

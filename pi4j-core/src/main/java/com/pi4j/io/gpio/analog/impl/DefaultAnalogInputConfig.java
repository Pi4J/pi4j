package com.pi4j.io.gpio.analog.impl;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  DefaultAnalogInputConfig.java
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

import com.pi4j.io.gpio.analog.AnalogInputConfig;
import com.pi4j.util.StringUtil;

import java.util.Map;

/**
 * <p>DefaultAnalogInputConfig class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public class DefaultAnalogInputConfig
        extends AnalogConfigBase<AnalogInputConfig>
        implements AnalogInputConfig {

    protected Integer busNumber = null;

    /**
     * PRIVATE CONSTRUCTOR
     */
    private DefaultAnalogInputConfig(){
        super();
    }

    /**
     * PRIVATE CONSTRUCTOR
     *
     * @param properties a {@link java.util.Map} object.
     */
    protected DefaultAnalogInputConfig(Map<String,String> properties){
        super(properties);

        // define default property values if any are missing (based on the required address value)
        this.id = StringUtil.setIfNullOrEmpty(this.id, "AIN-" + this.address, true);
        this.name = StringUtil.setIfNullOrEmpty(this.name, "AIN-" + this.address, true);
        this.description = StringUtil.setIfNullOrEmpty(this.description, "AIN-" + this.address, true);

        if(properties.containsKey(BUS_NUMBER)){
            this.busNumber = Integer.parseInt(properties.get(BUS_NUMBER));
        }
    }

    @Override
    public Integer busNumber() {
        return busNumber;
    }
}

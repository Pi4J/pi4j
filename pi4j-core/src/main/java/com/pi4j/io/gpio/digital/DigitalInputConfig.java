package com.pi4j.io.gpio.digital;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  DigitalInputConfig.java
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

/**
 * <p>DigitalInputConfig interface.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public interface DigitalInputConfig extends DigitalConfig<DigitalInputConfig> {

    /** Constant <code>PULL_RESISTANCE_KEY="pull"</code> */
    String PULL_RESISTANCE_KEY = "pull";
    /** Constant <code>DEBOUNCE_RESISTANCE_KEY="debounce"</code> */
    String DEBOUNCE_RESISTANCE_KEY = "debounce";

    /**
     * <p>pull.</p>
     *
     * @return a {@link com.pi4j.io.gpio.digital.PullResistance} object.
     */
    PullResistance pull();
    /**
     * <p>getPull.</p>
     *
     * @return a {@link com.pi4j.io.gpio.digital.PullResistance} object.
     */
    default PullResistance getPull(){
        return pull();
    }

    /**
     * <p>debounce.</p>
     *
     * @return a {@link java.lang.Long} object.
     */
    Long debounce();
    /**
     * <p>getDebounce.</p>
     *
     * @return a {@link java.lang.Long} object.
     */
    default Long getDebounce(){ return debounce(); }

    /**
     * <p>newBuilder.</p>
     *
     * @param context {@link Context}
     * @return a {@link com.pi4j.io.gpio.digital.DigitalInputConfigBuilder} object.
     * @deprecated use {@link #newBuilder()}
     */
    @Deprecated
    static DigitalInputConfigBuilder newBuilder(Context context)  {
        return DigitalInputConfigBuilder.newInstance(context);
    }

    static DigitalInputConfigBuilder newBuilder()  {
        return DigitalInputConfigBuilder.newInstance();
    }
}

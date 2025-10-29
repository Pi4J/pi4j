package com.pi4j.io;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  IOAddressConfigBuilder.java
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

import com.pi4j.config.PinConfigBuilder;
import com.pi4j.platform.Platform;
import com.pi4j.provider.Provider;

/**
 * <p>IOConfigBuilder interface.</p>
 *
 * @param <BUILDER_TYPE>
 * @param <CONFIG_TYPE>
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public interface IOPinConfigBuilder<BUILDER_TYPE, CONFIG_TYPE>
    extends IOConfigBuilder<BUILDER_TYPE, CONFIG_TYPE>,
    PinConfigBuilder<BUILDER_TYPE, CONFIG_TYPE> {
    BUILDER_TYPE provider(String provider);

    BUILDER_TYPE provider(Class<? extends Provider> providerClass);

    BUILDER_TYPE platform(String platform);

    BUILDER_TYPE platform(Class<? extends Platform> platformClass);
}

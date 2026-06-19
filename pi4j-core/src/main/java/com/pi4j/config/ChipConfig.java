package com.pi4j.config;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  ChipConfig.java
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

/**
 * Configuration contract for I/O instances that are addressed by a chip number, for example the
 * GPIO chip exposed by the Linux GPIO character-device interface. The {@code chip} value selects
 * which controller chip the I/O belongs to.
 *
 * @param <CONFIG_TYPE> the concrete configuration sub-type, returned by fluent accessors to enable type-safe chaining
 */
public interface ChipConfig<CONFIG_TYPE extends Config> extends Config<CONFIG_TYPE> {
    /**
     * Property key under which the chip number is stored in the configuration properties map.
     */
    String CHIP_KEY = "chip";

    /**
     * Returns the chip number this I/O is assigned to.
     *
     * @return the chip number, or {@code null} if not configured
     */
    Integer chip();

    /**
     * Returns the chip number this I/O is assigned to.
     *
     * @return the chip number, or {@code null} if not configured
     */
    default Integer getChip() {
        return this.chip();
    }
}

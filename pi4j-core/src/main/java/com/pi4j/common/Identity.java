package com.pi4j.common;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  Identity.java
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
 * <p>Identity interface.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public interface Identity extends Describable {
    /**
     * <p>id.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String id();
    /**
     * <p>name.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String name();
    /**
     * <p>description.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String description();
    /**
     * <p>metadata.</p>
     *
     * @return a {@link com.pi4j.common.Metadata} object.
     */
    Metadata metadata();

    /**
     * <p>getId.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    default String getId(){
        return id();
    }

    /**
     * <p>getName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    default String getName(){
        return name();
    }

    /**
     * <p>getDescription.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    default String getDescription(){
        return description();
    }

    /**
     * <p>getMetadata.</p>
     *
     * @return a {@link com.pi4j.common.Metadata} object.
     */
    default Metadata getMetadata(){
        return metadata();
    }

    @Override
    default Descriptor describe() {
        return Descriptor.create()
                .id(id())
                .name(name())
                .description(description());
    }
}

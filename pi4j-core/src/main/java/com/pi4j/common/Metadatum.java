package com.pi4j.common;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  Metadatum.java
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

import com.pi4j.common.impl.MetadatumImpl;

/**
 * <p>Metadatum interface.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public interface Metadatum extends Describable {

    /**
     * <p>key.</p>
     *
     * @param key a {@link java.lang.String} object.
     * @return a {@link com.pi4j.common.Metadatum} object.
     */
    Metadatum key(String key);
    /**
     * <p>value.</p>
     *
     * @param value a {@link java.lang.Object} object.
     * @return a {@link com.pi4j.common.Metadatum} object.
     */
    Metadatum value(Object value);
    /**
     * <p>description.</p>
     *
     * @param description a {@link java.lang.String} object.
     * @return a {@link com.pi4j.common.Metadatum} object.
     */
    Metadatum description(String description);
    /**
     * <p>key.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String key();
    /**
     * <p>value.</p>
     *
     * @return a {@link java.lang.Object} object.
     */
    Object value();
    /**
     * <p>description.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String description();

    /**
     * <p>create.</p>
     *
     * @return a {@link com.pi4j.common.Metadatum} object.
     */
    static Metadatum create(){
        return new MetadatumImpl();
    }

    /**
     * <p>create.</p>
     *
     * @param key a {@link java.lang.String} object.
     * @return a {@link com.pi4j.common.Metadatum} object.
     */
    static Metadatum create(String key){
        return create().key(key);
    }

    /**
     * <p>create.</p>
     *
     * @param key a {@link java.lang.String} object.
     * @param value a {@link java.lang.Object} object.
     * @return a {@link com.pi4j.common.Metadatum} object.
     */
    static Metadatum create(String key, Object value){
        return create(key).value(value);
    }

    /**
     * <p>create.</p>
     *
     * @param key a {@link java.lang.String} object.
     * @param value a {@link java.lang.Object} object.
     * @param description a {@link java.lang.String} object.
     * @return a {@link com.pi4j.common.Metadatum} object.
     */
    static Metadatum create(String key, Object value, String description){
        return create(key, value).description(description);
    }

    @Override
    default Descriptor describe() {
        return Descriptor.create().name(key()).description(description()).value(value());
    }
}

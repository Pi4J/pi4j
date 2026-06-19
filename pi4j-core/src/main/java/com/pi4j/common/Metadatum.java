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

public interface Metadatum extends Describable {

    Metadatum key(String key);

    Metadatum value(Object value);

    Metadatum description(String description);

    String key();

    Object value();

    String description();

    static Metadatum create() {
        return new MetadatumImpl();
    }

    static Metadatum create(String key) {
        return create().key(key);
    }

    static Metadatum create(String key, Object value) {
        return create(key).value(value);
    }

    static Metadatum create(String key, Object value, String description) {
        return create(key, value).description(description);
    }

    @Override
    default Descriptor describe() {
        return Descriptor.create().name(key()).description(description()).value(value());
    }
}

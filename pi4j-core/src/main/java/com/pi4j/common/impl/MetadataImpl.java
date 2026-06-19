package com.pi4j.common.impl;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  MetadataImpl.java
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

import com.pi4j.common.Metadata;
import com.pi4j.common.Metadatum;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class MetadataImpl implements Metadata {

    private Map<String, Metadatum> metadata = Collections.synchronizedMap(new LinkedHashMap<>());

    @Override
    public int size() {
        return this.metadata.size();
    }

    @Override
    public boolean isEmpty() {
        return this.metadata.isEmpty();
    }

    @Override
    public boolean contains(String key) {
        return this.metadata.containsKey(key);
    }

    @Override
    public Metadata put(Metadatum metadatum) {
        this.metadata.put(metadatum.key(), metadatum);
        return this;
    }

    @Override
    public Metadatum remove(String key) {
        return this.metadata.remove(key);
    }

    @Override
    public Metadata clear() {
        this.metadata.clear();
        return this;
    }

    @Override
    public Metadatum get(String key) {
        return this.metadata.get(key);
    }

    @Override
    public Collection<Metadatum> all() {
        return this.metadata.values();
    }
}

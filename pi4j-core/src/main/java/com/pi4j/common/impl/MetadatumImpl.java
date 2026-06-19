package com.pi4j.common.impl;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  MetadatumImpl.java
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

import com.pi4j.common.Metadatum;

/**
 * <p>MetadatumImpl class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public class MetadatumImpl implements Metadatum {

    private String key;
    private Object value;
    private String description;

    @Override
    public MetadatumImpl key(String key) {
        this.key = key;
        return this;
    }

    @Override
    public MetadatumImpl value(Object value) {
        this.value = value;
        return this;
    }

    @Override
    public MetadatumImpl description(String description) {
        this.description = description;
        return this;
    }

    @Override
    public String key() {
        return this.key;
    }

    @Override
    public Object value() {
        return this.value;
    }

    @Override
    public String description() {
        return this.description;
    }

    @Override
    public String toString(){
        return key() + " = " + value().toString();
    }

}

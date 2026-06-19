package com.pi4j.io.i2c;

import com.pi4j.provider.ProviderBase;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  I2CProviderBase.java
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
 * <p>Abstract I2CProviderBase class.</p>
 */
public abstract class I2CProviderBase
        extends ProviderBase<I2CProvider, I2C, I2CConfig>
        implements I2CProvider {

    public I2CProviderBase(){
        super();
    }

    public I2CProviderBase(String id){
        super(id);
    }

    public I2CProviderBase(String id, String name){
        super(id, name);
    }
}

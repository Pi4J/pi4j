package com.pi4j.test.provider.impl;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: TESTING  :: Unit/Integration Tests
 * FILENAME      :  TestI2CProviderImpl.java
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

import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.i2c.I2CProviderBase;
import com.pi4j.test.provider.TestI2CProvider;

/**
 * <p>TestI2CProviderImpl class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public class TestI2CProviderImpl extends I2CProviderBase implements TestI2CProvider {

    /**
     * <p>Constructor for TestI2CProviderImpl.</p>
     */
    public TestI2CProviderImpl(){ super(); }

    /**
     * <p>Constructor for TestI2CProviderImpl.</p>
     *
     * @param id a {@link java.lang.String} object.
     */
    public TestI2CProviderImpl(String id){
        super(id);
    }

    /**
     * <p>Constructor for TestI2CProviderImpl.</p>
     *
     * @param id a {@link java.lang.String} object.
     * @param name a {@link java.lang.String} object.
     */
    public TestI2CProviderImpl(String id, String name){
        super(id, name);
    }

    /** {@inheritDoc} */
    @Override
    public I2C create(I2CConfig config) {
        return null;
    }
}

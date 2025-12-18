package com.pi4j.test.provider.impl;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: TESTING  :: Unit/Integration Tests
 * FILENAME      :  TestPwmProviderImpl.java
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

import com.pi4j.io.pwm.Pwm;
import com.pi4j.io.pwm.PwmConfig;
import com.pi4j.io.pwm.PwmProviderBase;
import com.pi4j.test.provider.TestPwmProvider;

/**
 * <p>TestPwmProviderImpl class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public class TestPwmProviderImpl extends PwmProviderBase implements TestPwmProvider {

    /**
     * <p>Constructor for TestPwmProviderImpl.</p>
     */
    public TestPwmProviderImpl(){ super(); }

    /**
     * <p>Constructor for TestPwmProviderImpl.</p>
     *
     * @param id a {@link java.lang.String} object.
     */
    public TestPwmProviderImpl(String id){
        super(id);
    }

    /**
     * <p>Constructor for TestPwmProviderImpl.</p>
     *
     * @param id a {@link java.lang.String} object.
     * @param name a {@link java.lang.String} object.
     */
    public TestPwmProviderImpl(String id, String name){
        super(id, name);
    }

    /** {@inheritDoc} */
    @Override
    public Pwm create(PwmConfig config) {
        return null;
    }
}

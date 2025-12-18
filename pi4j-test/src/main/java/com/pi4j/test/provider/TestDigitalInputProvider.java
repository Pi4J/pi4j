package com.pi4j.test.provider;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: TESTING  :: Unit/Integration Tests
 * FILENAME      :  TestDigitalInputProvider.java
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

import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalInputProvider;
import com.pi4j.test.provider.impl.TestDigitalInputProviderImpl;

/**
 * <p>TestDigitalInputProvider interface.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public interface TestDigitalInputProvider extends DigitalInputProvider {
    /**
     * <p>newInstance.</p>
     *
     * @return a {@link com.pi4j.test.provider.TestDigitalInputProvider} object.
     */
    static TestDigitalInputProvider newInstance(){
        return new TestDigitalInputProviderImpl();
    }
    /**
     * <p>newInstance.</p>
     *
     * @param id a {@link java.lang.String} object.
     * @return a {@link com.pi4j.test.provider.TestDigitalInputProvider} object.
     */
    static TestDigitalInputProvider newInstance(String id){
        return new TestDigitalInputProviderImpl(id);
    }
    /**
     * <p>newInstance.</p>
     *
     * @param id a {@link java.lang.String} object.
     * @param name a {@link java.lang.String} object.
     * @return a {@link com.pi4j.test.provider.TestDigitalInputProvider} object.
     */
    static TestDigitalInputProvider newInstance(String id, String name){
        return new TestDigitalInputProviderImpl(id, name);
    }

    /**
     * <p>create.</p>
     *
     * @param <T> a T object.
     * @return a T object.
     */
    default <T extends DigitalInput> T create() {
        return create(0);
    }
}

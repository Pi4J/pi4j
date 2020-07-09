package com.pi4j.common;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  Lifecycle.java
 *
 * This file is part of the Pi4J project. More information about
 * this project can be found here:  https://pi4j.com/
 * **********************************************************************
 * %%
 * Copyright (C) 2012 - 2020 Pi4J
 * %%
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

import com.pi4j.context.Context;
import com.pi4j.exception.InitializeException;
import com.pi4j.exception.ShutdownException;

/**
 * <p>Lifecycle interface.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public interface Lifecycle<T> {

    /**
     * <p>Initialize a lifecycle providing the {@link Context} which can be stored by the implementation for later
     * reference, e.g. to start a thread in the {@link com.pi4j.executor.Executor}</p>
     *
     * @param context a {@link com.pi4j.context.Context} object.
     * @return a T object.
     * @throws com.pi4j.exception.InitializeException if any.
     */
    T initialize(Context context) throws InitializeException;

    /**
     * <p>Shutdown the lifecycle.</p>
     *
     * @param context a {@link com.pi4j.context.Context} object.
     * @return a T object.
     * @throws com.pi4j.exception.ShutdownException if any.
     */
    T shutdown(Context context) throws ShutdownException;
}

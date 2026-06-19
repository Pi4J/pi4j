package com.pi4j.event;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  ShutdownEvent.java
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

import com.pi4j.context.Context;

/**
 * {@link Event} fired when a Pi4J {@link Context} is shutting down, and delivered to
 * registered {@link ShutdownListener}s via
 * {@link ShutdownListener#onShutdown(ShutdownEvent)} (with an optional pre-notification
 * through {@link ShutdownListener#beforeShutdown(ShutdownEvent)}). It carries the
 * context being shut down as its payload.
 */
public class ShutdownEvent implements Event {
    protected final Context context;

    /**
     * Creates a shutdown event for the given Pi4J context.
     *
     * @param context the Pi4J {@link Context} that is being shut down
     */
    public ShutdownEvent(Context context){
        this.context = context;
    }
}

package com.pi4j.event;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  EventDelegate.java
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
 * Functional strategy used by an {@link EventManager} to deliver a single event to a
 * single listener. Implementations typically invoke the appropriate callback method on
 * the listener (for example {@link InitializedListener#onInitialized}), allowing the
 * generic {@link EventManager} to remain agnostic of the concrete listener and event
 * types.
 *
 * @param <LISTENER_TYPE> the listener type that receives the event
 * @param <EVENT_TYPE> the event type being delivered
 */
public interface EventDelegate<LISTENER_TYPE, EVENT_TYPE> {
    /**
     * Delivers the given event to the given listener, invoking the listener callback
     * that corresponds to the event type.
     *
     * @param listener the listener to notify
     * @param event the event to deliver to the listener
     */
    void dispatch(LISTENER_TYPE listener, EVENT_TYPE event);
}

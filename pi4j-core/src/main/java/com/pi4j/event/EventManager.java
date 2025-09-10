package com.pi4j.event;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  EventManager.java
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class EventManager<SOURCE_TYPE, LISTENER_TYPE extends Listener, EVENT_TYPE> {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final SOURCE_TYPE source;
    private final Set<LISTENER_TYPE> listeners = new CopyOnWriteArraySet<>();
    private final EventDelegate<LISTENER_TYPE,EVENT_TYPE> delegate;

    public EventManager(SOURCE_TYPE source, EventDelegate<LISTENER_TYPE,EVENT_TYPE> delegate){
        this.source = source;
        this.delegate = delegate;
    }

    /** Returns true if this event manage has at least one listener. Used to skip event creation otherwise. */
    public boolean hasListeners() {
        return !listeners.isEmpty();
    }

    public SOURCE_TYPE add(LISTENER_TYPE ... listener){
        listeners.addAll(List.of(listener));
        return this.source;
    }

    public SOURCE_TYPE remove(LISTENER_TYPE ... listener){
        listeners.removeAll(List.of(listener));
        return this.source;
    }

    public SOURCE_TYPE clear(){
        this.listeners.clear();
        return this.source;
    }

    public SOURCE_TYPE dispatch(EVENT_TYPE event){
        listeners.forEach(listener->{
            try {
                this.delegate.dispatch(listener, event);
            }
            catch (Exception e){
                logger.error(e.getMessage(), e);
            }
        });
        return this.source;
    }

    public SOURCE_TYPE dispatch(EVENT_TYPE event, EventDelegate<LISTENER_TYPE,EVENT_TYPE> delegate){
        listeners.forEach(listener->{
            try {
                delegate.dispatch(listener, event);
            }
            catch (Exception e){
                logger.error(e.getMessage(), e);
            }
        });
        return this.source;
    }

}

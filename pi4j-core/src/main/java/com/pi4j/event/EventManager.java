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
import java.util.function.Predicate;

/**
 * Reusable, thread-safe helper that {@link EventProducer}s delegate to in order to
 * manage their {@link Listener}s and dispatch {@link Event}s. It holds the set of
 * registered listeners and uses an {@link EventDelegate} to invoke the appropriate
 * callback on each listener. Listener-management and dispatch methods return the owning
 * source object so the producer can expose them fluently.
 *
 * @param <SOURCE_TYPE> the type of the owning event source, returned for method chaining
 * @param <LISTENER_TYPE> the {@link Listener} type managed by this manager
 * @param <EVENT_TYPE> the event type dispatched to the listeners
 */
public class EventManager<SOURCE_TYPE, LISTENER_TYPE extends Listener, EVENT_TYPE> {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final SOURCE_TYPE source;
    private final Set<LISTENER_TYPE> listeners = new CopyOnWriteArraySet<>();
    private final EventDelegate<LISTENER_TYPE,EVENT_TYPE> delegate;

    /**
     * Creates an event manager for the given source, using the supplied delegate as the
     * default strategy for delivering events to listeners.
     *
     * @param source the owning event source returned from the fluent methods
     * @param delegate the default {@link EventDelegate} used by {@link #dispatch(Object)}
     *                 to deliver each event to a listener
     */
    public EventManager(SOURCE_TYPE source, EventDelegate<LISTENER_TYPE,EVENT_TYPE> delegate){
        this.source = source;
        this.delegate = delegate;
    }

    /**
     * Indicates whether any listeners are currently registered. Callers can use this to
     * avoid the cost of constructing an event when there is no one to receive it.
     *
     * @return {@code true} if at least one listener is registered, otherwise {@code false}
     */
    public boolean hasListeners() {
        return !listeners.isEmpty();
    }

    /**
     * Registers one or more listeners with this manager. Duplicate listeners are ignored.
     *
     * @param listener the listener(s) to register
     * @return the owning source object for method chaining
     */
    public SOURCE_TYPE add(LISTENER_TYPE ... listener){
        listeners.addAll(List.of(listener));
        return this.source;
    }

    /**
     * Unregisters one or more previously registered listeners.
     *
     * @param listener the listener(s) to remove
     * @return the owning source object for method chaining
     */
    public SOURCE_TYPE remove(LISTENER_TYPE ... listener){
        listeners.removeAll(List.of(listener));
        return this.source;
    }

    /**
     * Removes every registered listener that matches the given condition.
     *
     * @param condition the predicate selecting which listeners to remove
     * @return the owning source object for method chaining
     */
    public SOURCE_TYPE remove(Predicate<LISTENER_TYPE> condition){
        listeners.removeIf(condition);
        return this.source;
    }

    /**
     * Removes all registered listeners from this manager.
     *
     * @return the owning source object for method chaining
     */
    public SOURCE_TYPE clear(){
        this.listeners.clear();
        return this.source;
    }

    /**
     * Dispatches the given event to every registered listener using the default
     * {@link EventDelegate} supplied at construction. Any exception thrown while
     * notifying an individual listener is caught and logged so that it does not prevent
     * the remaining listeners from being notified.
     *
     * @param event the event to deliver to all registered listeners
     * @return the owning source object for method chaining
     */
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

    /**
     * Dispatches the given event to every registered listener using the supplied
     * delegate instead of the default one, allowing a different callback to be invoked
     * for this dispatch (for example {@link ShutdownListener#beforeShutdown}). Any
     * exception thrown while notifying an individual listener is caught and logged so
     * that it does not prevent the remaining listeners from being notified.
     *
     * @param event the event to deliver to all registered listeners
     * @param delegate the {@link EventDelegate} used to deliver the event to each listener
     * @return the owning source object for method chaining
     */
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

package com.pi4j.io.gpio.digital;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  DigitalBase.java
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
import com.pi4j.event.EventDelegate;
import com.pi4j.event.EventManager;
import com.pi4j.exception.ShutdownException;
import com.pi4j.io.binding.Bindable;
import com.pi4j.io.binding.BindingDelegate;
import com.pi4j.io.binding.BindingManager;
import com.pi4j.io.binding.DigitalBinding;
import com.pi4j.io.gpio.GpioBase;

/**
 * <p>Abstract DigitalBase class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 * @param <CONFIG_TYPE>
 * @param <DIGITAL_TYPE>
 * @param <PROVIDER_TYPE>
 */
public abstract class DigitalBase<DIGITAL_TYPE extends Digital<DIGITAL_TYPE, CONFIG_TYPE, PROVIDER_TYPE>,
        CONFIG_TYPE extends DigitalConfig<CONFIG_TYPE>,
        PROVIDER_TYPE extends DigitalProvider>
        extends GpioBase<DIGITAL_TYPE, CONFIG_TYPE, PROVIDER_TYPE>
        implements Digital<DIGITAL_TYPE, CONFIG_TYPE, PROVIDER_TYPE>,
        Bindable<DIGITAL_TYPE, DigitalBinding>
{
    // internal listeners collection
    protected final EventManager<DIGITAL_TYPE, DigitalStateChangeListener, DigitalStateChangeEvent> stateChangeEventManager;

    // internal bindings collection
    protected BindingManager<DIGITAL_TYPE, DigitalBinding, DigitalStateChangeEvent> bindings;

    /**
     * <p>Constructor for DigitalBase.</p>
     *
     * @param provider a PROVIDER_TYPE object.
     * @param config a CONFIG_TYPE object.
     */
    public DigitalBase(PROVIDER_TYPE provider, CONFIG_TYPE config){
        super(provider,config);

        // create an event manager for digital state change events
        stateChangeEventManager  = new EventManager(this,
                (EventDelegate<DigitalStateChangeListener, DigitalStateChangeEvent>)
                        (listener, event) -> listener.onDigitalStateChange(event));

        // create a binding manager for digital state change events
        bindings = new BindingManager(this, (BindingDelegate<DigitalBinding, DigitalStateChangeEvent>)
                (binding, event) -> binding.process(event));
    }

    /** {@inheritDoc} */
    @Override
    public DIGITAL_TYPE addListener(DigitalStateChangeListener... listener) {
        stateChangeEventManager.add(listener);
        return (DIGITAL_TYPE)this;
    }

    /** {@inheritDoc} */
    @Override
    public DIGITAL_TYPE removeListener(DigitalStateChangeListener... listener) {
        stateChangeEventManager.remove(listener);
        return (DIGITAL_TYPE)this;
    }

    /** {@inheritDoc} */
    @Override
    public DIGITAL_TYPE bind(DigitalBinding ... binding) {
        return bindings.bind(binding);

        //bindings.addAll(List.of(binding));
        //return (DIGITAL_TYPE)this;
    }

    /** {@inheritDoc} */
    @Override
    public DIGITAL_TYPE unbind(DigitalBinding ... binding) {
        return bindings.unbind(binding);
        //bindings.removeAll(List.of(binding));
        //return (DIGITAL_TYPE)this;
    }

    public boolean hasListenersOrBindings() {
        return stateChangeEventManager.hasListeners() || bindings.hasBindings();
    }


    /**
     * Dispatch DigitalChangeEvent on digital input state changes
     *
     * @param event DigitalChangeEvent
     */
    protected void dispatch(DigitalStateChangeEvent event){
        stateChangeEventManager.dispatch(event);
        bindings.process(event);
    }

    /** {@inheritDoc} */
    @Override
    public DIGITAL_TYPE shutdown(Context context) throws ShutdownException {
        // remove all listeners
        stateChangeEventManager.clear();

        // remove all bindings
        bindings.clear();

        // return this instance
        return (DIGITAL_TYPE) this;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isOn() {
        // the default ON state is HIGH
        DigitalState onState = DigitalState.HIGH;

        // get configured ON state
        if(config().onState() != null){
            onState = config().onState();
        }

        // return TRUE if the current state matches the configured ON state
        return state().equals(onState);
    }
}

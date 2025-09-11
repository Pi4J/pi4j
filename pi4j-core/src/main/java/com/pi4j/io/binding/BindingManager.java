package com.pi4j.io.binding;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  BindingManager.java
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

public class BindingManager<SOURCE_TYPE, BINDING_TYPE extends Binding, EVENT_TYPE> implements Bindable<SOURCE_TYPE, BINDING_TYPE> {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final SOURCE_TYPE source;
    protected Set<BINDING_TYPE> bindings = new CopyOnWriteArraySet();
    protected final BindingDelegate<BINDING_TYPE,EVENT_TYPE> delegate;

    public BindingManager(SOURCE_TYPE source, BindingDelegate<BINDING_TYPE,EVENT_TYPE> delegate){
        this.source = source;
        this.delegate = delegate;
    }

    public SOURCE_TYPE clear(){
        this.bindings.clear();
        return this.source;
    }

    public SOURCE_TYPE process(EVENT_TYPE event){
        bindings.forEach(binding->{
            try {
                delegate.process(binding, event);
            }
            catch (Exception e){
                logger.error(e.getMessage(), e);
            }
        });
        return this.source;
    }

    @Override
    public SOURCE_TYPE bind(BINDING_TYPE... binding) {
        bindings.addAll(List.of(binding));
        return this.source;
    }

    @Override
    public SOURCE_TYPE unbind(BINDING_TYPE... binding) {
        bindings.removeAll(List.of(binding));
        return this.source;
    }

    public boolean hasBindings() {
        return !bindings.isEmpty();
    }
}

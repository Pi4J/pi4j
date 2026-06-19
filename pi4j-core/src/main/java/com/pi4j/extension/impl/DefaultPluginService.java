package com.pi4j.extension.impl;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  DefaultPluginService.java
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
import com.pi4j.extension.PluginService;
import com.pi4j.provider.Provider;

public class DefaultPluginService implements PluginService {

    private Context context = null;
    private PluginStore store = null;

    public static PluginService newInstance(Context context, PluginStore store){
        return new DefaultPluginService(context, store);
    }

    // private constructor
    private DefaultPluginService(Context context, PluginStore store) {
        // set local reference
        this.context = context;
        this.store = store;
    }

    @Override
    public Context context() {
        return this.context;
    }

    @Override
    public PluginService register(Provider... provider) {
        if(provider != null) {
            for (Provider p : provider){
                store.providers.add(p);
            }
        }
        return this;
    }
}

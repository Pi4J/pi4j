package com.pi4j.extension.impl;

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

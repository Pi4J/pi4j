package com.pi4j.extension.impl;

import com.pi4j.provider.Provider;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class PluginStore {
    public Set<Provider> providers = Collections.synchronizedSet(new HashSet<>());
}

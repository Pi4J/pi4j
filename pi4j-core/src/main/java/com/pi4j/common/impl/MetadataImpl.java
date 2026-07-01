package com.pi4j.common.impl;

import com.pi4j.common.Metadata;
import com.pi4j.common.Metadatum;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class MetadataImpl implements Metadata {

    private Map<String, Metadatum> metadata = Collections.synchronizedMap(new LinkedHashMap<>());

    @Override
    public int size() {
        return this.metadata.size();
    }

    @Override
    public boolean isEmpty() {
        return this.metadata.isEmpty();
    }

    @Override
    public boolean contains(String key) {
        return this.metadata.containsKey(key);
    }

    @Override
    public Metadata put(Metadatum metadatum) {
        this.metadata.put(metadatum.key(), metadatum);
        return this;
    }

    @Override
    public Metadatum remove(String key) {
        return this.metadata.remove(key);
    }

    @Override
    public Metadata clear() {
        this.metadata.clear();
        return this;
    }

    @Override
    public Metadatum get(String key) {
        return this.metadata.get(key);
    }

    @Override
    public Collection<Metadatum> all() {
        return this.metadata.values();
    }
}

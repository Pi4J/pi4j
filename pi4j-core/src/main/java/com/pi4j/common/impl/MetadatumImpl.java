package com.pi4j.common.impl;

import com.pi4j.common.Metadatum;

public class MetadatumImpl implements Metadatum {

    private String key;
    private Object value;
    private String description;

    @Override
    public MetadatumImpl key(String key) {
        this.key = key;
        return this;
    }

    @Override
    public MetadatumImpl value(Object value) {
        this.value = value;
        return this;
    }

    @Override
    public MetadatumImpl description(String description) {
        this.description = description;
        return this;
    }

    @Override
    public String key() {
        return this.key;
    }

    @Override
    public Object value() {
        return this.value;
    }

    @Override
    public String description() {
        return this.description;
    }

    @Override
    public String toString(){
        return key() + " = " + value().toString();
    }

}

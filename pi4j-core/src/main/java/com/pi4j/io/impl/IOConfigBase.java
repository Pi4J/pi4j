package com.pi4j.io.impl;

import com.pi4j.config.ConfigBase;
import com.pi4j.io.IOConfig;

import java.util.Map;

/**
 */
public abstract class IOConfigBase extends ConfigBase implements IOConfig {

    /**
     * PRIVATE CONSTRUCTOR
     */
    protected IOConfigBase(){
    }

    /**
     * PRIVATE CONSTRUCTOR
     */
    protected IOConfigBase(Map<String,String> properties){
        super(properties);
    }
}

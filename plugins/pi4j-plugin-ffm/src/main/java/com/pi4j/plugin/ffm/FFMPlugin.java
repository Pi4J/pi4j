package com.pi4j.plugin.ffm;

import com.pi4j.context.Context;
import com.pi4j.context.ContextConfig;
import com.pi4j.extension.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * Pi4J {@link Plugin} entry point for the FFM (Foreign Function &amp; Memory) native I/O backend.
 */
public class FFMPlugin implements Plugin {
    private static final Logger logger = LoggerFactory.getLogger(FFMPlugin.class);

    @Override
    public Context createContext(ContextConfig config) {
        return new FFMContext(config);
    }
}

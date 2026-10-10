package com.pi4j.extension;

import com.pi4j.context.Context;
import com.pi4j.context.ContextConfig;

/**
 * A loadable Pi4J component, typically discovered on the classpath.
 */
public interface Plugin  {
    Context createContext(ContextConfig config);

    default boolean isMock() {
        return false;
    }
}

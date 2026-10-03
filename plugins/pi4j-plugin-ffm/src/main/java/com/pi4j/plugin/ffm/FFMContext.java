package com.pi4j.plugin.ffm;

import com.pi4j.context.ContextConfig;
import com.pi4j.context.impl.DefaultContext;
import com.pi4j.io.IO;
import com.pi4j.io.IOConfig;
import com.pi4j.io.IOType;

class FFMContext extends DefaultContext {
    FFMContext(ContextConfig config) {
        super(config);
    }

    @Override
    public <I extends IO<?, ?>> I create(IOConfig config, IOType type) {
        return null;
    }
}

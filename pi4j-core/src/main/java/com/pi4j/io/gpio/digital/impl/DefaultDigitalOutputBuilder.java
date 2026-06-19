package com.pi4j.io.gpio.digital.impl;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  DefaultDigitalOutputBuilder.java
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
import com.pi4j.io.gpio.digital.*;
import com.pi4j.provider.Provider;
import com.pi4j.util.StringUtil;


public class DefaultDigitalOutputBuilder implements DigitalOutputBuilder {

    private final Context context;
    private final DigitalOutputConfigBuilder builder;

    private String providerId = null;
    private Class<? extends Provider> providerClass = null;

    /**
     * Creates a new instance of {@link DefaultDigitalOutputBuilder} with the specified context.
     *
     * @param context the context used to initialize the digital output
     * @return a new instance of {@link DigitalOutputBuilder}
     */
    public static DigitalOutputBuilder newInstance(Context context) {
        return new DefaultDigitalOutputBuilder(context);
    }

    /**
     * PRIVATE CONSTRUCTOR
     */
    protected DefaultDigitalOutputBuilder(Context context) {
        super();
        this.context = context;
        this.builder = DigitalOutputConfigBuilder.newInstance();
    }

    @Override
    public DigitalOutputBuilder id(String id) {
        this.builder.id(id);
        return this;
    }

    @Override
    public DigitalOutputBuilder name(String name) {
        this.builder.name(name);
        return this;
    }

    @Override
    public DigitalOutputBuilder description(String description) {
        this.builder.description(description);
        return this;
    }

    @Override
    public DigitalOutputBuilder address(Integer bcm) {
        this.builder.bcm(bcm);
        return this;
    }

    @Override
    public DigitalOutputBuilder shutdown(DigitalState state) {
        this.builder.shutdown(state);
        return this;
    }

    @Override
    public DigitalOutputBuilder initial(DigitalState state) {
        this.builder.initial(state);
        return this;
    }

    @Override
    public DigitalOutputBuilder provider(String providerId) {
        this.providerId = providerId;
        return this;
    }

    @Override
    public DigitalOutputBuilder provider(Class<? extends Provider> providerClass) {
        this.providerClass = providerClass;
        return this;
    }

    @Override
    public DigitalOutput build() {

        // create I/O instance config
        DigitalOutputConfig config = this.builder.build();

        if (StringUtil.isNotNullOrEmpty(this.providerId)) {
            return (DigitalOutput) context.provider(this.providerId).create(config);
        }
        if (this.providerClass != null) {
            return (DigitalOutput) context.provider(this.providerClass).create(config);
        }

        // use default digital output provider
        return context.dout().create(config);
    }
}

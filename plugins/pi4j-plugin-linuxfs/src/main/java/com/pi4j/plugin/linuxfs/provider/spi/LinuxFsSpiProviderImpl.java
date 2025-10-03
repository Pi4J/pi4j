/*
 *    * Copyright (C) 2012 - 2024 Pi4J
 *  * %%
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 * -
 *  #%L
 *  **********************************************************************
 *  ORGANIZATION  :  Pi4J
 *  PROJECT       :  Pi4J :: EXTENSION
 *  FILENAME      :  LinuxFsSpiProviderImpl.java
 *
 *  This file is part of the Pi4J project. More information about
 *  this project can be found here:  https://pi4j.com/
 *  **********************************************************************
 *  %%
 */


package com.pi4j.plugin.linuxfs.provider.spi;

import com.pi4j.boardinfo.util.BoardInfoHelper;
import com.pi4j.context.Context;
import com.pi4j.exception.ShutdownException;
import com.pi4j.io.spi.Spi;
import com.pi4j.io.spi.SpiConfig;
import com.pi4j.io.spi.SpiProvider;
import com.pi4j.io.spi.SpiProviderBase;


/**
 * @author mpilone
 * @since 10/3/24.
 */
public class LinuxFsSpiProviderImpl extends SpiProviderBase
    implements LinuxFsSpiProvider {


    public LinuxFsSpiProviderImpl() {
        this.id = ID;
        this.name = NAME;
    }

        @Override
    public int getPriority() {
        // the linux FS driver should always be higher priority
        return BoardInfoHelper.usesRP1() ? 150 : 50;
    }

    @Override
    public Spi create(SpiConfig config) {
        Spi spi = new LinuxFsSpi(this, config);

        // Is this the right place to call open? Should we have a shared spi device like the I2CBus?
        spi.open();

        this.context.registry().add(spi);
        return spi;
    }

    @Override
    public SpiProvider shutdownInternal(Context context) throws ShutdownException {

        // Is this the right place to call close?
        this.context.registry().allByType(LinuxFsSpi.class).values()
            .forEach(LinuxFsSpi::close);

        return super.shutdownInternal(context);
    }


}
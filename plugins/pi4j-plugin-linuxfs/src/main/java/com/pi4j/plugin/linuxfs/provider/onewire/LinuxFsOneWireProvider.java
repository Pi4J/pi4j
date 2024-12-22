package com.pi4j.plugin.linuxfs.provider.onewire;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: PLUGIN   :: LinuxFS I/O Providers
 * FILENAME      :  LinuxFsOneWireProvider.java
 *
 * This file is part of the Pi4J project. More information about
 * this project can be found here:  https://pi4j.com/
 * **********************************************************************
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import com.pi4j.io.onewire.OneWireProvider;
import com.pi4j.plugin.linuxfs.LinuxFsPlugin;

/**
 * The {@code LinuxFsOneWireProvider} interface defines the provider for OneWire functionality
 * based on the Linux filesystem. It serves as a bridge between the Pi4J framework and the
 * OneWire devices accessed via the Linux file system.
 */
public interface LinuxFsOneWireProvider extends OneWireProvider {

    /**
     * The name identifier for the Linux Filesystem OneWire provider.
     * This name is used for logging, configuration, and identification purposes.
     */
    String NAME = LinuxFsPlugin.ONE_WIRE_PROVIDER_NAME;

    /**
     * The unique identifier for the Linux Filesystem OneWire provider.
     * This ID is used internally by the Pi4J framework to differentiate providers.
     */
    String ID = LinuxFsPlugin.ONE_WIRE_PROVIDER_ID;

    /**
     * Creates a new instance of {@code LinuxFsOneWireProvider} with the specified
     * filesystem path for OneWire devices.
     *
     * @param oneWireFileSystemPath the file system path where the OneWire devices are located.
     * @return a new {@link LinuxFsOneWireProvider} instance configured with the specified path.
     */
    static LinuxFsOneWireProvider newInstance(String oneWireFileSystemPath) {
        return new LinuxFsOneWireProviderImpl(oneWireFileSystemPath);
    }

    /**
     * Creates a new instance of {@code LinuxFsOneWireProvider} using the default
     * filesystem path for OneWire devices defined in {@link LinuxFsPlugin#DEFAULT_ONE_WIRE_FILESYSTEM_PATH}.
     *
     * @return a new {@link LinuxFsOneWireProvider} instance configured with the default path.
     */
    static LinuxFsOneWireProvider newInstance() {
        return new LinuxFsOneWireProviderImpl(LinuxFsPlugin.DEFAULT_ONE_WIRE_FILESYSTEM_PATH);
    }
}

package com.pi4j.plugin.linuxfs.provider.onewire;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: PLUGIN   :: LinuxFS I/O Providers
 * FILENAME      :  LinuxFsOneWireProviderImpl.java
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


import com.pi4j.io.onewire.OneWire;
import com.pi4j.io.onewire.OneWireConfig;
import com.pi4j.io.onewire.OneWireProviderBase;
import com.pi4j.plugin.linuxfs.internal.LinuxOneWire;

/**
 * The {@code LinuxFsOneWireProviderImpl} class implements the {@link LinuxFsOneWireProvider} interface
 * and serves as the concrete implementation for the OneWire provider based on the Linux filesystem.
 * <p>
 * This provider manages the creation and configuration of OneWire instances that interact with
 * the Linux filesystem for hardware communication.
 */
public class LinuxFsOneWireProviderImpl extends OneWireProviderBase implements LinuxFsOneWireProvider {

    /**
     * The file system path where OneWire devices are located.
     * This path is used to initialize and manage OneWire interactions.
     */
    final String oneWireFileSystemPath;

    /**
     * Constructs a new instance of {@code LinuxFsOneWireProviderImpl} with the specified filesystem path.
     *
     * @param oneWireFileSystemPath the path in the Linux filesystem where OneWire devices are located.
     */
    public LinuxFsOneWireProviderImpl(String oneWireFileSystemPath) {
        this.id = ID; // Assign the unique provider ID
        this.name = NAME; // Assign the provider name
        this.oneWireFileSystemPath = oneWireFileSystemPath; // Store the specified file system path
    }

    /**
     * Returns the priority of this provider. Providers with lower priority values
     * are preferred during provider selection in the Pi4J framework.
     *
     * @return the priority value for this provider, default is 50.
     */
    @Override
    public int getPriority() {
        return 50;
    }

    /**
     * Creates a new {@link OneWire} instance based on the provided configuration.
     * <p>
     * This method initializes a filesystem-based OneWire instance using the provided
     * configuration and registers it with the Pi4J context registry.
     *
     * @param config the {@link OneWireConfig} containing configuration details for the OneWire instance.
     * @return a {@link OneWire} object representing the created OneWire instance.
     */
    @Override
    public OneWire create(OneWireConfig config) {
        LinuxOneWire oneWire = new LinuxOneWire(this.oneWireFileSystemPath);
        LinuxFsOneWire fsOneWire = new LinuxFsOneWire(oneWire, this, config);
        this.context.registry().add(fsOneWire);
        return fsOneWire;
    }
}


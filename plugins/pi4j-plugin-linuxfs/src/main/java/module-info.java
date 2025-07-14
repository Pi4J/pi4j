import com.pi4j.plugin.linuxfs.LinuxFsPlugin;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: PLUGIN   :: LinuxFS I/O Providers
 * FILENAME      :  module-info.java
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
module com.pi4j.plugin.linuxfs {

    // depends on SLF4J
    requires org.slf4j;

    requires com.pi4j;
    requires com.pi4j.library.linuxfs;
    requires com.sun.jna;
    requires jsch;

    exports com.pi4j.plugin.linuxfs;
    exports com.pi4j.plugin.linuxfs.provider.gpio.digital;
    exports com.pi4j.plugin.linuxfs.provider.pwm;
    exports com.pi4j.plugin.linuxfs.provider.i2c;
    exports com.pi4j.plugin.linuxfs.provider.onewire;
    exports com.pi4j.plugin.linuxfs.provider.spi;

    provides com.pi4j.extension.Plugin
            with LinuxFsPlugin;
}

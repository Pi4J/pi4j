/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  module-info.java
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
module com.pi4j {

    // for tests
    opens com.pi4j.boardinfo.definition;
    opens com.pi4j.boardinfo.model;
    opens com.pi4j.boardinfo.util;

    // depends on SLF4J
    requires org.slf4j;

    // exposed interfaces/classes
    exports com.pi4j;
    exports com.pi4j.boardinfo.definition;
    exports com.pi4j.boardinfo.model;
    exports com.pi4j.boardinfo.util;
    exports com.pi4j.common;
    exports com.pi4j.config;
    exports com.pi4j.config.exception;
    exports com.pi4j.context;
    exports com.pi4j.exception;
    exports com.pi4j.extension;
    exports com.pi4j.extension.exception;
    exports com.pi4j.event;
    exports com.pi4j.io;
    exports com.pi4j.io.binding;
    exports com.pi4j.io.gpio.analog;
    exports com.pi4j.io.gpio.digital;
    exports com.pi4j.io.exception;
    exports com.pi4j.io.group;
    exports com.pi4j.io.i2c;
    exports com.pi4j.io.pwm;
    exports com.pi4j.io.serial;
    exports com.pi4j.io.spi;
    exports com.pi4j.platform;
    exports com.pi4j.platform.exception;
    exports com.pi4j.provider;
    exports com.pi4j.provider.exception;
    exports com.pi4j.registry;
    exports com.pi4j.util;
    exports com.pi4j.boardinfo.datareader;
    opens com.pi4j.boardinfo.datareader;
    exports com.pi4j.boardinfo.util.command;
    opens com.pi4j.boardinfo.util.command;

    // extensibility service interfaces
    uses com.pi4j.extension.Plugin;
}

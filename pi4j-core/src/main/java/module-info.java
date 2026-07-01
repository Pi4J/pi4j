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
    exports com.pi4j.io.gpio;
    exports com.pi4j.io.gpio.digital;
    exports com.pi4j.io.exception;
    exports com.pi4j.io.i2c;
    exports com.pi4j.io.pwm;
    exports com.pi4j.io.spi;
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

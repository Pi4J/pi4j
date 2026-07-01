module com.pi4j.plugin.mock {
    requires com.pi4j;
    requires org.slf4j;

    uses com.pi4j.extension.Plugin;

    exports com.pi4j.plugin.mock;
    exports com.pi4j.plugin.mock.provider.gpio.digital;
    exports com.pi4j.plugin.mock.provider.pwm;
    exports com.pi4j.plugin.mock.provider.spi;
    exports com.pi4j.plugin.mock.provider.i2c;

    provides com.pi4j.extension.Plugin
        with com.pi4j.plugin.mock.MockPlugin;
}

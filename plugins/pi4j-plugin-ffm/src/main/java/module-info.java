import com.pi4j.plugin.ffm.FFMPlugin;

module com.pi4j.plugin.ffm {

    // depends on SLF4J
    requires org.slf4j;
    requires com.pi4j;

    exports com.pi4j.plugin.ffm.providers.pwm;
    exports com.pi4j.plugin.ffm.providers.spi;
    exports com.pi4j.plugin.ffm.providers.i2c;
    exports com.pi4j.plugin.ffm.providers.gpio;

    provides com.pi4j.extension.Plugin with FFMPlugin;
}

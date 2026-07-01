module com.pi4j.test {

    // Pi4J API
    requires com.pi4j;

    // SLF4J MODULES
    requires org.slf4j;
    requires org.slf4j.simple;

    uses com.pi4j.extension.Extension;
    uses com.pi4j.provider.Provider;

    // Pi4J Mock Platform and Providers
    requires com.pi4j.plugin.mock;

    // TEST
    requires com.pi4j.plugin.ffm;
    requires jdk.incubator.vector;
    // TEST END

    uses com.pi4j.plugin.mock.provider.gpio.digital.MockDigitalInput;
    uses com.pi4j.plugin.mock.provider.gpio.digital.MockDigitalInputProvider;
    uses com.pi4j.plugin.mock.provider.gpio.digital.MockDigitalOutput;
    uses com.pi4j.plugin.mock.provider.gpio.digital.MockDigitalOutputProvider;
    uses com.pi4j.plugin.mock.provider.pwm.MockPwm;
    uses com.pi4j.plugin.mock.provider.pwm.MockPwmProvider;
    uses com.pi4j.plugin.mock.provider.i2c.MockI2C;
    uses com.pi4j.plugin.mock.provider.i2c.MockI2CProvider;
    uses com.pi4j.plugin.mock.provider.spi.MockSpi;
    uses com.pi4j.plugin.mock.provider.spi.MockSpiProvider;

    exports com.pi4j.test.provider;
    exports com.pi4j.test.smoketest;
}

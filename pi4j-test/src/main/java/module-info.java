/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: TESTING  :: Unit/Integration Tests
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
    requires com.pi4j.plugin.gpiod;
    uses com.pi4j.plugin.gpiod.GpioDPlugin;
    uses com.pi4j.plugin.gpiod.provider.gpio.digital.GpioDDigitalOutput;
    uses com.pi4j.plugin.gpiod.provider.gpio.digital.GpioDDigitalOutputProvider;

    requires com.pi4j.plugin.linuxfs;
    requires com.pi4j.plugin.ffm;
    uses com.pi4j.plugin.linuxfs.LinuxFsPlugin;
    uses com.pi4j.plugin.linuxfs.provider.gpio.digital.LinuxFsDigitalOutput;
    uses com.pi4j.plugin.linuxfs.provider.gpio.digital.LinuxFsDigitalOutputProvider;
    // TEST END

    uses com.pi4j.plugin.mock.platform.MockPlatform;
    uses com.pi4j.plugin.mock.provider.gpio.analog.MockAnalogInput;
    uses com.pi4j.plugin.mock.provider.gpio.analog.MockAnalogInputProvider;
    uses com.pi4j.plugin.mock.provider.gpio.analog.MockAnalogOutput;
    uses com.pi4j.plugin.mock.provider.gpio.analog.MockAnalogOutputProvider;
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
    uses com.pi4j.plugin.mock.provider.serial.MockSerial;
    uses com.pi4j.plugin.mock.provider.serial.MockSerialProvider;

    exports com.pi4j.test.platform;
    exports com.pi4j.test.provider;
}

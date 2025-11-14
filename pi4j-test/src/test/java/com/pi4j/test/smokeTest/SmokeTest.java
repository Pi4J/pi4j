/*
 *
 * -
 *  * #%L
 *  * **********************************************************************
 *  * ORGANIZATION  :  Pi4J
 *  * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 *  * FILENAME      :  SmokeTest.java
 *  *
 *  * This file is part of the Pi4J project. More information about
 *  * this project can be found here:  https://pi4j.com/
 *  * **********************************************************************
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  * #L%
 *
 *
 */

/*
 *
 * -
 *  * #%L
 *  * **********************************************************************
 *  * ORGANIZATION  :  Pi4J
 *  * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 *  * FILENAME      :  SmokeTest.java
 *  *
 *  * This file is part of the Pi4J project. More information about
 *  * this project can be found here:  https://pi4j.com/
 *  * **********************************************************************
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  * #L%
 *
 *
 */

package com.pi4j.test.smokeTest;

import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.*;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.pwm.Pwm;
import com.pi4j.io.pwm.PwmType;
import com.pi4j.io.spi.Spi;
import com.pi4j.io.spi.SpiBus;
import com.pi4j.io.spi.SpiChipSelect;
import com.pi4j.io.spi.SpiMode;
import com.pi4j.util.Console;

import java.time.Duration;
import java.time.Instant;
import com.pi4j.util.DeferredDelay.* ;


public class SmokeTest {
    static String i2cProvider = "linuxfs-i2c";
    static String gpioOutProvider = "gpiod-digital-output";
    static String gpioInProvider = "gpiod-digital-input";
    static String pwmProvider = "linuxfs-pwm";
    static String spiProvider = "linuxfs-spi";

    static Context pi4j = null;
    int bmpI2cBus = 1;
    int bmpI2cAddr = 0x76;
    I2C i2c = null;
    SpiChipSelect bmpSpiChipSelect = SpiChipSelect.CS_0;
    SpiBus bmpSpiSpiBus = SpiBus.BUS_0;
    Spi spi = null;

    // test PWM  pin GPIO18
    int pwmChannel = 2;
    int pinGpio23 = 23;
    DigitalInput gpio23InMonitor = null;
    private static Pwm pwm = null;

    // test output
    int pinGpio24 = 24;
    int pinGpio25 = 25;
    DigitalOutput gpio24OutTest = null;
    DigitalInput gpio25InMonitor = null;

    // test input
    int pinGpio16 = 16;
    int pinGpio26 = 26;
    DigitalInput gpio16InTest = null;
    DigitalOutput gpio26OutControl = null;

    int pwmFlashes = 0;

    // test Serial
    //  | GPIO14 TX      | White  | loopback|
    //  | GPIO15 RX      | White  | loopback|


    int testNumber = 0;
    Console console = null;

    public SmokeTest(Context pi4j, Console console, int testNumber) {
        super();
        SmokeTest.pi4j = pi4j;
        this.console = console;
        this.testNumber = testNumber;

    }


    public void incPwmFlashes() {
        pwmFlashes++;
        ;
    }

    public boolean runTestCase(int testNumber) {
        boolean rval = false;
        this.console.println("enter: runTestCase number %d ", testNumber);
        switch (testNumber) {
            case 1:
                rval = testI2c();
                break;
            case 2:
                rval = testSpi();
                break;
            case 3:
                rval = testPWM();
                break;
            case 4:
                rval = testGpioIn();
                break;
            case 5:
                rval = testGpioOut();
                break;
            case 6:
                rval = testSerial();
                break;
            default:
                this.console.println("Invalid testnumber %d", testNumber);
        }
        return rval;
    }

    // individual tests

    private boolean testSpi() {
        boolean rval = false;
        createSPIDevice();
        ;
        int idValueMskBMP = 0x58;   // expected chpId value BMP280
        int idValueMskBME = 0x60;   // expected chpId value BME280
        int resetReg = 0xE0;
        int reset_cmd = 0xB6;
        int chipId = 0xD0;
        this.console.println("enter: testSpi");
        // read 0xD0 validate data equal 0x58 or 0x60
        // reset chip
        int rc = writeRegister(resetReg, reset_cmd);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int id = this.readRegister(chipId);

        console.println("Read chipId  %02x ", id);
        if ((id == idValueMskBMP) || (id == idValueMskBME)) {
            this.console.println("Correct chip ID read");
            rval = true;
        } else {
            this.console.println("Incorrect chip ID read");
        }
        this.console.println("exit: testI2c success  %b", rval);
        return rval;
    }


    public boolean testI2c() {
        boolean rval = false;
        createI2cDevicees();
        int idValueMskBMP = 0x58;   // expected chpId value BMP280
        int idValueMskBME = 0x60;   // expected chpId value BME280
        this.console.println("enter: testI2c");
        // read 0xD0 validate data equal 0x58 or 0x60
        int id = this.i2c.readRegister(0xD0);
        console.println("Read chipId  %02x ", id);
        if ((id == idValueMskBMP) || (id == idValueMskBME)) {
            this.console.println("Correct chip ID read");
            rval = true;
        } else {
            this.console.println("Incorrect chip ID read");
        }
        this.console.println("exit: testI2c success  %b", rval);
        return rval;
    }

    private boolean testPWM() {
        boolean rval = false;
        this.console.println("enter: testPWM");

        gpio23InMonitor = createDigitalInput(pinGpio23);
        gpio23InMonitor.addListener(new SmokeTest.DataInGpioListener(this));
        // pwm pin gpio18PWMTest
        pwm = createPWM(pwmChannel);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        if (pwmFlashes == 10) {
            rval = true;
        }
        this.console.println("exit: testPWM flashCount %d success  %b", pwmFlashes, rval);
        return rval;
    }

    private boolean testGpioIn() {
        boolean rval = false;
        this.console.println("enter: testGpioIn");

        gpio26OutControl = createDigitalOutput(pinGpio26);

        gpio16InTest = createDigitalInput(pinGpio16);
        // Validate monitor is LOW, test state HIGH
        if (gpio16InTest.state() == DigitalState.LOW) {
            gpio26OutControl.high();
            if (gpio16InTest.state() == DigitalState.HIGH) {
                rval = true;
            }
        } else {
            this.console.println("Initial state incorrect");
        }

        this.console.println("exit: testGpioIn success  %b", rval);
        return rval;
    }

    private boolean testGpioOut() {
        boolean rval = false;
        this.console.println("enter: testGpioOut");
        gpio24OutTest = createDigitalOutput(pinGpio24);

        gpio25InMonitor = createDigitalInput(pinGpio25);
        // Validate monitor is LOW, test state HIGH
        if (gpio25InMonitor.state() == DigitalState.LOW) {
            gpio24OutTest.high();
            if (gpio25InMonitor.state() == DigitalState.HIGH) {
                rval = true;
            }
        } else {
            this.console.println("Initial state incorrect");
        }

        this.console.println("exit: testGpioOut success  %b", rval);
        return rval;
    }

    private boolean testSerial() {
        boolean rval = false;
        this.console.println("enter: testSerial");


        this.console.println("exit: testSerial success  %b", rval);
        return rval;
    }


    private static Pwm createPWM(int channel) {
        var configPwm = Pwm.newConfigBuilder(pi4j)
            .channel(channel)
            .pwmType(PwmType.HARDWARE)
            .provider(pwmProvider)     // linuxfs-pwm   PwmFFMProvider
            .initial(50)
            .frequency(1)
            // ffm .busNumber(42)
            /* .shutdown(10) */
            .build();
        try {
            pwm = pi4j.create(configPwm);
        } catch (Exception e) {
            System.out.println("Error in createPWM " + e.getMessage());
            e.printStackTrace();
        }
        return pwm;
    }

    private void createI2cDevicees() {

        String name = "I2cBMC280";
        String id = Integer.toHexString(bmpI2cAddr);
        var i2cDeviceConfig = I2C.newConfigBuilder(pi4j)
            .bus(bmpI2cBus)
            .device(bmpI2cAddr)
            .id(id + " " + name)
            .name(name)
            .provider(i2cProvider)
            .build();
        this.i2c = pi4j.create(i2cDeviceConfig);
    }

    private void createSPIDevice() {
        var spiConfig = Spi.newConfigBuilder(pi4j)
            .id("SPI" + this.bmpSpiSpiBus + "_BMP280")
            .name("D/A converter")
            .bus(this.bmpSpiSpiBus)
            .chipSelect(this.bmpSpiChipSelect)
            .baud(Spi.DEFAULT_BAUD)    // Max 10MHz
            .mode(SpiMode.MODE_0)
            .provider(spiProvider)
            .build();
        this.spi = pi4j.create(spiConfig);
    }

    private DigitalInput createDigitalInput(int pin) {
        var inputConfig3 = DigitalInput.newConfigBuilder(pi4j)
            .bcm(pin)
            .pull(PullResistance.PULL_DOWN)
            .provider(gpioInProvider);
        return pi4j.create(inputConfig3);
    }

    private DigitalOutput createDigitalOutput(int pin) {
        var outputConfig3 = DigitalOutput.newConfigBuilder(pi4j)
            .bcm(pin)
            .initial(DigitalState.LOW)
            .shutdown(DigitalState.LOW)
            .provider(gpioOutProvider);
        return pi4j.create(outputConfig3);
    }


    public int writeRegister(int register, int data) {
        int rval = 0;
        int byteswritten = -1;
        byte[] buffer = new byte[]{(byte) (0b01111111 & register),
            (byte) data
        };
        byte[] dummy = new byte[2];
        // send read request to BMP chip via SPI channel
        byteswritten = this.spi.write(buffer);

        return (rval);
    }

    public int readRegister(int register) {
        byte[] data = new byte[]{(byte) (0b10000000 | register)};
        byte[] value = new byte[1];
        this.spi.writeThenRead(data, value);
        return value[0]; //rval);
    }


    /* Listener class        */
    private static class DataInGpioListener implements DigitalStateChangeListener {

        SmokeTest testObj = null;


        public DataInGpioListener(SmokeTest testClass) {
            testObj = testClass;
            System.out.println("DataInGpioListener ctor");
        }


        @Override
        public void onDigitalStateChange(DigitalStateChangeEvent event) {
            if (event.state() == DigitalState.HIGH) {
                System.out.println("onDigitalStateChange Pin went High");
            } else if (event.state() == DigitalState.LOW) {
                System.out.println("onDigitalStateChange Pin went Low");
                testObj.incPwmFlashes();
            } else {
                System.out.println("Strange event state  " + event.state());
            }
        }
    }
}

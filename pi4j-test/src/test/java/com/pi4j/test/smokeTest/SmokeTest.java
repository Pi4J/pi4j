package com.pi4j.test.smokeTest;

/*
 *
 *
 *  #%L
 *  **********************************************************************
 *  ORGANIZATION  :  Pi4J
 *  PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 *  FILENAME      :  SmokeTest.java
 *
 *  This file is part of the Pi4J project. More information about
 *  this project can be found here:  https://pi4j.com/
 *  **********************************************************************
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  #L%
 *
 *
 */



import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.*;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.pwm.Pwm;
import com.pi4j.io.pwm.PwmType;
import com.pi4j.io.spi.Spi;
import com.pi4j.io.spi.SpiBus;
import com.pi4j.io.spi.SpiChipSelect;
import com.pi4j.io.spi.SpiMode;

import com.pi4j.io.serial.Serial;
import com.pi4j.util.Console;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.pi4j.util.DeferredDelay.* ;


public class SmokeTest {
    static final String I2C_PROVIDER = "ffm-i2c"; //"linuxfs-i2c";
    static final String GPIO_OUT_PROVIDER = "ffm-digital-output" ; //"gpiod-digital-output";  "ffm-digital-output"
    static final String GPIO_IN_PROVIDER = "ffm-digital-input" ; // "gpiod-digital-input";
    static final String PWM_PROVIDER =  "ffm-pwm" ; //"linuxfs-pwm";
    static final String SPI_PROVIDER = "ffm-spi" ; //"linuxfs-spi";

    static Context pi4j = null;
    static final int BMP_I2C_BUS = 1;
    static final int BMP_I2C_ADDR = 0x76;
    I2C i2c = null;
    SpiChipSelect bmpSpiChipSelect = SpiChipSelect.CS_0;
    SpiBus bmpSpiSpiBus = SpiBus.BUS_0;
    Spi spi = null;

    // test PWM  pin GPIO18
    static final int PWM_CHANNEL = 2;
    static final int PIN_GPIO23 = 23;
    DigitalInput gpio23InMonitor = null;
    private static Pwm pwm = null;

    // test output
    static final int PIN_GPIO24 = 24;
    static final int PIN_GPIO25 = 25;
    DigitalOutput gpio24OutTest = null;
    DigitalInput gpio25InMonitor = null;

    // test input
    static final int PIN_GPIO16 = 16;
    static final int PIN_GPIO26 = 26;
    DigitalInput gpio16InTest = null;
    DigitalOutput gpio26OutControl = null;

    int pwmFlashes = 0;

    // test Serial
    //  | GPIO14 TX      | White  | loopback|
    //  | GPIO15 RX      | White  | loopback|


    public SmokeTest(Context pi4j) {
        super();
        SmokeTest.pi4j = pi4j;
    }


    public void incPwmFlashes() {
        pwmFlashes++;
    }


    // individual tests

    int readSpiID() {
        createSPIDevice();
        int chipId = 0xD0;
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int id = this.readRegister(chipId);

        return id;
    }


    public int readI2cID() {
        createI2cDevicees();
        // read 0xD0 validate data equal 0x58 or 0x60
        int id = this.i2c.readRegister(0xD0);
        return id;
    }

    int testPWM() throws Exception {
        gpio23InMonitor = createDigitalInput(PIN_GPIO23);
        gpio23InMonitor.addListener(new SmokeTest.DataInGpioListener(this));
        // pwm pin gpio18PWMTest
        pwm = createPWM(PWM_CHANNEL);
        pwm.on(50, 1) ;
        Thread.sleep(10000);

        return pwmFlashes;
    }

     DigitalState testGpioIn() {
         gpio26OutControl = createDigitalOutput(PIN_GPIO26);

         gpio16InTest = createDigitalInput(PIN_GPIO16);
         // Validate monitor is LOW, test state HIGH
         if (gpio16InTest.state() == DigitalState.LOW) {
             gpio26OutControl.high();
         }
             return gpio16InTest.state();

     }

     DigitalState testGpioOut() {
        gpio24OutTest = createDigitalOutput(PIN_GPIO24);

        gpio25InMonitor = createDigitalInput(PIN_GPIO25);
        // Validate monitor is LOW, test state HIGH
        if (gpio25InMonitor.state() == DigitalState.LOW) {
          gpio24OutTest.high();
        }
        return gpio25InMonitor.state() ;
    }

     boolean testSerial() {
        return false;
    }


    private static Pwm createPWM(int channel) {
     //   var chip = PwmChipUtil.getPWMChip();
        // The ffm PWM config is still being reworked
        // In the mean time hardcode what will work
        var configPwm = Pwm.newConfigBuilder(pi4j)
            .channel(0) //  ???channel)
            .pwmType(PwmType.HARDWARE)
            .provider(PWM_PROVIDER)     // linuxfs-pwm   PwmFFMProvider
            .initial(50)
            .frequency(1)
            .bus(2)   // ???? utility ?   not required linuxfs
            .shutdown(0)  //  ?????
            .build();
        pwm = pi4j.create(configPwm);
        return pwm;
    }

    private void createI2cDevicees() {

        String name = "I2cBMC280";
        String id = Integer.toHexString(BMP_I2C_ADDR);
        var i2cDeviceConfig = I2C.newConfigBuilder(pi4j)
            .bus(BMP_I2C_BUS)
            .device(BMP_I2C_ADDR)
            .id(id + " " + name)
            .name(name)
            .provider(I2C_PROVIDER)
            .build();
        this.i2c = pi4j.create(i2cDeviceConfig);
    }

    private  void createSPIDevice() {
        var spiConfig = Spi.newConfigBuilder(pi4j)
            .id("SPI" + this.bmpSpiSpiBus + "_BMP280")
            .name("D/A converter")
            .bus(this.bmpSpiSpiBus)
            .chipSelect(this.bmpSpiChipSelect)
            .baud(Spi.DEFAULT_BAUD)    // Max 10MHz
            .mode(SpiMode.MODE_0)
            .provider(SPI_PROVIDER)
            .build();
        this.spi = pi4j.create(spiConfig);
    }

    private DigitalInput createDigitalInput(int pin) {
        var inputConfig3 = DigitalInput.newConfigBuilder(pi4j)
            .bcm(pin)
            .pull(PullResistance.PULL_DOWN)
            .provider(GPIO_IN_PROVIDER);
        return pi4j.create(inputConfig3);
    }

    private DigitalOutput createDigitalOutput(int pin) {
        var outputConfig3 = DigitalOutput.newConfigBuilder(pi4j)
            .bcm(pin)
            .initial(DigitalState.LOW)
            .shutdown(DigitalState.LOW)
            .provider(GPIO_OUT_PROVIDER);
        return pi4j.create(outputConfig3);
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
        }


        @Override
        public void onDigitalStateChange(DigitalStateChangeEvent event) {
            if (event.state() == DigitalState.HIGH) {
               ; // System.out.println("onDigitalStateChange Pin went High");
            } else if (event.state() == DigitalState.LOW) {
                testObj.incPwmFlashes();
            } else {
                System.out.println("Strange event state  " + event.state());
            }
        }
    }


// TODO under construction for the testSerial   Do not review
    static public class SerialReader implements Runnable {

        private final Console console;
        private final Serial serial;

        private boolean continueReading = true;

        public SerialReader(Console console,Serial serial) {
            this.console = console;
            this.serial = serial;
        }

        public void stopReading() {
            continueReading = false;
        }

        @Override
        public void run() {
            // We use a buffered reader to handle the data received from the serial port
            BufferedReader br = new BufferedReader(new InputStreamReader(serial.getInputStream()));

            try {
                // Data from the GPS is recieved in lines
                String line = "";

                // Read data until the flag is false
                while (continueReading) {
                    // First we need to check if there is data available to read.
                    // The read() command for pigio-serial is a NON-BLOCKING call,
                    // in contrast to typical java input streams.
                    var available = serial.available();
                    if (available > 0) {
                        for (int i = 0; i < available; i++) {
                            byte b = (byte) br.read();
                            if (b < 32) {
                                // All non-string bytes are handled as line breaks
                                if (!line.isEmpty()) {
                                    // Here we should add code to parse the data to a GPS data object
                                    console.println("Data: '" + line + "'");
                                    line = "";
                                }
                            } else {
                                line += (char) b;
                            }
                        }
                    } else {
                        Thread.sleep(10);
                    }
                }
            } catch (Exception e) {
                console.println("Error reading data from serial: " + e.getMessage());
                System.out.println(e.getStackTrace());
            }
        }
    }
}

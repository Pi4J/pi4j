 package com.pi4j.test.smokeTest.provider;

/*
 *
 *
 *  #%L
 *  **********************************************************************
 *  ORGANIZATION  :  Pi4J
 *  PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 *  FILENAME      :  ProviderSmokeTest.java
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

import com.pi4j.Pi4J;
import com.pi4j.boardinfo.util.PwmChipUtil;
import com.pi4j.context.Context;
import com.pi4j.exception.Pi4JException;
import com.pi4j.io.gpio.digital.*;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.pwm.Pwm;
import com.pi4j.io.pwm.PwmType;
import com.pi4j.io.spi.Spi;
import com.pi4j.io.spi.SpiBus;
import com.pi4j.io.spi.SpiChipSelect;
import com.pi4j.io.spi.SpiMode;
import jdk.jfr.Description;
import org.junit.jupiter.api.*;

 /**
  * ProviderSmokeTest
  * Simple test of the six providers. Dependent upon
  * the wiring described in the README file.
  *
  */
 public class ProviderSmokeTest {

    static Context pi4j = null;

    static int pwmFlashes = 0 ;
    static final int PWM_CHANNEL = 2;
    static final int PIN_GPIO23 = 23;
    static final int ID_VALUE_MSK_BMP = 0x58;   // expected chpId value BMP28
    static final int ID_VALUE_MSK_BME = 0x60;   // expected chpId value BME280
    static final int PIN_GPIO16 = 16;
    static final int PIN_GPIO26 = 26;
    static final int PIN_GPIO24 = 24;
    static final int PIN_GPIO25 = 25;
    static final int BMP_I2C_BUS = 1;
    static final int BMP_I2C_ADDR = 0x76;

    static final String SPI_PROVIDER = "ffm-spi" ;
    static final String GPIO_IN_PROVIDER = "ffm-digital-input" ;
    static final String I2C_PROVIDER = "ffm-i2c";
    static final String GPIO_OUT_PROVIDER = "ffm-digital-output" ;
    static final String PWM_PROVIDER =  "ffm-pwm" ;
    static final String SERIAL_PROVIDER =  "ffm-serial";


     @BeforeEach
    public void beforeTest() {

        System.setProperty(org.slf4j.simple.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "TRACE");
        pi4j = Pi4J.newAutoContext();

    }


    @AfterEach
    public void afterTest() {
        try {
            pi4j.shutdown();
        } catch (Pi4JException e) { /* do nothing */ }
    }


    @Test
    public void testI2c() {
      
        I2C dev = createI2cBMPDevice();
        // read 0xD0 validate data equal 0x58 or 0x60
        int id = dev.readRegister(0xD0);

        Assertions.assertTrue(id == ID_VALUE_MSK_BMP || id ==ID_VALUE_MSK_BME, "TestBMP and BME ID");
    }

    @Disabled("Needs issue 552 implement writeThenRead in ffm Spi")
    @Test
    public void testSpi() {

        Spi spi = createSPIDevice();
        final int chipId = 0xD0;
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int id = this.readSpiRegister(spi, chipId);

        Assertions.assertTrue(id == ID_VALUE_MSK_BMP || id ==ID_VALUE_MSK_BME, "TestBMP and BME ID");
    }

    @Test
    public void testPWM() throws Exception {
        pwmFlashes = 0 ;
        DigitalInput gpio23InMonitor = createDigitalInput(PIN_GPIO23, PullResistance.PULL_DOWN);
        gpio23InMonitor.addListener(new ProviderSmokeTest.DataInGpioListener());
        Pwm pwm = createHwPwm(PWM_CHANNEL);
        pwm.on(50, 1) ;
        Thread.sleep(10000);  // wait 10 seconds while listener counts flashes

        Assertions.assertTrue(pwmFlashes == 10, "TestPWM flashing 10 times in 10 seconds");
    }

    @Test
    public void testGpioIn() {
        DigitalOutput gpio26OutControl = createDigitalOutput(PIN_GPIO26, DigitalState.LOW, DigitalState.LOW);
        DigitalInput gpio16InTest = createDigitalInput(PIN_GPIO16,PullResistance.PULL_DOWN);
        // Validate monitor is LOW, then set test control state HIGH
        if (gpio16InTest.state() == DigitalState.LOW) {
            gpio26OutControl.high();
        }
        DigitalState state = gpio16InTest.state();

        Assertions.assertTrue(state  ==  DigitalState.HIGH, "TestGpioIn  now HIGHs");
    }

    @DisplayName("testGpioOut() Has work-around while debugging with ffm")
    @Test
    public void testGpioOut() {

        DigitalOutput gpio24OutTest = createDigitalOutput(PIN_GPIO24, DigitalState.LOW, DigitalState.LOW);

        DigitalInput gpio25InMonitor = createDigitalInput(PIN_GPIO25, PullResistance.PULL_DOWN);
        // Validate monitor is LOW, test control state HIGH
        if (gpio25InMonitor.state() == DigitalState.LOW) {
            gpio24OutTest.high();
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        DigitalState state =  gpio25InMonitor.state() ;
        Assertions.assertTrue(state  ==  DigitalState.HIGH, "TestGpioOut  now HIGHs");
    }

    @Disabled("Requires implementation")
     @Test
    public void testSerial() {
        Assertions.assertTrue(false, "TestSerial expected read on loopback");
    }


    private I2C  createI2cBMPDevice() {
        String name = "I2cBMP280";
        String id = Integer.toHexString(BMP_I2C_ADDR);
        var i2cDeviceConfig = I2C.newConfigBuilder(pi4j)
            .bus(BMP_I2C_BUS)
            .device(BMP_I2C_ADDR)
            .id(id + " " + name)
            .name(name)
            .provider(I2C_PROVIDER)
            .build();
        I2C i2c = pi4j.create(i2cDeviceConfig);
        return  i2c;
    }
    Spi createSPIDevice() {
        SpiBus bmpSpiBus = SpiBus.BUS_0;

        var spiConfig = Spi.newConfigBuilder(pi4j)
            .id("SPI" + bmpSpiBus + "_BMP280")
            .name("Sensor")
            .bus(bmpSpiBus)
            .chipSelect(SpiChipSelect.CS_0)
            .baud(Spi.DEFAULT_BAUD)    // Max 10MHz
            .mode(SpiMode.MODE_0)
            .provider(SPI_PROVIDER)
            .build();
        Spi spi = pi4j.create(spiConfig);
        return spi;
    }

    DigitalInput createDigitalInput(int pin, PullResistance pull) {

        var inputConfig3 = DigitalInput.newConfigBuilder(pi4j)
            .bcm(pin)
            .pull(pull)
            .provider(GPIO_IN_PROVIDER);
        return pi4j.create(inputConfig3);
    }

    DigitalOutput createDigitalOutput(int pin, DigitalState initial, DigitalState shutDown) {

        var outputConfig3 = DigitalOutput.newConfigBuilder(pi4j)
            .bcm(pin)
            .initial(initial)
            .shutdown(shutDown)
            .provider(GPIO_OUT_PROVIDER);
        return pi4j.create(outputConfig3);
    }


    public int readSpiRegister(Spi spi, int register) {
        byte[] data = new byte[]{(byte) (0b10000000 | register)};
        byte[] value = new byte[1];
        spi.writeThenRead(data, value);
        return value[0];
    }

    Pwm createHwPwm(int channel) {

        var chip = PwmChipUtil.getPWMChip();
        var configPwm = Pwm.newConfigBuilder(pi4j)
            .channel(channel)
            .pwmType(PwmType.HARDWARE)
            .provider(PWM_PROVIDER)
            .initial(50)
            .frequency(1)
            .chip(chip)
            .shutdown(0)  //  ?????
            .build();
        Pwm pwm = pi4j.create(configPwm);
        return pwm;
    }


    /* Listener class        */
    private static class DataInGpioListener implements DigitalStateChangeListener {


        public DataInGpioListener() {
        }


        @Override
        public void onDigitalStateChange(DigitalStateChangeEvent event) {
            if (event.state() == DigitalState.HIGH) {
                ; // System.out.println("onDigitalStateChange Pin went High");
            } else if (event.state() == DigitalState.LOW) {
                ProviderSmokeTest.pwmFlashes ++;
            } else {
                System.out.println("Strange event state  " + event.state());
            }
        }
    }

}

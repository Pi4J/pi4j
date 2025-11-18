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

/*
 *
 *
 *  #%L
 *  **********************************************************************
 *  ORGANIZATION  :  Pi4J
 *  PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 *  FILENAME      :  GpioProviderTest.java
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
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

 /**
  * ProviderSmokeTest
  * Simple test of the six providers. Dependent upon
  * the wiring described in the README file.
  *
  */
 public class ProviderSmokeTest {

    static Context pi4j = null;

    static int pwmFlashes = 0 ;

    @BeforeAll
    public static void beforeTest() {

        System.setProperty(org.slf4j.simple.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "TRACE");
        pi4j = Pi4J.newAutoContext();

    }


    @AfterAll
    public static void afterTest() {
        try {
            pi4j.shutdown();
        } catch (Pi4JException e) { /* do nothing */ }
    }


    @Test
    public void testI2c() {
        final int idValueMskBMP = 0x58;   // expected chpId value BMP280
        final int idValueMskBME = 0x60;   // expected chpId value BME280

        I2C dev = createI2cBMPDevice();
        // read 0xD0 validate data equal 0x58 or 0x60
        int id = dev.readRegister(0xD0);

        Assertions.assertTrue(id == idValueMskBMP || id ==idValueMskBME, "TestBMP and BME ID");
    }

    @Test
    public void testSpi() {
        final int idValueMskBMP = 0x58;   // expected chpId value BMP280
        final int idValueMskBME = 0x60;   // expected chpId value BME280

        Spi spi = createSPIDevice();
        final int chipId = 0xD0;
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int id = this.readSpiRegister(spi, chipId);

        Assertions.assertTrue(id == idValueMskBMP || id ==idValueMskBME, "TestBMP and BME ID");
    }

    @Test
    public void testPWM() throws Exception {
        final int PWM_CHANNEL = 2;
        final int PIN_GPIO23 = 23;
        pwmFlashes = 0 ;
        DigitalInput gpio23InMonitor = createDigitalInput(PIN_GPIO23);
        gpio23InMonitor.addListener(new ProviderSmokeTest.DataInGpioListener());
        Pwm pwm = createPWM(PWM_CHANNEL);
        pwm.on(50, 1) ;
        Thread.sleep(10000);  // wait 10 seconds while listener counts flashes

        Assertions.assertTrue(pwmFlashes == 10, "TestPWM flashing 10 times in 10 seconds");
    }

    @Test
    public void testGpioIn() {
          final int PIN_GPIO16 = 16;
        final int PIN_GPIO26 = 26;
        DigitalOutput gpio26OutControl = createDigitalOutput(PIN_GPIO26);
        DigitalInput gpio16InTest = createDigitalInput(PIN_GPIO16);
        // Validate monitor is LOW, then set test control state HIGH
        if (gpio16InTest.state() == DigitalState.LOW) {
            gpio26OutControl.high();
        }
        DigitalState state = gpio16InTest.state();

        Assertions.assertTrue(state  ==  DigitalState.HIGH, "TestGpioIn  now HIGHs");
    }

    @Test
    public void testGpioOut() {
        final int PIN_GPIO24 = 24;
        final int PIN_GPIO25 = 25;

        DigitalOutput gpio24OutTest = createDigitalOutput(PIN_GPIO24);

        DigitalInput gpio25InMonitor = createDigitalInput(PIN_GPIO25);
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

    @Test
    public void testSerial() {
        Assertions.assertTrue(false, "TestSerial expected read on loopback");
    }


    private I2C  createI2cBMPDevice() {
        final String I2C_PROVIDER = "ffm-i2c";
        final int BMP_I2C_BUS = 1;
        final int BMP_I2C_ADDR = 0x76;
        String name = "I2cBMC280";
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
        SpiBus bmpSpiSpiBus = SpiBus.BUS_0;
        final String SPI_PROVIDER = "ffm-spi" ;

        var spiConfig = Spi.newConfigBuilder(pi4j)
            .id("SPI" + bmpSpiSpiBus + "_BMP280")
            .name("D/A converter")
            .bus(bmpSpiSpiBus)
            .chipSelect(SpiChipSelect.CS_0)
            .baud(Spi.DEFAULT_BAUD)    // Max 10MHz
            .mode(SpiMode.MODE_0)
            .provider(SPI_PROVIDER)
            .build();
        Spi spi = pi4j.create(spiConfig);
        return spi;
    }

    DigitalInput createDigitalInput(int pin) {
        final String GPIO_IN_PROVIDER = "ffm-digital-input" ;

        var inputConfig3 = DigitalInput.newConfigBuilder(pi4j)
            .bcm(pin)
            .pull(PullResistance.PULL_DOWN)
            .provider(GPIO_IN_PROVIDER);
        return pi4j.create(inputConfig3);
    }

    DigitalOutput createDigitalOutput(int pin) {
        final String GPIO_OUT_PROVIDER = "ffm-digital-output" ;

        var outputConfig3 = DigitalOutput.newConfigBuilder(pi4j)
            .bcm(pin)
            .initial(DigitalState.LOW)
            .shutdown(DigitalState.LOW)
            .provider(GPIO_OUT_PROVIDER);
        return pi4j.create(outputConfig3);
    }


    public int readSpiRegister(Spi spi, int register) {
        byte[] data = new byte[]{(byte) (0b10000000 | register)};
        byte[] value = new byte[1];
        spi.writeThenRead(data, value);
        return value[0];
    }

    Pwm createPWM(int channel) {
        final String PWM_PROVIDER =  "ffm-pwm" ;

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

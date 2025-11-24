package com.pi4j.test;/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: TESTING  :: Unit/Integration Tests
 * FILENAME      :  Main.java
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

import com.pi4j.Pi4J;
import com.pi4j.boardinfo.util.PwmChipUtil;
import com.pi4j.context.Context;
import com.pi4j.io.IOType;
import com.pi4j.io.gpio.digital.*;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.pwm.Pwm;
import com.pi4j.io.pwm.PwmType;
import com.pi4j.io.spi.Spi;
import com.pi4j.io.spi.SpiBus;
import com.pi4j.io.spi.SpiChipSelect;
import com.pi4j.io.spi.SpiMode;
/* import com.pi4j.plugin.ffm.providers.gpio.DigitalInputFFMProviderImpl;
import com.pi4j.plugin.ffm.providers.gpio.DigitalOutputFFMProviderImpl;
import com.pi4j.plugin.ffm.providers.i2c.I2CFFMProviderImpl;
import com.pi4j.plugin.ffm.providers.pwm.PwmFFMProviderImpl;
import com.pi4j.plugin.ffm.providers.serial.SerialFFMProviderImpl;
import com.pi4j.plugin.ffm.providers.spi.SpiFFMProviderImpl;  */

import com.pi4j.util.Console;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * <p>Main class.</p>
 * Simple test of the six providers. Dependent upon
 * the wiring described in the README file.
 *
 */


public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final int PWM_CHANNEL = 2;
    private static final int PIN_GPIO23 = 23;
    private static final int ID_VALUE_MSK_BMP = 0x58;   // expected chpId value BMP28
    private static final int ID_VALUE_MSK_BME = 0x60;   // expected chpId value BME280
    private static final int PIN_GPIO16 = 16;
    private static final int PIN_GPIO26 = 26;
    private static final int PIN_GPIO24 = 24;
    private static final int PIN_GPIO25 = 25;
    private static final int BMP_I2C_BUS = 1;
    private static final int BMP_I2C_ADDR = 0x76;
    private static final String FFM_PROVIDER = "ffm";
    private static final String LINUXFS_PROVIDER = "linuxfs";
    private static final String SPI_PROVIDER = "linuxfs-spi"; //"ffm-spi";
    private static final String GPIO_IN_PROVIDER = "linuxfs-digital-input"; //"ffm-digital-input";
    private static final String I2C_PROVIDER =  "linuxfs-i2c";//"ffm-i2c";
    private static final String GPIO_OUT_PROVIDER = "linuxfs-digital-output"; //"linuxfs-digital-output"; //"ffm-digital-output";
    private static final String PWM_PROVIDER = "linuxfs-pwm"; //"ffm-pwm";
    private static final String SERIAL_PROVIDER = "ffm-serial";
    static Context pi4j = null;
    private static final Console console = null;
    private static int pwmFlashes = 0;

    /**
     * <p>Constructor for Main.</p>
     */
    public Main() {
    }

    /**
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     */
    public static void main(String[] args) throws Exception {

        System.setProperty(org.slf4j.simple.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "TRACE");

        // Initialize Pi4J with an auto context
        // An auto context includes AUTO-DETECT BINDINGS enabled
        // which will load all detected Pi4J extension libraries
        // (Platforms and Providers) in the class path


        /* Plan the ContextUtil class to create the linuxfs or ffm provider
        as requested and return appropriate provider names by IO-TYPE
        */
        pi4j = Pi4J.newAutoContext();     //  remove var

  /*    pi4j = Pi4J.newContextBuilder()
            .add(new DigitalOutputFFMProviderImpl())
            .add(new DigitalInputFFMProviderImpl())
            .add(new I2CFFMProviderImpl())
            .add(new SpiFFMProviderImpl())
            .add(new PwmFFMProviderImpl())
            .add(new SerialFFMProviderImpl())
            .build();  */
        // create About class instance
        About about = new About();
        about.enumerateProviders(pi4j);
        about.enumeratePlatforms(pi4j);
        about.describeDefaultPlatform(pi4j);
        for (var ioType : IOType.values()) {
            about.enumerateProviders(pi4j, ioType);
        }
        Console console = new Console();
        logger.info("==============================================================");
        logger.info("startup  Main ");
        logger.info("==============================================================");

        String useProvider = FFM_PROVIDER;
        String helpString = " parms:   -p  linuxfs  ffm   -h help \n ";
        for (int i = 0; i < args.length; i++) {
            String o = args[i];
            if (o.contentEquals("-p")) {
                String a = args[i + 1];
                if (a.equalsIgnoreCase(FFM_PROVIDER)) {
                    useProvider = FFM_PROVIDER;
                } else if (a.equalsIgnoreCase(LINUXFS_PROVIDER)) {
                    useProvider = LINUXFS_PROVIDER;
                } else {
                    logger.info("Invalid value for parm -p ");
                    logger.info(helpString);
                    System.exit(30);
                }
                i++;
            } else if (o.contentEquals("-h")) {
                logger.info(helpString);
                System.exit(31);
            } else {
                logger.info("  !!! Invalid Parm " + args);
                logger.info(helpString);
                System.exit(32);
            }

        }
        String overResult = "";

        logger.info("Run all tests");
        // testObj = new SmokeTest(pi4j, console, testNumber);
        overResult += "\n Test result   \n";
        boolean result = testI2c();
        overResult += String.format(" Test  %s  Result %b  \n", "testI2C    ", result);

        // result = testSpi();
        overResult += String.format(" Test  %s  Result %b  \n", "testSpi    ", result);

        result = testPWM();
        overResult += String.format(" Test  %s  Result %b  \n", "testPWM    ", result);

        result = testGpioIn();
        overResult += String.format(" Test  %s  Result %b  \n", "testGpioIn ", result);

        result = testGpioOut();
        overResult += String.format(" Test  %s  Result %b  \n", "testGpioOut", result);

        result = testSerial();
        overResult += String.format(" Test  %s  Result %b  \n", "testSerial ", result);

        logger.info(overResult);
        pi4j.shutdown();
    }

    private static boolean testI2c() {
        I2C dev = createI2cBMPDevice();
        // read 0xD0 validate data equal 0x58 or 0x60
        int id = dev.readRegister(0xD0);

        return (id == ID_VALUE_MSK_BMP || id == ID_VALUE_MSK_BME);
    }


    private static boolean testSpi() {

        Spi spi = createSPIDevice();
        final int chipId = 0xD0;
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int id = readSpiRegister(spi, chipId);

        return (id == ID_VALUE_MSK_BMP || id == ID_VALUE_MSK_BME);
    }

    private static boolean testPWM() throws Exception {
        pwmFlashes = 0;
        DigitalInput gpio23InMonitor = createDigitalInput(PIN_GPIO23, PullResistance.PULL_DOWN);
        gpio23InMonitor.addListener(new Main.DataInGpioListener());
        Pwm pwm = createHwPwm(PWM_CHANNEL);
        pwm.on(50, 1);
        Thread.sleep(10000);  // wait 10 seconds while listener counts flashes

        return (pwmFlashes == 10);
    }

    private static boolean testGpioIn() {
        DigitalOutput gpio26OutControl = createDigitalOutput(PIN_GPIO26, DigitalState.LOW, DigitalState.LOW);
        DigitalInput gpio16InTest = createDigitalInput(PIN_GPIO16, PullResistance.PULL_DOWN);
        // Validate monitor is LOW, then set test control state HIGH
        if (gpio16InTest.state() == DigitalState.LOW) {
            gpio26OutControl.high();
        }
        DigitalState state = gpio16InTest.state();

        return (state == DigitalState.HIGH);
    }

    private static boolean testGpioOut() {

        DigitalOutput gpio24OutTest = createDigitalOutput(PIN_GPIO24, DigitalState.LOW, DigitalState.LOW);

        DigitalInput gpio25InMonitor = createDigitalInput(PIN_GPIO25, PullResistance.PULL_DOWN);
        // Validate monitor is LOW, test control state HIGH
        if (gpio25InMonitor.state() == DigitalState.LOW) {
            gpio24OutTest.high();
        }
        logger.info("Workaround to debug ffm");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        DigitalState state = gpio25InMonitor.state();
        return (state == DigitalState.HIGH);
    }

    private static boolean testSerial() {

        return false;
    }


    private static I2C createI2cBMPDevice() {
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
        return i2c;
    }

    private static Spi createSPIDevice() {
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

    private static DigitalInput createDigitalInput(int pin, PullResistance pull) {

        var inputConfig3 = DigitalInput.newConfigBuilder(pi4j)
            .bcm(pin)
            .pull(pull)
            .provider(GPIO_IN_PROVIDER);
        return pi4j.create(inputConfig3);
    }

    private static DigitalOutput createDigitalOutput(int pin, DigitalState initial, DigitalState shutDown) {

        var outputConfig3 = DigitalOutput.newConfigBuilder(pi4j)
            .bcm(pin)
            .initial(initial)
            .shutdown(shutDown)
            .provider(GPIO_OUT_PROVIDER);
        return pi4j.create(outputConfig3);
    }


    private static int readSpiRegister(Spi spi, int register) {
        byte[] data = new byte[]{(byte) (0b10000000 | register)};
        byte[] value = new byte[1];
        spi.writeThenRead(data, value);
        return value[0];
    }

    private static Pwm createHwPwm(int channel) {

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
                // System.out.println("onDigitalStateChange Pin went High");
            } else if (event.state() == DigitalState.LOW) {
                logger.info("PWM flashed");
                Main.pwmFlashes++;
            } else {
                logger.info("Strange event state  " + event.state());
            }
        }
    }


}

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
import com.pi4j.io.serial.*;
import com.pi4j.io.spi.Spi;
import com.pi4j.io.spi.SpiBus;
import com.pi4j.io.spi.SpiChipSelect;
import com.pi4j.io.spi.SpiMode;
import com.pi4j.plugin.ffm.providers.gpio.DigitalInputFFMProviderImpl;
import com.pi4j.plugin.ffm.providers.gpio.DigitalOutputFFMProviderImpl;
import com.pi4j.plugin.ffm.providers.i2c.I2CFFMProviderImpl;
import com.pi4j.plugin.ffm.providers.pwm.PwmFFMProviderImpl;
import com.pi4j.plugin.ffm.providers.serial.SerialFFMProviderImpl;
import com.pi4j.plugin.ffm.providers.spi.SpiFFMProviderImpl;
import com.pi4j.plugin.gpiod.provider.gpio.digital.GpioDDigitalInputProvider;
import com.pi4j.plugin.gpiod.provider.gpio.digital.GpioDDigitalOutputProvider;
import com.pi4j.plugin.linuxfs.provider.i2c.LinuxFsI2CProvider;
import com.pi4j.plugin.linuxfs.provider.pwm.LinuxFsPwmProvider;
import com.pi4j.plugin.linuxfs.provider.spi.LinuxFsSpiProvider;
import com.pi4j.util.Console;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * <p>Main class.</p>
 * Simple test of the six providers. Dependent upon the wiring described in the README file.
 *
 */
public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final int PWM_CHANNEL = 2;
    private static final int ID_VALUE_MSK_BMP = 0x58;   // expected chpId value BMP28
    private static final int ID_VALUE_MSK_BME = 0x60;   // expected chpId value BME280
    private static final int BMP_I2C_BUS = 1;
    private static final int BMP_I2C_ADDR = 0x76;
    private static final String FFM_PROVIDER = "ffm";
    private static final String LINUXFS_PROVIDER = "linuxfs";

    static Context pi4j = null;
    static ProviderContext pc = null;
    private static final Console console = null;
    private static int pwmFlashes = 0;
    public static String DEFAULT_PWM_FILESYSTEM_PATH = "/sys/class/pwm";

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
        //   pi4j = Pi4J.newAutoContext();     //  remove var

        logger.info("==============================================================");
        logger.info("startup  Main ");
        logger.info("==============================================================");

        String useProvider = LINUXFS_PROVIDER;
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
        pc = new ProviderContext(useProvider);
        pi4j = pc.getContext();
        // create About class instance
        About about = new About();
        about.enumerateProviders(pi4j);
        about.enumeratePlatforms(pi4j);
        about.describeDefaultPlatform(pi4j);
        for (var ioType : IOType.values()) {
            about.enumerateProviders(pi4j, ioType);
        }

        String overResult = "";

        logger.info("Run all tests");
        // testObj = new SmokeTest(pi4j, console, testNumber);
        overResult += "\n Test result   \n";
        boolean result = testI2c();
        overResult += String.format(" Test  %s  Result %b  \n", "testI2C    ", result);

        result = testSpi();
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
        logger.info("Enter: testI2c");
        I2C dev = createI2cBMPDevice();
        // read 0xD0 validate data equal 0x58 or 0x60
        int id = dev.readRegister(0xD0);
        logger.info("Exit: testI2c");
        return (id == ID_VALUE_MSK_BMP || id == ID_VALUE_MSK_BME);
    }

    private static boolean testSpi() {
        logger.info("Enter: testSpi");

        if (pc.getProviderGroupName().equals(FFM_PROVIDER)) {
            logger.info("SKIP test, no interface in FFM");
            return true ;
        }
        Spi spi = createSPIDevice();
        final int chipId = 0xD0;
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int id = readSpiRegister(spi, chipId);
        logger.info("Exit: testSpi");
        return (id == ID_VALUE_MSK_BMP || id == ID_VALUE_MSK_BME);
    }

    private static boolean testPWM() throws Exception {
        logger.info("Enter: testPWM");
        pwmFlashes = 0;
        DigitalInput gpioInMonitor = createDigitalInput(23, PullResistance.PULL_DOWN);
        gpioInMonitor.addListener(new Main.DataInGpioListener());
        Pwm pwm = createHwPwm(PWM_CHANNEL);
        pwm.on(50, 1);
        Thread.sleep(10000);  // wait 10 seconds while listener counts flashes
        pwm.off();
        logger.info("Exit: testPWM");
        return (pwmFlashes == 10);
    }

    private static boolean testGpioIn() {
        logger.info("Enter: testGpioIn");
        DigitalOutput gpioOutControl = createDigitalOutput(26, DigitalState.LOW, DigitalState.LOW);
        DigitalInput gpioInTest = createDigitalInput(16, PullResistance.PULL_DOWN);
        // Validate monitor is LOW, then set test control state HIGH
        if (gpioInTest.state() == DigitalState.LOW) {
            gpioOutControl.high();
        }
        DigitalState state = gpioInTest.state();
        logger.info("Exit: testGpioIn");

        return (state == DigitalState.HIGH);
    }

    private static boolean testGpioOut() {
        logger.info("Enter: testGpioOut");
        DigitalOutput gpioOutTest = createDigitalOutput(24, DigitalState.LOW, DigitalState.LOW);
        DigitalInput gpioInMonitor = createDigitalInput(25, PullResistance.PULL_DOWN);
        // Validate monitor is LOW, test control state HIGH
        if (gpioInMonitor.state() == DigitalState.LOW) {
            gpioOutTest.high();
        }
        DigitalState state = gpioInMonitor.state();
        logger.info("Exit: testGpioOut");

        return (state == DigitalState.HIGH);
    }

    private static boolean testSerial()
    {
        logger.info("Enter; testSerial ");
        if (pc.getProviderGroupName().equals(LINUXFS_PROVIDER)) {
            logger.info("SKIP test, not in LINUXFS");
            return true ;
        }
        final SerialReader[] serialReader = new SerialReader[1];
        String testData = "serial_test serial_test serial_test serial_test  " ;
        Serial txPort = createSerialDevice();
        txPort.open();;
        logger.info("about to create runnable");
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                logger.info("BUG, isOpen not implemented...Waiting till serial port is open");
                while (!txPort.isOpen()) {
                    try {
                        Thread.sleep(1000);
                        logger.info("retry...");
                        break;   //  bug work around
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

                //opened now!
                logger.info("serial port is open!");

                // Start a thread to handle the incoming data from the serial port
                serialReader[0] = new Main.SerialReader(txPort);
                Thread serialReaderThread = new Thread(serialReader[0], "SerialReader");
                serialReaderThread.setDaemon(true);
                serialReaderThread.start();

            }
        };
        logger.info("about to start runnable");
        runnable.run();
        for (int i = 0 ; i <10; i++) {
            txPort.write(testData);
         }
        // allow time for the serail reader to process incoming data
         try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        serialReader[0].stopReading();
        String read = serialReader[0].getReadData();
        return (read.indexOf(testData) > -1) ? true : false;
  }

    private static I2C createI2cBMPDevice() {
        String name = "I2cBMP280";
        String id = Integer.toHexString(BMP_I2C_ADDR);
        var i2cDeviceConfig = I2C
            .newConfigBuilder(pi4j)
            .bus(BMP_I2C_BUS)
            .device(BMP_I2C_ADDR)
            .id(id + " " + name)
            .name(name)
            .provider(pc.getI2cName())
            .build();
        I2C i2c = pi4j.create(i2cDeviceConfig);
        return i2c;
    }

    private static Serial createSerialDevice(){

        //Object StopBits;
        Serial serial = pi4j.create(Serial.newConfigBuilder(pi4j)
            .baud(Baud._4800)
            .dataBits_8()
            .parity(Parity.NONE)
            .stopBits(StopBits._1)
            .flowControl(FlowControl.NONE)
            .id("smokeTest port")
            .port("/dev/ttyAMA0")       //serial0")    // /dev/ttyAMA0  /dev/ttyS0
            .provider(pc.getSerialName())
            .build());


        return serial;
    }
    private static Spi createSPIDevice() {
        SpiBus bmpSpiBus = SpiBus.BUS_0;

        var spiConfig = Spi
            .newConfigBuilder(pi4j)
            .id("SPI" + bmpSpiBus + "_BMP280")
            .name("Sensor")
            .bus(bmpSpiBus)
            .chipSelect(SpiChipSelect.CS_0)
            .baud(Spi.DEFAULT_BAUD)    // Max 10MHz
            .mode(SpiMode.MODE_0)
            .provider(pc.getSpiName())
            .build();
        Spi spi = pi4j.create(spiConfig);
        return spi;
    }

    private static DigitalInput createDigitalInput(int bcm, PullResistance pull) {

        var inputConfig3 = DigitalInput.newConfigBuilder(pi4j).bcm(bcm).pull(pull).provider(pc.getInPinName());
        return pi4j.create(inputConfig3);
    }

    private static DigitalOutput createDigitalOutput(int bcm, DigitalState initial, DigitalState shutDown) {
        var outputConfig3 = DigitalOutput
            .newConfigBuilder(pi4j)
            .bcm(bcm)
            .initial(initial)
            .shutdown(shutDown)
            .provider(pc.getOutPinName());
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
        var configPwm = Pwm
            .newConfigBuilder(pi4j)
            .channel(channel)
            .pwmType(PwmType.HARDWARE)
            .provider(pc.getPwmName())
            .initial(50)
            .frequency(1)
            .chip(chip)
            .shutdown(0)
            .build();
        Pwm pwm = pi4j.create(configPwm);
        return pwm;
    }

    /**
     * Class ProviderContext
     * Will create a new context with the designated providers classes
     *
     *
     */
    private static class ProviderContext {
        Context pi4j = null;
        String group = null;

        ProviderContext() {
            pi4j = Pi4J.newAutoContext();
            group = LINUXFS_PROVIDER;
        }

        /**
         *
         * @param argGroup Identifies which set of providers to create
         */
        ProviderContext(String argGroup) {
            group = argGroup;

            if (group == LINUXFS_PROVIDER) {
                String pwmFileSystemPath = DEFAULT_PWM_FILESYSTEM_PATH;
                pi4j = Pi4J.newContextBuilder().add(LinuxFsI2CProvider.newInstance())
                    .add(GpioDDigitalInputProvider.newInstance())
                    .add(GpioDDigitalOutputProvider.newInstance())
                    .add(LinuxFsPwmProvider.newInstance(pwmFileSystemPath))
                    .add(LinuxFsI2CProvider.newInstance())
                    .add(LinuxFsSpiProvider.newInstance())
                    .build();
                i2cName = "linuxfs-i2c";
                spiName = "linuxfs-spi";
                pwmName = "linuxfs-pwm";
                outPinName = "gpiod-digital-output";
                inPinName = "gpiod-digital-input";
                serialName = "NONE-serial";
            } else if (group == FFM_PROVIDER) {
                pi4j = Pi4J.newContextBuilder()
                    .add(new DigitalOutputFFMProviderImpl())
                    .add(new DigitalInputFFMProviderImpl())
                    .add(new I2CFFMProviderImpl())
                    .add(new SpiFFMProviderImpl())
                    .add(new PwmFFMProviderImpl())
                    .add(new SerialFFMProviderImpl())
                    .build();
                i2cName = "ffm-i2c";
                spiName = "ffm-spi";
                pwmName = "ffm-pwm";
                outPinName = "ffm-digital-output";
                inPinName = "ffm-digital-input";
                serialName = "ffm-serial";
            }
        }

        private String getProviderGroupName() { return group ;}

        private Context getContext() {
            return pi4j;
        }

        private String i2cName = "";
        private String spiName = "";
        private String pwmName = "";
        private String outPinName = "";
        private String inPinName = "";
        private String serialName = "";

        private String getI2cName() {
            return i2cName;
        }

        private String getSpiName() {
            return spiName;
        }

        private String getPwmName() {
            return pwmName;
        }

        private String getOutPinName() {
            return outPinName;
        }

        private String getInPinName() {
            return inPinName;
        }

        private String getSerialName() {
            return serialName;
        }

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

    private static class SerialReader implements Runnable {

     private final Serial serial;
    private String line = "";
    private boolean continueReading = true;


    public SerialReader(Serial serial) {
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
               // Read data until the flag is false
                while (continueReading) {
                    // First we need to check if there is data available to read.
                    var available = serial.available();
                    if (available > 0) {
                        for (int i = 0; i < available; i++) {
                            byte b = (byte) br.read();
                            if (b < 32) {
                                // All non-string bytes are ignored
                                ;
                                } else {
                                    line += (char) b;
                                    //logger.info("line: '" + line + "'");
                                }
                        }
                    } else {
                        Thread.sleep(10);
                    }
                }
            } catch (Exception e) {
                logger.info("Error reading data from serial: " + e.getMessage());
                System.out.println(e.getStackTrace());
            }
        }

        private String getReadData(){
            return line;
        }
    }
}

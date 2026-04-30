# Pi4J Smoke Test

Testing the full Pi4J library is hard because it requires real hardware devices to validate all interactions. This test project is intended to provide a simple way to verify that the Pi4J library is working correctly on a Raspberry Pi with a few simple devices.

## Main Test

The `src/main/java/com/pi4j/test/Main.java` class verifies that the providers can be loaded and used. The use is very basic, but just enough to know it worked. 

## Build and Run

In top directory
``` text
mvn clean install -Pnative
cd pi4j-test/target/distribution
./runSmoketest.sh 
```

Usage parms:

```text
-p newautocontext (default), linuxfs, or ffm
-h help
```

## Wiring

Two BMP280 (air pressure and temperature) or BME280 (air pressure, temperature, and humidity) are used as they are available as PCBs with both I2C and SPI interfaces. Some other wiring is added to connect GPIOs to each other, to test different types of communication. 

<!-- Full image URL as this is used as a source for a page on pi4j.com -->
![Wiring diagram](https://github.com/Pi4J/pi4j/raw/develop/pi4j-test/wiring/wiring-diagram.png)

**Depending on the type of BMP/BME-sensors, the actual wiring may look a bit different**. Use the tables below to define the correct connections for your components.

<!-- Full image URL as this is used as source for a page on pi4j.com -->
![Example setup](https://github.com/Pi4J/pi4j/raw/develop/pi4j-test/wiring/test-setup.jpg)

![Example setup](https://github.com/Pi4J/pi4j/raw/develop/pi4j-test/wiring/test-setup-side-1.jpg)

![Example setup](https://github.com/Pi4J/pi4j/raw/develop/pi4j-test/wiring/test-setup-side-2.jpg)

Used for the wiring diagram:

* Fritzing
* [Adafruit T-cobbler](https://github.com/adafruit/Fritzing-Library/blob/master/parts/Adafruit%20T-Cobbler%20Plus.fzpz)

### Components

* Breadboard
* T-cobbler plugged into a prototype board
* Two sensors, BMP280 or BME280, mounted on PCB with I2C and SPI
* M-M breadboard wires

#### Buying Examples

* [Amazon: T-cobbler, 2 sets](https://www.amazon.com/Quluxe-Breakout-Expansion-Assembled-Raspberry/dp/B08D3S6FGH/?th=1)
* [Amazon: Breadboard, T-cobbler, Breadboard, 65pcs wires](https://www.amazon.com/LK-COKOINO-Raspberry-Solderless-Breadboard/dp/B08B4SHS18/)
* [Amazon: BME280, 3-pack](https://www.amazon.com.be/dp/B08BC2NGVV?ref=ppx_yo2ov_dt_b_fed_asin_title)
* [Sparkfun: BME280](https://www.sparkfun.com/sparkfun-atmospheric-sensor-breakout-bme280.html)

#### Datasheets

* [Bosch BMP280 Digital Pressure Sensor](https://www.bosch-sensortec.com/media/boschsensortec/downloads/datasheets/bst-bmp280-ds001.pdf)
* [Bosch BME280 Combined humidity and pressure sensor](https://www.bosch-sensortec.com/media/boschsensortec/downloads/datasheets/bst-bme280-ds002.pdf)

### Sensor Wiring

The I2C and SPI BMP280 charts refer to connection points on the breakout board, 
but your sensor may use different letter designations. Also, there are BMP sensors available with I2C **and** SPI interfaces or **only one** of the interfaces, read the fine print if you order these sensors.

#### I2C

| RPi Pin | Number | BCM | Color     | Sensor                |
|:--------|:------:|:---:|:----------|:----------------------|
| 3.3V    | 1      |  -  | Red       | Vin or Vcc or 3.3V    |
| Gnd     | 9      |  -  | Black     | Gnd                   |
| SDA     | 3      |  2  | Orange    | SDA                   |
| SCL     | 5      |  3  | Yellow    | SCK or SCL            |
| 3.3v    | 1      |  -  | Red       | CS if needed, see (*) |   

(*): 3.3v connection to CS. If the sensor supports both the SPI and I2C interface, the CS pin controls the I2C interface enablement. From the datasheet: _**Interface selection is done automatically based on CSB (chip select) status. If CSB is connected
  to VDDIO, the I²C interface is active.**_

#### SPI Using a 4-wires Connection

| RPi Pin | Number | BCM | Color   | Sensor             |
|:--------|:------:|:---:|:--------|:-------------------|
| MOSI    |   19   | 10  | Blue    | SDA or SDI         |
| MISO    |   21   |  9  | Purple  | SDO                |
| SCLK    |   23   | 11  | Green   | SCK or SCL         |
| SPI CE0 |   24   |  8  | Yellow  | CS                 |
| Gnd     |   9    |  -  | Black   | GND                |
| 3.3V    |   1    |  -  | Red     | Vin or Vcc or 3.3V |

### Direct Wiring

The PWM, input, output, and debounce connections are M-M jumpers between T-cobbler pins.

| Test      | From RPi Pin | Number | BCM | To RPi Pin | Number | BCM | Color | LED |
|:----------|:-------------|:------:|:---:|:-----------|:------:|:---:|:------|-----|
| PWM       | PWM0         |   12   | 18  | GPIO       |   16   | 23  | Green | 1   |
| Output    | GPIO         |   18   | 24  | GPIO       |   22   | 25  | Blue  | 2   |
| Input     | GPIO         |   36   | 16  | GPIO       |   37   | 26  | White | 3   |
| Debounce  | GPIO         |   15   | 22  | GPIO       |   13   | 27  | Grey  | 4   |

Only needed for `DigitalInputDebounceMonitorTestCase` (not added to the wiring diagram and pictures).

| Test      | From RPi Pin | Number | BCM  | To RPi Pin | Number | BCM | Color | LED |
|:----------|:-------------|:------:|:----:|:-----------|:------:|:---:|:------|-----|
| Debounce  | GPIO         | 32     | 12   | GPIO       |   35   | 19  | Brown | 5   |
|           |              |        |      | GPIO       |   38   | 20  | LOGIC | 6   |
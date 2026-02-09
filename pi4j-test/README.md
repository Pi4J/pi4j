# Pi4J Smoke Test

Testing the full Pi4J library is hard because it requires real hardware devices to validate all interactions. This test project is intended to provide a simple way to verify that the Pi4J library is working correctly on a Raspberry Pi with a few simple devices.

## Main Test

The `src/main/java/com/pi4j/test/Main.java` class verifies that the providers can be loaded and used. The use is very basic, but just enough to know it worked. 

Usage parms:

```text
-p newautocontext (default), linuxfs, or ffm
-h help
```

## Wiring

Two BMP280 (air pressure and temperature) or BME280 (air pressure, temperature, and humidity) are used as they are available as PCBs with both I2C and SPI interfaces. Some other wiring is added to connect GPIOs to each other, to test different types of communication. 

<!-- Full image URL as this is used as source for a page on pi4j.com -->
![Wiring diagram](https://github.com/Pi4J/pi4j/raw/develop/pi4j-test/wiring/wiring-diagram.png)

Depending on the type of BMP-sensors, the actual wiring may look a bit different. Use the tables below for correct connections.

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

Buying examples:

* [Amazon: T-cobbler, 2 sets](https://www.amazon.com/Quluxe-Breakout-Expansion-Assembled-Raspberry/dp/B08D3S6FGH/?th=1)
* [Amazon: Breadboard, T-cobbler, Breadboard, 65pcs wires](https://www.amazon.com/LK-COKOINO-Raspberry-Solderless-Breadboard/dp/B08B4SHS18/)
* [Amazon: BME280, 3-pack](https://www.amazon.com.be/dp/B08BC2NGVV?ref=ppx_yo2ov_dt_b_fed_asin_title)
* [Sparkfun: BME280](https://www.sparkfun.com/sparkfun-atmospheric-sensor-breakout-bme280.html)

### Sensor Wiring

The I2C and SPI BMP280 charts refers to connection points on the breakout board
but your sensor may use different letter designations. Also, the are BMP sensors available with I2C and SPI interfaces or only one of the interfaces, read the fine print if you order these sensors.

**I2C**

| RPi Pin | Number | BCM | Color     | Sensor     |
|:--------|:------:|:---:|:----------|:-----------|
| 3.3V    | 1      |  -  | Red       | Vin or Vcc or 3.3V        |
| Gnd     | 9      |  -  | Black     | Gnd        |
| SDA     | 3      |  2  | Orange    | SDA        |
| SCL     | 5      |  3  | Yellow    | SCK or SCL |
| 3.3v    | 1      |  -  | Red       | CS         |

**SPI** connection using 4-wire SPI

| RPi Pin | Number | BCM | Color   | Sensor             |
|:--------|:------:|:---:|:--------|:-------------------|
| MOSI    |   19   | 10  | Blue    | SDA or SDI         |
| MISO    |   21   |  9  | Purple  | SDO                |
| SCLK    |   23   | 11  | Green   | SCK or SCL         |
| SPI CE0 |   24   |  8  | Yellow  | CS                 |
| Gnd     |   9    |  -  | Black   | GND                |
| 3.3V    |   1    |  -  | Red     | Vin or Vcc or 3.3V |

### Direct Wiring

The PWM, input, output and debounce connections are M-M jumpers between T-cobbler pins.


| Test      | From RPi Pin | Number | BCM | To RPi Pin | Number | BCM | Color |
|:----------|:-------------|:------:|:---:|:-----------|:------:|:---:|:------|
| PWM       | PWM0         |   12   | 18  | GPIO       |   16   | 23  | Green | 
| Output    | GPIO         |   18   | 24  | GPIO       |   22   | 25  | Blue  |
| Input     | GPIO         |   36   | 16  | GPIO       |   37   | 26  | White |
| Debounce  | GPIO         |   15   | 22  | GPIO       |   17   | 27  | Grey  |



| Test      | From RPi Pin | Number | BCM  | To RPi Pin | Number | BCM | Color |
|:----------|:-------------|:------:|:----:|:-----------|:------:|:---:|:------|
| Debounce  | GPIO         | 32     | 12   | GPIO       |   36   | 16  | Brown |
|           |              |        |      | GPIO       |   38   | 20  | LOGIC |



12 16 20
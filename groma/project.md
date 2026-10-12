---
type: Groma Project
title: Pi4J
groma:
  profile: architecture
description: A Java library giving applications typed, structured access to a Raspberry Pi's GPIO (digital, PWM, parallel port), I2C, and SPI hardware.
---

Pi4J defines a provider-based hardware I/O API in pi4j-core and ships two interchangeable provider plugins: pi4j-plugin-ffm, which talks to real Raspberry Pi hardware through the JDK's Foreign Function & Memory API, and pi4j-plugin-mock, which simulates hardware for development and testing. pi4j-test holds smoke tests and test provider doubles that exercise the core API end to end.
